package dao;

import it.athlix.database.DatabaseConnection;
import model.ScontoBean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class ScontoDAO {

    public List<ScontoBean> findAll() throws SQLException {
        List<ScontoBean> sconti = new ArrayList<>();

        String sql = """
            SELECT ID_sconto, Percentuale, Data_inizio, Data_fine
            FROM SCONTO
            ORDER BY Data_inizio DESC, ID_sconto DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                sconti.add(mapSconto(rs));
            }
        }

        return sconti;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public ScontoBean findById(int idSconto) throws SQLException {
        if (idSconto <= 0) {
            return null;
        }

        String sql = """
            SELECT ID_sconto, Percentuale, Data_inizio, Data_fine
            FROM SCONTO
            WHERE ID_sconto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSconto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapSconto(rs) : null;
            }
        }
    }

    /* Inserisce un nuovo record nel database tramite PreparedStatement. */

    public int insert(ScontoBean sconto) throws SQLException {
        validate(sconto);

        String sql = """
            INSERT INTO SCONTO
                (Percentuale, Data_inizio, Data_fine)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setBigDecimal(1, sconto.getPercentuale());
            ps.setDate(2, sconto.getDataInizio());
            ps.setDate(3, sconto.getDataFine());

            if (ps.executeUpdate() != 1) {
                throw new SQLException("Impossibile creare lo sconto.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (!rs.next()) {
                    throw new SQLException(
                            "Impossibile recuperare l'ID dello sconto."
                    );
                }

                return rs.getInt(1);
            }
        }
    }

    /* Aggiorna i record interessati nel database. */

    public boolean update(ScontoBean sconto) throws SQLException {
        validate(sconto);

        if (sconto.getIdSconto() == null || sconto.getIdSconto() <= 0) {
            return false;
        }

        String sql = """
            UPDATE SCONTO
            SET Percentuale = ?,
                Data_inizio = ?,
                Data_fine = ?
            WHERE ID_sconto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, sconto.getPercentuale());
            ps.setDate(2, sconto.getDataInizio());
            ps.setDate(3, sconto.getDataFine());
            ps.setInt(4, sconto.getIdSconto());

            return ps.executeUpdate() > 0;
        }
    }

    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */

    public boolean deleteAndDetach(int idSconto) throws SQLException {
        if (idSconto <= 0) {
            return false;
        }

        String existsSql = """
            SELECT 1
            FROM SCONTO
            WHERE ID_sconto = ?
        """;

        String detachSql = """
            UPDATE PRODOTTO
            SET ID_sconto = NULL
            WHERE ID_sconto = ?
        """;

        String deleteSql = """
            DELETE FROM SCONTO
            WHERE ID_sconto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(existsSql)) {
                    ps.setInt(1, idSconto);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(detachSql)) {
                    ps.setInt(1, idSconto);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(deleteSql)) {
                    ps.setInt(1, idSconto);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Impossibile eliminare lo sconto.");
                    }
                }

                con.commit();
                return true;

            } catch (SQLException e) {
                try {
                    con.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }

                throw e;

            } finally {
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ignored) {
                    // La connessione verrà chiusa dal try-with-resources.
                }
            }
        }
    }

    public int countAssignedProducts(int idSconto) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS totale
            FROM PRODOTTO
            WHERE ID_sconto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idSconto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("totale") : 0;
            }
        }
    }

    /* Assegna l’associazione richiesta nel database. */

    public boolean assignToProduct(
            int idProdotto,
            Integer idSconto) throws SQLException {

        if (idProdotto <= 0) {
            return false;
        }

        if (idSconto != null && findById(idSconto) == null) {
            throw new SQLException("Sconto selezionato non valido.");
        }

        String sql = """
            UPDATE PRODOTTO
            SET ID_sconto = ?
            WHERE ID_prodotto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (idSconto == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, idSconto);
            }

            ps.setInt(2, idProdotto);

            return ps.executeUpdate() > 0;
        }
    }

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private void validate(ScontoBean sconto) throws SQLException {
        if (sconto == null) {
            throw new SQLException("Sconto non valido.");
        }

        BigDecimal percentuale = sconto.getPercentuale();
        Date dataInizio = sconto.getDataInizio();
        Date dataFine = sconto.getDataFine();

        if (percentuale == null
                || percentuale.compareTo(BigDecimal.ZERO) <= 0
                || percentuale.compareTo(new BigDecimal("100")) > 0) {

            throw new SQLException(
                    "La percentuale deve essere maggiore di 0 e non superiore a 100."
            );
        }

        if (dataInizio == null || dataFine == null) {
            throw new SQLException(
                    "Le date dello sconto sono obbligatorie."
            );
        }

        LocalDate inizio = dataInizio.toLocalDate();
        LocalDate fine = dataFine.toLocalDate();

        if (fine.isBefore(inizio)) {
            throw new SQLException(
                    "La data di fine non può precedere la data di inizio."
            );
        }
    }

    /* Converte una riga del ResultSet nel corrispondente bean di modello. */

    private ScontoBean mapSconto(ResultSet rs) throws SQLException {
        ScontoBean sconto = new ScontoBean();

        sconto.setIdSconto(
                rs.getInt("ID_sconto")
        );

        sconto.setPercentuale(
                rs.getBigDecimal("Percentuale")
        );

        sconto.setDataInizio(
                rs.getDate("Data_inizio")
        );

        sconto.setDataFine(
                rs.getDate("Data_fine")
        );

        return sconto;
    }
}

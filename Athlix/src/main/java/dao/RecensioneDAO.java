package dao;

import it.athlix.database.DatabaseConnection;
import model.RecensioneBean;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class RecensioneDAO {

    public List<RecensioneBean> findByProductId(int idProdotto)
            throws SQLException {

        List<RecensioneBean> recensioni = new ArrayList<>();

        String sql = """
            SELECT
                r.ID_recensione,
                r.Data_recensione,
                r.Voto,
                r.Commento,
                r.ID_utente,
                u.Nome_utente,
                u.Cognome_utente,
                u.Email,
                r.ID_prodotto,
                p.Nome AS Nome_prodotto
            FROM RECENSIONE r
            JOIN UTENTE u
              ON r.ID_utente = u.ID_utente
            JOIN PRODOTTO p
              ON r.ID_prodotto = p.ID_prodotto
            WHERE r.ID_prodotto = ?
            ORDER BY r.Data_recensione DESC,
                     r.ID_recensione DESC
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(mapRecensione(rs));
                }
            }
        }

        return recensioni;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public List<RecensioneBean> findAllForAdmin(String search)
            throws SQLException {

        List<RecensioneBean> recensioni = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                r.ID_recensione,
                r.Data_recensione,
                r.Voto,
                r.Commento,
                r.ID_utente,
                u.Nome_utente,
                u.Cognome_utente,
                u.Email,
                r.ID_prodotto,
                p.Nome AS Nome_prodotto
            FROM RECENSIONE r
            JOIN UTENTE u
              ON r.ID_utente = u.ID_utente
            JOIN PRODOTTO p
              ON r.ID_prodotto = p.ID_prodotto
            WHERE 1 = 1
        """);

        String normalized = search == null ? null : search.trim();

        if (normalized != null && !normalized.isBlank()) {
            sql.append("""
                 AND (
                        LOWER(p.Nome) LIKE LOWER(?)
                     OR LOWER(u.Nome_utente) LIKE LOWER(?)
                     OR LOWER(u.Cognome_utente) LIKE LOWER(?)
                     OR LOWER(u.Email) LIKE LOWER(?)
                 )
            """);
        }

        sql.append("""
             ORDER BY r.Data_recensione DESC,
                      r.ID_recensione DESC
        """);

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            if (normalized != null && !normalized.isBlank()) {
                String value = "%" + normalized + "%";

                ps.setString(1, value);
                ps.setString(2, value);
                ps.setString(3, value);
                ps.setString(4, value);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recensioni.add(mapRecensione(rs));
                }
            }
        }

        return recensioni;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public RecensioneBean findByUserAndProduct(
            int idUtente,
            int idProdotto) throws SQLException {

        String sql = """
            SELECT
                r.ID_recensione,
                r.Data_recensione,
                r.Voto,
                r.Commento,
                r.ID_utente,
                u.Nome_utente,
                u.Cognome_utente,
                u.Email,
                r.ID_prodotto,
                p.Nome AS Nome_prodotto
            FROM RECENSIONE r
            JOIN UTENTE u
              ON r.ID_utente = u.ID_utente
            JOIN PRODOTTO p
              ON r.ID_prodotto = p.ID_prodotto
            WHERE r.ID_utente = ?
              AND r.ID_prodotto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRecensione(rs) : null;
            }
        }
    }

    /* Implementa l’operazione hasPurchasedProduct usata dalla logica applicativa. */

    public boolean hasPurchasedProduct(
            int idUtente,
            int idProdotto) throws SQLException {

        String sql = """
            SELECT 1
            FROM ORDINE o
            JOIN CONTENERE c
              ON o.ID_ordine = c.ID_ordine
            WHERE o.ID_utente = ?
              AND c.ID_prodotto = ?
            LIMIT 1
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public double getAverageRating(int idProdotto) throws SQLException {
        String sql = """
            SELECT COALESCE(AVG(Voto), 0) AS media
            FROM RECENSIONE
            WHERE ID_prodotto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? rs.getDouble("media")
                        : 0.0;
            }
        }
    }

    /* Conta i record che soddisfano i criteri richiesti. */

    public int countByProductId(int idProdotto) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS totale
            FROM RECENSIONE
            WHERE ID_prodotto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProdotto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? rs.getInt("totale")
                        : 0;
            }
        }
    }

    /* Salva i dati nel database dopo la validazione. */

    public void saveOrUpdate(
            int idUtente,
            int idProdotto,
            int voto,
            String commento) throws SQLException {

        if (idUtente <= 0 || idProdotto <= 0) {
            throw new SQLException("Utente o prodotto non valido.");
        }

        validate(voto, commento);

        if (!hasPurchasedProduct(idUtente, idProdotto)) {
            throw new SQLException(
                    "Puoi recensire solo prodotti che hai acquistato."
            );
        }

        String sql = """
            INSERT INTO RECENSIONE
                (
                    Data_recensione,
                    Voto,
                    Commento,
                    ID_utente,
                    ID_prodotto
                )
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                Data_recensione = VALUES(Data_recensione),
                Voto = VALUES(Voto),
                Commento = VALUES(Commento)
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(
                    1,
                    Date.valueOf(LocalDate.now())
            );

            ps.setInt(
                    2,
                    voto
            );

            ps.setString(
                    3,
                    commento.trim()
            );

            ps.setInt(
                    4,
                    idUtente
            );

            ps.setInt(
                    5,
                    idProdotto
            );

            ps.executeUpdate();
        }
    }

    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */

    public boolean deleteByAdmin(int idRecensione) throws SQLException {
        if (idRecensione <= 0) {
            return false;
        }

        String sql = """
            DELETE FROM RECENSIONE
            WHERE ID_recensione = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(
                    1,
                    idRecensione
            );

            return ps.executeUpdate() > 0;
        }
    }

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private void validate(
            int voto,
            String commento) throws SQLException {

        if (voto < 1 || voto > 5) {
            throw new SQLException(
                    "Il voto deve essere compreso tra 1 e 5."
            );
        }

        if (commento == null
                || commento.trim().length() < 3
                || commento.trim().length() > 1000) {

            throw new SQLException(
                    "Il commento deve contenere tra 3 e 1000 caratteri."
            );
        }
    }

    /* Converte una riga del ResultSet nel corrispondente bean di modello. */

    private RecensioneBean mapRecensione(
            ResultSet rs) throws SQLException {

        RecensioneBean recensione =
                new RecensioneBean();

        recensione.setIdRecensione(
                rs.getInt("ID_recensione")
        );

        recensione.setDataRecensione(
                rs.getDate("Data_recensione")
        );

        recensione.setVoto(
                rs.getInt("Voto")
        );

        recensione.setCommento(
                rs.getString("Commento")
        );

        recensione.setIdUtente(
                rs.getInt("ID_utente")
        );

        recensione.setNomeUtente(
                rs.getString("Nome_utente")
        );

        recensione.setCognomeUtente(
                rs.getString("Cognome_utente")
        );

        recensione.setEmailUtente(
                rs.getString("Email")
        );

        recensione.setIdProdotto(
                rs.getInt("ID_prodotto")
        );

        recensione.setNomeProdotto(
                rs.getString("Nome_prodotto")
        );

        return recensione;
    }
}

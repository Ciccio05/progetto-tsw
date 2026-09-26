package dao;

import it.athlix.database.DatabaseConnection;
import model.AdminOrderBean;
import model.AdminOrderItemBean;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class AdminOrderDAO {

    private static final Set<String> STATI_ORDINE_CONSENTITI = Set.of(
            "CONFERMATO",
            "IN LAVORAZIONE",
            "SPEDITO",
            "CONSEGNATO"
    );

    private static final Set<String> STATI_ANNULLABILI = Set.of(
            "CONFERMATO",
            "IN LAVORAZIONE"
    );

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public List<AdminOrderBean> findFiltered(
            LocalDate dataDa,
            LocalDate dataA,
            String cliente) throws SQLException {

        List<AdminOrderBean> ordini = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT
                o.ID_ordine,
                o.Data_ordine,
                o.Stato_o,
                o.Totale,
                u.ID_utente,
                u.Nome_utente,
                u.Cognome_utente,
                u.Email,
                p.ID_pagamento,
                p.Stato AS Stato_pagamento,
                p.Data_pagamento,
                p.ID_metodo,
                s.Indirizzo,
                s.Stato_s,
                s.Data_partenza,
                s.Data_consegna
            FROM ORDINE o
            JOIN UTENTE u ON o.ID_utente = u.ID_utente
            JOIN PAGAMENTO p ON o.ID_pagamento = p.ID_pagamento
            LEFT JOIN SPEDIZIONE s ON o.ID_ordine = s.ID_ordine
            WHERE 1 = 1
        """);

        List<Object> parameters = new ArrayList<>();

        if (dataDa != null) {
            sql.append(" AND o.Data_ordine >= ?");
            parameters.add(Date.valueOf(dataDa));
        }

        if (dataA != null) {
            sql.append(" AND o.Data_ordine <= ?");
            parameters.add(Date.valueOf(dataA));
        }

        String clienteNormalizzato = cliente == null
                ? null
                : cliente.trim().toLowerCase(Locale.ROOT);

        if (clienteNormalizzato != null && !clienteNormalizzato.isEmpty()) {
            sql.append("""
                 AND (
                        LOWER(u.Email) LIKE ?
                     OR LOWER(u.Nome_utente) LIKE ?
                     OR LOWER(u.Cognome_utente) LIKE ?
                     OR CAST(u.ID_utente AS CHAR) LIKE ?
                 )
            """);

            String ricerca = "%" + clienteNormalizzato + "%";
            parameters.add(ricerca);
            parameters.add(ricerca);
            parameters.add(ricerca);
            parameters.add(ricerca);
        }

        sql.append(" ORDER BY o.Data_ordine DESC, o.ID_ordine DESC");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                Object value = parameters.get(i);
                if (value instanceof Date date) {
                    ps.setDate(i + 1, date);
                } else {
                    ps.setString(i + 1, value.toString());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordini.add(mapOrder(rs));
                }
            }
        }

        return ordini;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public AdminOrderBean findById(int idOrdine) throws SQLException {
        if (idOrdine <= 0) {
            return null;
        }

        String sql = """
            SELECT
                o.ID_ordine,
                o.Data_ordine,
                o.Stato_o,
                o.Totale,
                u.ID_utente,
                u.Nome_utente,
                u.Cognome_utente,
                u.Email,
                p.ID_pagamento,
                p.Stato AS Stato_pagamento,
                p.Data_pagamento,
                p.ID_metodo,
                s.Indirizzo,
                s.Stato_s,
                s.Data_partenza,
                s.Data_consegna
            FROM ORDINE o
            JOIN UTENTE u ON o.ID_utente = u.ID_utente
            JOIN PAGAMENTO p ON o.ID_pagamento = p.ID_pagamento
            LEFT JOIN SPEDIZIONE s ON o.ID_ordine = s.ID_ordine
            WHERE o.ID_ordine = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapOrder(rs) : null;
            }
        }
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public List<AdminOrderItemBean> findItemsByOrderId(int idOrdine) throws SQLException {
        List<AdminOrderItemBean> items = new ArrayList<>();

        String sql = """
            SELECT
                c.ID_prodotto,
                p.Nome,
                c.Quantita_ordine,
                c.Prezzo_acquisto,
                c.IVA_acquisto
            FROM CONTENERE c
            JOIN PRODOTTO p ON c.ID_prodotto = p.ID_prodotto
            WHERE c.ID_ordine = ?
            ORDER BY p.Nome
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idOrdine);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdminOrderItemBean item = new AdminOrderItemBean();
                    item.setIdProdotto(rs.getInt("ID_prodotto"));
                    item.setNomeProdotto(rs.getString("Nome"));
                    item.setQuantita(rs.getInt("Quantita_ordine"));
                    item.setPrezzoAcquisto(rs.getBigDecimal("Prezzo_acquisto"));
                    item.setIvaAcquisto(rs.getBigDecimal("IVA_acquisto"));
                    items.add(item);
                }
            }
        }

        return items;
    }

    /* Aggiorna i record interessati nel database. */

    public void updateWorkflow(int idOrdine, String nuovoStato) throws SQLException {
        if (idOrdine <= 0) {
            throw new SQLException("Ordine non valido.");
        }

        String stato = normalizeStatus(nuovoStato);
        if (!STATI_ORDINE_CONSENTITI.contains(stato)) {
            throw new SQLException("Stato ordine non valido.");
        }

        String sqlLockOrdine = """
            SELECT Stato_o
            FROM ORDINE
            WHERE ID_ordine = ?
            FOR UPDATE
        """;

        String sqlOrdine = """
            UPDATE ORDINE
            SET Stato_o = ?
            WHERE ID_ordine = ?
        """;

        String sqlSpedizionePreparazione = """
            UPDATE SPEDIZIONE
            SET Stato_s = 'IN PREPARAZIONE',
                Data_partenza = NULL,
                Data_consegna = NULL
            WHERE ID_ordine = ?
        """;

        String sqlSpedizioneSpedita = """
            UPDATE SPEDIZIONE
            SET Stato_s = 'SPEDITA',
                Data_partenza = COALESCE(Data_partenza, CURDATE()),
                Data_consegna = NULL
            WHERE ID_ordine = ?
        """;

        String sqlSpedizioneConsegnata = """
            UPDATE SPEDIZIONE
            SET Stato_s = 'CONSEGNATA',
                Data_partenza = COALESCE(Data_partenza, CURDATE()),
                Data_consegna = COALESCE(Data_consegna, CURDATE())
            WHERE ID_ordine = ?
        """;

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String statoAttuale;

                try (PreparedStatement psLock = con.prepareStatement(sqlLockOrdine)) {
                    psLock.setInt(1, idOrdine);
                    try (ResultSet rs = psLock.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Ordine non trovato.");
                        }
                        statoAttuale = normalizeStatus(rs.getString("Stato_o"));
                    }
                }

                if ("ANNULLATO".equals(statoAttuale)) {
                    throw new SQLException("Un ordine annullato non può cambiare stato.");
                }

                if (!isTransitionAllowed(statoAttuale, stato)) {
                    throw new SQLException(
                            "Passaggio di stato non consentito da "
                                    + statoAttuale + " a " + stato + "."
                    );
                }

                try (PreparedStatement psOrder = con.prepareStatement(sqlOrdine)) {
                    psOrder.setString(1, stato);
                    psOrder.setInt(2, idOrdine);
                    if (psOrder.executeUpdate() != 1) {
                        throw new SQLException("Ordine non trovato.");
                    }
                }

                String sqlSpedizione;
                if ("SPEDITO".equals(stato)) {
                    sqlSpedizione = sqlSpedizioneSpedita;
                } else if ("CONSEGNATO".equals(stato)) {
                    sqlSpedizione = sqlSpedizioneConsegnata;
                } else {
                    sqlSpedizione = sqlSpedizionePreparazione;
                }

                try (PreparedStatement psShipment = con.prepareStatement(sqlSpedizione)) {
                    psShipment.setInt(1, idOrdine);
                    if (psShipment.executeUpdate() != 1) {
                        throw new SQLException("Spedizione associata all'ordine non trovata.");
                    }
                }

                con.commit();

            } catch (SQLException e) {
                rollbackQuietly(con, e);
                throw e;
            } finally {
                restoreAutoCommit(con);
            }
        }
    }

    /**
     * Annulla un ordine solo se è ancora CONFERMATO o IN LAVORAZIONE.
     * La transazione aggiorna ordine/spedizione/pagamento e restituisce lo stock.
     */
    public void annullaOrdine(int idOrdine) throws SQLException {
        if (idOrdine <= 0) {
            throw new SQLException("Ordine non valido.");
        }

        String sqlLockOrdine = """
            SELECT Stato_o, ID_pagamento
            FROM ORDINE
            WHERE ID_ordine = ?
            FOR UPDATE
        """;

        String sqlRigheOrdine = """
            SELECT ID_prodotto, Quantita_ordine
            FROM CONTENERE
            WHERE ID_ordine = ?
        """;

        String sqlRipristinaStock = """
            UPDATE PRODOTTO
            SET Quantita = Quantita + ?,
                Disponibilita = TRUE
            WHERE ID_prodotto = ?
        """;

        String sqlAnnullaOrdine = """
            UPDATE ORDINE
            SET Stato_o = 'ANNULLATO'
            WHERE ID_ordine = ?
        """;

        String sqlAnnullaSpedizione = """
            UPDATE SPEDIZIONE
            SET Stato_s = 'ANNULLATA',
                Data_partenza = NULL,
                Data_consegna = NULL
            WHERE ID_ordine = ?
        """;

        String sqlAggiornaPagamento = """
            UPDATE PAGAMENTO
            SET Stato = CASE
                WHEN UPPER(Stato) = 'COMPLETATO' THEN 'RIMBORSATO'
                ELSE 'ANNULLATO'
            END
            WHERE ID_pagamento = ?
        """;

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                String statoAttuale;
                int idPagamento;

                try (PreparedStatement psLock = con.prepareStatement(sqlLockOrdine)) {
                    psLock.setInt(1, idOrdine);
                    try (ResultSet rs = psLock.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Ordine non trovato.");
                        }
                        statoAttuale = normalizeStatus(rs.getString("Stato_o"));
                        idPagamento = rs.getInt("ID_pagamento");
                    }
                }

                if ("ANNULLATO".equals(statoAttuale)) {
                    throw new SQLException("L'ordine è già annullato.");
                }

                if (!STATI_ANNULLABILI.contains(statoAttuale)) {
                    throw new SQLException(
                            "È possibile annullare soltanto ordini CONFERMATI o IN LAVORAZIONE."
                    );
                }

                List<int[]> righeOrdine = new ArrayList<>();
                try (PreparedStatement psItems = con.prepareStatement(sqlRigheOrdine)) {
                    psItems.setInt(1, idOrdine);
                    try (ResultSet rs = psItems.executeQuery()) {
                        while (rs.next()) {
                            righeOrdine.add(new int[]{
                                    rs.getInt("ID_prodotto"),
                                    rs.getInt("Quantita_ordine")
                            });
                        }
                    }
                }

                if (righeOrdine.isEmpty()) {
                    throw new SQLException("L'ordine non contiene prodotti: annullamento interrotto.");
                }

                try (PreparedStatement psStock = con.prepareStatement(sqlRipristinaStock)) {
                    for (int[] riga : righeOrdine) {
                        if (riga[1] <= 0) {
                            throw new SQLException("Quantità non valida nell'ordine.");
                        }
                        psStock.setInt(1, riga[1]);
                        psStock.setInt(2, riga[0]);
                        if (psStock.executeUpdate() != 1) {
                            throw new SQLException("Impossibile ripristinare lo stock del prodotto #" + riga[0] + ".");
                        }
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlAnnullaOrdine)) {
                    ps.setInt(1, idOrdine);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Impossibile annullare l'ordine.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlAnnullaSpedizione)) {
                    ps.setInt(1, idOrdine);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Spedizione associata all'ordine non trovata.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(sqlAggiornaPagamento)) {
                    ps.setInt(1, idPagamento);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Pagamento associato all'ordine non trovato.");
                    }
                }

                con.commit();

            } catch (SQLException e) {
                rollbackQuietly(con, e);
                throw e;
            } finally {
                restoreAutoCommit(con);
            }
        }
    }

    private boolean isTransitionAllowed(String statoAttuale, String nuovoStato) {
        if (statoAttuale.equals(nuovoStato)) {
            return true;
        }

        return switch (statoAttuale) {
            case "CONFERMATO" -> "IN LAVORAZIONE".equals(nuovoStato);
            case "IN LAVORAZIONE" -> "SPEDITO".equals(nuovoStato);
            case "SPEDITO" -> "CONSEGNATO".equals(nuovoStato);
            default -> false;
        };
    }

    /* Normalizza il valore ricevuto prima di usarlo nella logica applicativa. */

    private String normalizeStatus(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    /* Esegue il rollback in sicurezza in caso di errore transazionale. */

    private void rollbackQuietly(Connection con, SQLException original) {
        try {
            con.rollback();
        } catch (SQLException rollbackException) {
            original.addSuppressed(rollbackException);
        }
    }

    /* Ripristina lo stato della connessione dopo la transazione. */

    private void restoreAutoCommit(Connection con) {
        try {
            con.setAutoCommit(true);
        } catch (SQLException ignored) {
            // La connessione viene comunque chiusa dal try-with-resources.
        }
    }

    private AdminOrderBean mapOrder(ResultSet rs) throws SQLException {
        AdminOrderBean ordine = new AdminOrderBean();

        ordine.setIdOrdine(rs.getInt("ID_ordine"));
        ordine.setDataOrdine(rs.getDate("Data_ordine"));
        ordine.setStato(rs.getString("Stato_o"));
        ordine.setTotale(rs.getBigDecimal("Totale"));
        ordine.setIdUtente(rs.getInt("ID_utente"));
        ordine.setNomeUtente(rs.getString("Nome_utente"));
        ordine.setCognomeUtente(rs.getString("Cognome_utente"));
        ordine.setEmail(rs.getString("Email"));
        ordine.setIdPagamento(rs.getInt("ID_pagamento"));
        ordine.setStatoPagamento(rs.getString("Stato_pagamento"));
        ordine.setDataPagamento(rs.getDate("Data_pagamento"));
        ordine.setIdMetodoPagamento(rs.getInt("ID_metodo"));
        ordine.setIndirizzoSpedizione(rs.getString("Indirizzo"));
        ordine.setStatoSpedizione(rs.getString("Stato_s"));
        ordine.setDataPartenza(rs.getDate("Data_partenza"));
        ordine.setDataConsegna(rs.getDate("Data_consegna"));

        return ordine;
    }
}

package dao;

import it.athlix.database.DatabaseConnection;
import model.CartItem;
import model.OrdineBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class OrderDAO {

    public int creaOrdine(
            int idUtente,
            int idMetodoPagamento,
            List<CartItem> cartItems,
            String indirizzo,
            String citta,
            String cap) throws SQLException {

        if (idUtente <= 0) {
            throw new SQLException("Utente non valido.");
        }

        if (cartItems == null || cartItems.isEmpty()) {
            throw new SQLException(
                    "Impossibile creare un ordine con carrello vuoto."
            );
        }

        BigDecimal totale = calcolaTotale(cartItems);

        String sqlMetodo = """
            SELECT 1
            FROM METODO_PAGAMENTO
            WHERE ID_metodo = ?
        """;

        String sqlPagamento = """
            INSERT INTO PAGAMENTO
                (Stato, Data_pagamento, ID_metodo)
            VALUES (?, ?, ?)
        """;

        String sqlOrdine = """
            INSERT INTO ORDINE
                (Data_ordine, Stato_o, Totale, ID_utente, ID_pagamento)
            VALUES (?, ?, ?, ?, ?)
        """;

        String sqlContenere = """
            INSERT INTO CONTENERE
                (
                    ID_ordine,
                    ID_prodotto,
                    Quantita_ordine,
                    Prezzo_acquisto,
                    IVA_acquisto
                )
            VALUES (?, ?, ?, ?, ?)
        """;

        String sqlAggiornaStock = """
            UPDATE PRODOTTO
            SET Quantita = Quantita - ?
            WHERE ID_prodotto = ?
              AND Visibile = TRUE
              AND Disponibilita = TRUE
              AND Quantita >= ?
        """;

        String sqlAggiornaDisponibilita = """
            UPDATE PRODOTTO
            SET Disponibilita = (Quantita > 0)
            WHERE ID_prodotto = ?
        """;

        String sqlSpedizione = """
            INSERT INTO SPEDIZIONE
                (
                    Indirizzo,
                    Stato_s,
                    Data_partenza,
                    Data_consegna,
                    ID_ordine
                )
            VALUES (?, ?, ?, ?, ?)
        """;

        String sqlSvuotaCarrello = """
            DELETE i
            FROM INCLUDERE i
            JOIN CARRELLO c
              ON i.ID_carrello = c.ID_carrello
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
        """;

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {

                verificaMetodoPagamento(
                        con,
                        sqlMetodo,
                        idMetodoPagamento
                );

                int idPagamento = creaPagamento(
                        con,
                        sqlPagamento,
                        idMetodoPagamento
                );

                int idOrdine = creaRecordOrdine(
                        con,
                        sqlOrdine,
                        idUtente,
                        idPagamento,
                        totale
                );

                try (
                        PreparedStatement psCont =
                                con.prepareStatement(sqlContenere);

                        PreparedStatement psStock =
                                con.prepareStatement(sqlAggiornaStock);

                        PreparedStatement psDisponibilita =
                                con.prepareStatement(sqlAggiornaDisponibilita)
                ) {

                    for (CartItem item : cartItems) {

                        if (item == null
                                || item.getIdProdotto() <= 0
                                || item.getQuantita() <= 0
                                || item.getPrezzo() == null
                                || item.getIva() == null) {

                            throw new SQLException(
                                    "Dati del prodotto non validi nell'ordine."
                            );
                        }

                        BigDecimal prezzoAcquisto =
                                item.getPrezzoScontato()
                                        .setScale(
                                                2,
                                                RoundingMode.HALF_UP
                                        );

                        psCont.setInt(
                                1,
                                idOrdine
                        );

                        psCont.setInt(
                                2,
                                item.getIdProdotto()
                        );

                        psCont.setInt(
                                3,
                                item.getQuantita()
                        );

                        psCont.setBigDecimal(
                                4,
                                prezzoAcquisto
                        );

                        psCont.setBigDecimal(
                                5,
                                item.getIva()
                        );

                        psCont.executeUpdate();

                        psStock.setInt(
                                1,
                                item.getQuantita()
                        );

                        psStock.setInt(
                                2,
                                item.getIdProdotto()
                        );

                        psStock.setInt(
                                3,
                                item.getQuantita()
                        );

                        if (psStock.executeUpdate() == 0) {
                            throw new SQLException(
                                    "Quantità non disponibile per il prodotto ID "
                                            + item.getIdProdotto()
                            );
                        }

                        psDisponibilita.setInt(
                                1,
                                item.getIdProdotto()
                        );

                        psDisponibilita.executeUpdate();
                    }
                }

                try (
                        PreparedStatement psSpedizione =
                                con.prepareStatement(sqlSpedizione)
                ) {

                    String indirizzoCompleto =
                            indirizzo
                                    + ", "
                                    + citta
                                    + " "
                                    + cap;

                    psSpedizione.setString(
                            1,
                            indirizzoCompleto
                    );

                    psSpedizione.setString(
                            2,
                            "IN PREPARAZIONE"
                    );

                    psSpedizione.setNull(
                            3,
                            Types.DATE
                    );

                    psSpedizione.setNull(
                            4,
                            Types.DATE
                    );

                    psSpedizione.setInt(
                            5,
                            idOrdine
                    );

                    psSpedizione.executeUpdate();
                }

                try (
                        PreparedStatement psClear =
                                con.prepareStatement(sqlSvuotaCarrello)
                ) {

                    psClear.setInt(
                            1,
                            idUtente
                    );

                    psClear.executeUpdate();
                }

                con.commit();

                return idOrdine;

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

    public List<OrdineBean> findByUserId(int idUtente)
            throws SQLException {

        List<OrdineBean> ordini =
                new ArrayList<>();

        String sql = """
            SELECT
                o.ID_ordine,
                o.Data_ordine,
                o.Stato_o,
                o.Totale
            FROM ORDINE o
            WHERE o.ID_utente = ?
            ORDER BY o.Data_ordine DESC,
                     o.ID_ordine DESC
        """;

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idUtente
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {
                    ordini.add(
                            mapOrdineBase(rs)
                    );
                }
            }
        }

        return ordini;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public OrdineBean findByIdAndUserId(
            int idOrdine,
            int idUtente) throws SQLException {

        if (idOrdine <= 0 || idUtente <= 0) {
            return null;
        }

        String sql = """
            SELECT
                o.ID_ordine,
                o.Data_ordine,
                o.Stato_o,
                o.Totale,
                s.Indirizzo,
                s.Stato_s,
                s.Data_partenza,
                s.Data_consegna
            FROM ORDINE o
            LEFT JOIN SPEDIZIONE s
              ON o.ID_ordine = s.ID_ordine
            WHERE o.ID_ordine = ?
              AND o.ID_utente = ?
        """;

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idOrdine
            );

            ps.setInt(
                    2,
                    idUtente
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {
                    return mapOrdineConSpedizione(rs);
                }
            }
        }

        return null;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public List<CartItem> findItemsByOrderIdAndUserId(
            int idOrdine,
            int idUtente) throws SQLException {

        List<CartItem> items =
                new ArrayList<>();

        String sql = """
            SELECT
                c.ID_prodotto,
                p.Nome,
                c.Quantita_ordine,
                c.Prezzo_acquisto,
                c.IVA_acquisto
            FROM CONTENERE c
            JOIN ORDINE o
              ON c.ID_ordine = o.ID_ordine
            JOIN PRODOTTO p
              ON c.ID_prodotto = p.ID_prodotto
            WHERE c.ID_ordine = ?
              AND o.ID_utente = ?
            ORDER BY p.Nome
        """;

        try (
                Connection con =
                        DatabaseConnection.getConnection();

                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idOrdine
            );

            ps.setInt(
                    2,
                    idUtente
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    CartItem item =
                            new CartItem();

                    item.setIdProdotto(
                            rs.getInt("ID_prodotto")
                    );

                    item.setNomeProdotto(
                            rs.getString("Nome")
                    );

                    item.setQuantita(
                            rs.getInt("Quantita_ordine")
                    );

                    item.setPrezzo(
                            rs.getBigDecimal("Prezzo_acquisto")
                    );

                    item.setIva(
                            rs.getBigDecimal("IVA_acquisto")
                    );

                    item.setPercentualeSconto(
                            BigDecimal.ZERO
                    );

                    items.add(item);
                }
            }
        }

        return items;
    }

    /* Converte una riga del ResultSet nel corrispondente bean di modello. */

    private OrdineBean mapOrdineBase(
            ResultSet rs) throws SQLException {

        OrdineBean ordine =
                new OrdineBean();

        ordine.setIdOrdine(
                rs.getInt("ID_ordine")
        );

        ordine.setDataOrdine(
                rs.getDate("Data_ordine")
        );

        ordine.setStato(
                rs.getString("Stato_o")
        );

        ordine.setTotale(
                rs.getBigDecimal("Totale")
        );

        return ordine;
    }

    /* Converte una riga del ResultSet nel corrispondente bean di modello. */

    private OrdineBean mapOrdineConSpedizione(
            ResultSet rs) throws SQLException {

        OrdineBean ordine =
                mapOrdineBase(rs);

        ordine.setIndirizzoSpedizione(
                rs.getString("Indirizzo")
        );

        ordine.setStatoSpedizione(
                rs.getString("Stato_s")
        );

        ordine.setDataPartenza(
                rs.getDate("Data_partenza")
        );

        ordine.setDataConsegna(
                rs.getDate("Data_consegna")
        );

        return ordine;
    }

    /* Calcola il valore richiesto usando i dati persistenti. */

    private BigDecimal calcolaTotale(
            List<CartItem> cartItems)
            throws SQLException {

        BigDecimal totale =
                BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            if (item == null
                    || item.getQuantita() <= 0
                    || item.getPrezzo() == null) {

                throw new SQLException(
                        "Elemento del carrello non valido."
                );
            }

            BigDecimal subtotale =
                    item.getSubtotale();

            if (subtotale == null) {

                throw new SQLException(
                        "Subtotale del prodotto non valido."
                );
            }

            totale =
                    totale.add(subtotale);
        }

        return totale.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    /* Implementa l’operazione verificaMetodoPagamento usata dalla logica applicativa. */

    private void verificaMetodoPagamento(
            Connection con,
            String sql,
            int idMetodoPagamento)
            throws SQLException {

        if (idMetodoPagamento <= 0) {
            throw new SQLException(
                    "Metodo di pagamento non valido."
            );
        }

        try (
                PreparedStatement ps =
                        con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idMetodoPagamento
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Metodo di pagamento non valido."
                    );
                }
            }
        }
    }

    /* Crea e persiste il nuovo record necessario all’operazione. */

    private int creaPagamento(
            Connection con,
            String sql,
            int idMetodoPagamento)
            throws SQLException {

        try (
                PreparedStatement ps =
                        con.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setString(
                    1,
                    "COMPLETATO"
            );

            ps.setDate(
                    2,
                    Date.valueOf(LocalDate.now())
            );

            ps.setInt(
                    3,
                    idMetodoPagamento
            );

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Impossibile creare il pagamento."
                );
            }

            try (
                    ResultSet rs =
                            ps.getGeneratedKeys()
            ) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Impossibile recuperare l'ID del pagamento."
                    );
                }

                return rs.getInt(1);
            }
        }
    }

    /* Crea e persiste il nuovo record necessario all’operazione. */

    private int creaRecordOrdine(
            Connection con,
            String sql,
            int idUtente,
            int idPagamento,
            BigDecimal totale)
            throws SQLException {

        try (
                PreparedStatement ps =
                        con.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setDate(
                    1,
                    Date.valueOf(LocalDate.now())
            );

            ps.setString(
                    2,
                    "CONFERMATO"
            );

            ps.setBigDecimal(
                    3,
                    totale.setScale(
                            2,
                            RoundingMode.HALF_UP
                    )
            );

            ps.setInt(
                    4,
                    idUtente
            );

            ps.setInt(
                    5,
                    idPagamento
            );

            if (ps.executeUpdate() != 1) {
                throw new SQLException(
                        "Impossibile creare l'ordine."
                );
            }

            try (
                    ResultSet rs =
                            ps.getGeneratedKeys()
            ) {

                if (!rs.next()) {
                    throw new SQLException(
                            "Impossibile recuperare l'ID dell'ordine."
                    );
                }

                return rs.getInt(1);
            }
        }
    }
}

package dao;

import it.athlix.database.DatabaseConnection;
import model.CartItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class CartDAO {

    public int getOrCreateCartIdByUser(int idUtente) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()) {
            return getOrCreateCartIdByUser(con, idUtente);
        }
    }

    private int getOrCreateCartIdByUser(Connection con, int idUtente) throws SQLException {
        String selectSql = """
            SELECT ID_carrello
            FROM CARRELLO
            WHERE ID_utente = ? AND Stato = 'ATTIVO'
            ORDER BY ID_carrello DESC
            LIMIT 1
        """;

        String insertSql = """
            INSERT INTO CARRELLO (Data_creazione, Stato, ID_utente)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement psSelect = con.prepareStatement(selectSql)) {
            psSelect.setInt(1, idUtente);
            try (ResultSet rs = psSelect.executeQuery()) {
                if (rs.next()) return rs.getInt("ID_carrello");
            }
        }

        try (PreparedStatement psInsert = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setDate(1, Date.valueOf(LocalDate.now()));
            psInsert.setString(2, "ATTIVO");
            psInsert.setInt(3, idUtente);
            psInsert.executeUpdate();

            try (ResultSet rsKeys = psInsert.getGeneratedKeys()) {
                if (rsKeys.next()) return rsKeys.getInt(1);
            }
        }

        throw new SQLException("Impossibile creare o recuperare il carrello.");
    }

    public List<CartItem> getCartItemsByUserId(int idUtente) throws SQLException {
        List<CartItem> items = new ArrayList<>();

        String sql = """
            SELECT c.ID_carrello, p.ID_prodotto, p.Nome, p.Immagine, p.Prezzo, p.IVA,
                   i.Quantita_ordine,
                   CASE
                       WHEN p.Disponibilita = TRUE
                        AND p.Quantita > 0
                        AND s.ID_sconto IS NOT NULL
                        AND CURDATE() BETWEEN s.Data_inizio AND s.Data_fine
                       THEN s.Percentuale
                       ELSE 0
                   END AS Percentuale
            FROM CARRELLO c
            JOIN INCLUDERE i ON c.ID_carrello = i.ID_carrello
            JOIN PRODOTTO p ON i.ID_prodotto = p.ID_prodotto
            LEFT JOIN SCONTO s ON p.ID_sconto = s.ID_sconto
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
            ORDER BY p.Nome
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUtente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setIdCarrello(rs.getInt("ID_carrello"));
                    item.setIdProdotto(rs.getInt("ID_prodotto"));
                    item.setNomeProdotto(rs.getString("Nome"));
                    item.setImmagine(rs.getString("Immagine"));
                    item.setPrezzo(rs.getBigDecimal("Prezzo"));
                    item.setIva(rs.getBigDecimal("IVA"));
                    item.setQuantita(rs.getInt("Quantita_ordine"));
                    item.setPercentualeSconto(rs.getBigDecimal("Percentuale"));
                    items.add(item);
                }
            }
        }

        return items;
    }

    /* Aggiunge l’elemento richiesto verificando prima i vincoli applicativi. */

    public boolean addProductToCart(int idUtente, int idProdotto, int quantitaDaAggiungere)
            throws SQLException {
        if (quantitaDaAggiungere <= 0) return false;

        String productSql = "SELECT Quantita, Disponibilita FROM PRODOTTO WHERE ID_prodotto = ? AND Visibile = TRUE";
        String cartQuantitySql = "SELECT Quantita_ordine FROM INCLUDERE WHERE ID_carrello = ? AND ID_prodotto = ?";
        String insertSql = "INSERT INTO INCLUDERE (ID_carrello, ID_prodotto, Quantita_ordine) VALUES (?, ?, ?)";
        String updateSql = "UPDATE INCLUDERE SET Quantita_ordine = ? WHERE ID_carrello = ? AND ID_prodotto = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int stock;
                boolean disponibile;

                try (PreparedStatement psProduct = con.prepareStatement(productSql)) {
                    psProduct.setInt(1, idProdotto);
                    try (ResultSet rs = psProduct.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }
                        stock = rs.getInt("Quantita");
                        disponibile = rs.getBoolean("Disponibilita");
                    }
                }

                if (!disponibile || stock <= 0) {
                    con.rollback();
                    return false;
                }

                int idCarrello = getOrCreateCartIdByUser(con, idUtente);
                int quantitaAttuale = 0;
                boolean giaPresente = false;

                try (PreparedStatement psCart = con.prepareStatement(cartQuantitySql)) {
                    psCart.setInt(1, idCarrello);
                    psCart.setInt(2, idProdotto);
                    try (ResultSet rs = psCart.executeQuery()) {
                        if (rs.next()) {
                            giaPresente = true;
                            quantitaAttuale = rs.getInt("Quantita_ordine");
                        }
                    }
                }

                int nuovaQuantita = quantitaAttuale + quantitaDaAggiungere;
                if (nuovaQuantita > stock) {
                    con.rollback();
                    return false;
                }

                if (giaPresente) {
                    try (PreparedStatement psUpdate = con.prepareStatement(updateSql)) {
                        psUpdate.setInt(1, nuovaQuantita);
                        psUpdate.setInt(2, idCarrello);
                        psUpdate.setInt(3, idProdotto);
                        psUpdate.executeUpdate();
                    }
                } else {
                    try (PreparedStatement psInsert = con.prepareStatement(insertSql)) {
                        psInsert.setInt(1, idCarrello);
                        psInsert.setInt(2, idProdotto);
                        psInsert.setInt(3, quantitaDaAggiungere);
                        psInsert.executeUpdate();
                    }
                }

                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    /* Incrementa la quantità rispettando disponibilità e stock. */

    public boolean increaseQuantity(int idUtente, int idProdotto) throws SQLException {
        String sql = """
            UPDATE INCLUDERE i
            JOIN CARRELLO c ON i.ID_carrello = c.ID_carrello
            JOIN PRODOTTO p ON p.ID_prodotto = i.ID_prodotto
            SET i.Quantita_ordine = i.Quantita_ordine + 1
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
              AND i.ID_prodotto = ?
              AND p.Visibile = TRUE
              AND p.Disponibilita = TRUE
              AND i.Quantita_ordine < p.Quantita
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            return ps.executeUpdate() > 0;
        }
    }

    /* Riduce la quantità senza portarla sotto il minimo consentito. */

    public boolean decreaseQuantity(int idUtente, int idProdotto) throws SQLException {
        String sql = """
            UPDATE INCLUDERE i
            JOIN CARRELLO c ON i.ID_carrello = c.ID_carrello
            SET i.Quantita_ordine = i.Quantita_ordine - 1
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
              AND i.ID_prodotto = ?
              AND i.Quantita_ordine > 1
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            return ps.executeUpdate() > 0;
        }
    }

    /* Rimuove l’elemento richiesto dai dati persistenti. */

    public boolean removeProductFromCart(int idUtente, int idProdotto) throws SQLException {
        String sql = """
            DELETE i
            FROM INCLUDERE i
            JOIN CARRELLO c ON i.ID_carrello = c.ID_carrello
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
              AND i.ID_prodotto = ?
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.setInt(2, idProdotto);
            return ps.executeUpdate() > 0;
        }
    }

    /* Calcola il valore richiesto usando i dati persistenti. */

    public BigDecimal calculateSubtotal(int idUtente) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(
                ROUND(
                    p.Prezzo *
                    (1 - (
                        CASE
                            WHEN p.Disponibilita = TRUE
                             AND p.Quantita > 0
                             AND s.ID_sconto IS NOT NULL
                             AND CURDATE() BETWEEN s.Data_inizio AND s.Data_fine
                            THEN s.Percentuale
                            ELSE 0
                        END
                    ) / 100),
                    2
                ) * i.Quantita_ordine
            ), 0) AS subtotale
            FROM CARRELLO c
            LEFT JOIN INCLUDERE i ON c.ID_carrello = i.ID_carrello
            LEFT JOIN PRODOTTO p ON i.ID_prodotto = p.ID_prodotto
            LEFT JOIN SCONTO s ON p.ID_sconto = s.ID_sconto
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal subtotal = rs.getBigDecimal("subtotale");
                    return subtotal != null ? subtotal : BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /* Conta i record che soddisfano i criteri richiesti. */

    public int countDistinctProducts(int idUtente) throws SQLException {
        String sql = """
            SELECT COUNT(*) AS totale_prodotti
            FROM CARRELLO c
            JOIN INCLUDERE i ON c.ID_carrello = i.ID_carrello
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("totale_prodotti") : 0;
            }
        }
    }

    /* Conta i record che soddisfano i criteri richiesti. */

    public int countTotalPieces(int idUtente) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(i.Quantita_ordine), 0) AS totale_pezzi
            FROM CARRELLO c
            LEFT JOIN INCLUDERE i ON c.ID_carrello = i.ID_carrello
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("totale_pezzi") : 0;
            }
        }
    }

    /* Svuota i dati associati all’operazione corrente. */

    public boolean clearCart(int idUtente) throws SQLException {
        String sql = """
            DELETE i
            FROM INCLUDERE i
            JOIN CARRELLO c ON i.ID_carrello = c.ID_carrello
            WHERE c.ID_utente = ?
              AND c.Stato = 'ATTIVO'
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUtente);
            ps.executeUpdate();
            return true;
        }
    }
}

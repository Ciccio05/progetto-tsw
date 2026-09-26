package dao;

import it.athlix.database.DatabaseConnection;
import model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Savepoint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class ProductDAO {

    public enum DeleteResult {
        DELETED,
        DISABLED,
        NOT_FOUND
    }

    /*
     * Query base utilizzata per leggere i prodotti.
     *
     * IMPORTANTE:
     * lo sconto viene applicato soltanto se il prodotto:
     *
     * - è disponibile;
     * - ha quantità maggiore di zero;
     * - possiede uno sconto;
     * - lo sconto è attivo nella data corrente.
     *
     * Se il prodotto non è disponibile, il prezzo finale
     * coincide quindi con il prezzo normale.
     */
    private static final String SELECT_BASE = """
        SELECT
            p.ID_prodotto,
            p.Nome,
            p.Prezzo,
            p.IVA,
            p.Descrizione,
            p.Immagine,
            p.Visibile,
            p.Disponibilita,
            p.Quantita,
            p.ID_categoria,
            p.ID_sconto,

            c.Nome_categoria,

            CASE
                WHEN p.Disponibilita = TRUE
                 AND p.Quantita > 0
                 AND s.ID_sconto IS NOT NULL
                 AND CURDATE()
                     BETWEEN s.Data_inizio
                         AND s.Data_fine
                THEN s.Percentuale

                ELSE 0
            END AS Percentuale_sconto,

            ROUND(
                p.Prezzo *
                (
                    1 - (
                        CASE
                            WHEN p.Disponibilita = TRUE
                             AND p.Quantita > 0
                             AND s.ID_sconto IS NOT NULL
                             AND CURDATE()
                                 BETWEEN s.Data_inizio
                                     AND s.Data_fine
                            THEN s.Percentuale

                            ELSE 0
                        END
                    ) / 100
                ),
                2
            ) AS Prezzo_finale,

            COALESCE(
                (
                    SELECT ROUND(
                        AVG(r.Voto),
                        1
                    )
                    FROM RECENSIONE r
                    WHERE r.ID_prodotto =
                          p.ID_prodotto
                ),
                0
            ) AS Media_recensioni,

            (
                SELECT COUNT(*)
                FROM RECENSIONE r
                WHERE r.ID_prodotto =
                      p.ID_prodotto
            ) AS Numero_recensioni

        FROM PRODOTTO p

        JOIN CATEGORIA_SPORT c
            ON p.ID_categoria =
               c.ID_categoria

        LEFT JOIN SCONTO s
            ON p.ID_sconto =
               s.ID_sconto
        """;

    /*
     * Utilizzato principalmente dall'area Admin.
     *
     * Restituisce tutti i prodotti presenti fisicamente
     * nel database, compresi quelli non più visibili
     * nel catalogo pubblico.
     */
    public List<Product> findAll()
            throws SQLException {

        List<Product> products =
                new ArrayList<>();

        String sql =
                SELECT_BASE
                + " ORDER BY p.Nome ASC";

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
        ) {

            while (rs.next()) {

                products.add(
                        mapProduct(rs)
                );
            }
        }

        return products;
    }

    /*
     * Ricerca un prodotto tramite ID.
     *
     * Viene lasciato senza filtro Visibile perché
     * viene utilizzato anche dall'area Admin.
     */
    public Product findById(
            int idProdotto)
            throws SQLException {

        String sql =
                SELECT_BASE
                + " WHERE p.ID_prodotto = ?";

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProdotto
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                return rs.next()
                        ? mapProduct(rs)
                        : null;
            }
        }
    }

    /*
     * Ricerca AJAX.
     *
     * Mostra:
     * - prodotti disponibili;
     * - prodotti non disponibili;
     *
     * purché siano ancora visibili nel catalogo.
     *
     * Non mostra invece prodotti eliminati logicamente
     * dall'Admin (Visibile = FALSE).
     */
    public List<Product> searchByName(
            String search)
            throws SQLException {

        List<Product> products =
                new ArrayList<>();

        if (search == null
                || search.isBlank()) {

            return products;
        }

        String sql =
                SELECT_BASE + """

            WHERE p.Visibile = TRUE

              AND LOWER(p.Nome)
                  LIKE LOWER(?)

            ORDER BY p.Nome ASC

            LIMIT 8

        """;

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setString(
                    1,
                    "%"
                    + search.trim()
                    + "%"
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                while (rs.next()) {

                    products.add(
                            mapProduct(rs)
                    );
                }
            }
        }

        return products;
    }

    /*
     * Ricerca e filtraggio del catalogo.
     */
    public List<Product> findFiltered(
            String search,
            Integer idCategoria,
            BigDecimal prezzoMin,
            BigDecimal prezzoMax,
            boolean soloDisponibili,
            String ordinamento)
            throws SQLException {

        List<Product> products =
                new ArrayList<>();

        /*
         * Normalizzazione ricerca.
         */
        if (search != null) {

            search =
                    search.trim();

            if (search.isEmpty()) {

                search = null;
            }
        }

        /*
         * Ordinamento di default.
         */
        if (ordinamento == null
                || ordinamento.isBlank()) {

            ordinamento =
                    "nameAsc";
        }

        /*
         * Deve essere identico al calcolo
         * utilizzato nella SELECT_BASE.
         *
         * Serve per filtrare correttamente
         * prezzo minimo e massimo.
         */
        String prezzoFinaleExpression = """
            ROUND(
                p.Prezzo *
                (
                    1 - (
                        CASE
                            WHEN p.Disponibilita = TRUE
                             AND p.Quantita > 0
                             AND s.ID_sconto IS NOT NULL
                             AND CURDATE()
                                 BETWEEN s.Data_inizio
                                     AND s.Data_fine
                            THEN s.Percentuale

                            ELSE 0
                        END
                    ) / 100
                ),
                2
            )
        """;

        /*
         * Nel catalogo vengono mostrati tutti
         * i prodotti visibili.
         *
         * Quindi:
         *
         * Visibile = TRUE
         * Disponibilita = TRUE
         * -> visibile e acquistabile
         *
         * Visibile = TRUE
         * Disponibilita = FALSE
         * -> visibile ma NON acquistabile
         *
         * Visibile = FALSE
         * -> nascosto dal catalogo
         */
        StringBuilder sql =
                new StringBuilder(
                        SELECT_BASE
                        + """
                           WHERE p.Visibile = TRUE
                           """
                );

        /*
         * Ricerca nome.
         */
        if (search != null) {

            sql.append(
                    """
                     AND LOWER(p.Nome)
                         LIKE LOWER(?)
                    """
            );
        }

        /*
         * Categoria.
         */
        if (idCategoria != null) {

            sql.append(
                    """
                     AND p.ID_categoria = ?
                    """
            );
        }

        /*
         * Prezzo minimo.
         */
        if (prezzoMin != null) {

            sql.append(
                    " AND "
            );

            sql.append(
                    prezzoFinaleExpression
            );

            sql.append(
                    " >= ? "
            );
        }

        /*
         * Prezzo massimo.
         */
        if (prezzoMax != null) {

            sql.append(
                    " AND "
            );

            sql.append(
                    prezzoFinaleExpression
            );

            sql.append(
                    " <= ? "
            );
        }

        /*
         * Questo filtro viene aggiunto solamente
         * quando l'utente seleziona
         * "Solo disponibili".
         */
        if (soloDisponibili) {

            sql.append(
                    """
                     AND p.Disponibilita = TRUE
                     AND p.Quantita > 0
                    """
            );
        }

        /*
         * Ordinamento.
         *
         * Nessun valore dell'utente viene inserito
         * direttamente nella query.
         */
        switch (ordinamento) {

            case "priceAsc" ->

                    sql.append(
                            " ORDER BY Prezzo_finale ASC "
                    );

            case "priceDesc" ->

                    sql.append(
                            " ORDER BY Prezzo_finale DESC "
                    );

            case "nameDesc" ->

                    sql.append(
                            " ORDER BY p.Nome DESC "
                    );

            default ->

                    sql.append(
                            " ORDER BY p.Nome ASC "
                    );
        }

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(
                            sql.toString()
                    )
        ) {

            int index = 1;

            if (search != null) {

                ps.setString(
                        index++,
                        "%"
                        + search
                        + "%"
                );
            }

            if (idCategoria != null) {

                ps.setInt(
                        index++,
                        idCategoria
                );
            }

            if (prezzoMin != null) {

                ps.setBigDecimal(
                        index++,
                        prezzoMin
                );
            }

            if (prezzoMax != null) {

                ps.setBigDecimal(
                        index,
                        prezzoMax
                );
            }

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                while (rs.next()) {

                    products.add(
                            mapProduct(rs)
                    );
                }
            }
        }

        return products;
    }

    /*
     * Prodotti mostrati nella Home.
     *
     * Un prodotto in evidenza deve essere:
     *
     * - visibile;
     * - disponibile;
     * - con almeno un'unità a magazzino.
     *
     * Ordine di priorità:
     *
     * 1. prodotti con sconto attivo;
     * 2. media recensioni;
     * 3. numero recensioni;
     * 4. nome.
     */
    public List<Product> findFeatured(
            int limit)
            throws SQLException {

        int safeLimit =
                Math.max(
                        1,
                        Math.min(
                                limit,
                                12
                        )
                );

        List<Product> products =
                new ArrayList<>();

        String sql =
                SELECT_BASE + """

            WHERE p.Visibile = TRUE

              AND p.Disponibilita = TRUE

              AND p.Quantita > 0

            ORDER BY

                CASE
                    WHEN s.ID_sconto IS NOT NULL

                     AND CURDATE()
                         BETWEEN s.Data_inizio
                             AND s.Data_fine

                    THEN 0

                    ELSE 1

                END,

                Media_recensioni DESC,

                Numero_recensioni DESC,

                p.Nome ASC

            LIMIT ?

        """;

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    safeLimit
            );

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                while (rs.next()) {

                    products.add(
                            mapProduct(rs)
                    );
                }
            }
        }

        return products;
    }

    /*
     * Inserimento prodotto.
     *
     * Non specifichiamo Visibile:
     * il database utilizza DEFAULT TRUE.
     */
    public int insert(
            Product product)
            throws SQLException {

        String sql = """
            INSERT INTO PRODOTTO
            (
                Nome,
                Prezzo,
                IVA,
                Descrizione,
                Immagine,
                Disponibilita,
                Quantita,
                ID_categoria,
                ID_sconto
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(
                            sql,
                            PreparedStatement
                                    .RETURN_GENERATED_KEYS
                    )
        ) {

            setProductParameters(
                    ps,
                    product
            );

            ps.executeUpdate();

            try (
                ResultSet rs =
                        ps.getGeneratedKeys()
            ) {

                if (!rs.next()) {

                    throw new SQLException(
                            "Impossibile recuperare "
                            + "l'ID del prodotto creato."
                    );
                }

                return rs.getInt(1);
            }
        }
    }

    /*
     * Modifica prodotto.
     *
     * Visibile NON viene modificato qui:
     * serve esclusivamente per distinguere
     * i prodotti eliminati logicamente.
     */
    public boolean update(
            Product product)
            throws SQLException {

        String sql = """
            UPDATE PRODOTTO

            SET Nome = ?,
                Prezzo = ?,
                IVA = ?,
                Descrizione = ?,
                Immagine = ?,
                Disponibilita = ?,
                Quantita = ?,
                ID_categoria = ?,
                ID_sconto = ?

            WHERE ID_prodotto = ?
        """;

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection();

            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            setProductParameters(
                    ps,
                    product
            );

            ps.setInt(
                    10,
                    product.getIdProdotto()
            );

            return ps.executeUpdate() > 0;
        }
    }

    /*
     * Eliminazione prodotto.
     *
     * Se il prodotto NON è presente negli ordini,
     * viene eliminato realmente.
     *
     * Se invece compare nello storico degli ordini,
     * non può essere eliminato fisicamente:
     *
     * - Visibile = FALSE
     * - Disponibilita = FALSE
     * - Quantita = 0
     * - ID_sconto = NULL
     *
     * In questo modo:
     *
     * - sparisce dal catalogo;
     * - sparisce dalla ricerca;
     * - non appare nei prodotti in evidenza;
     * - rimane negli ordini storici.
     */
    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */
    public DeleteResult deleteOrDisable(
            int idProdotto)
            throws SQLException {

        if (idProdotto <= 0) {

            return DeleteResult.NOT_FOUND;
        }

        String existsSql = """
            SELECT 1
            FROM PRODOTTO
            WHERE ID_prodotto = ?
        """;

        String removeFromCartsSql = """
            DELETE FROM INCLUDERE
            WHERE ID_prodotto = ?
        """;

        String referencedSql = """
            SELECT 1
            FROM CONTENERE
            WHERE ID_prodotto = ?
            LIMIT 1
        """;

        String deleteSql = """
            DELETE FROM PRODOTTO
            WHERE ID_prodotto = ?
        """;

        String disableSql = """
            UPDATE PRODOTTO

            SET Visibile = FALSE,
                Disponibilita = FALSE,
                Quantita = 0,
                ID_sconto = NULL

            WHERE ID_prodotto = ?
        """;

        try (
            Connection con =
                    DatabaseConnection
                            .getConnection()
        ) {

            con.setAutoCommit(false);

            try {

                /*
                 * Verifica esistenza prodotto.
                 */
                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    existsSql
                            )
                ) {

                    ps.setInt(
                            1,
                            idProdotto
                    );

                    try (
                        ResultSet rs =
                                ps.executeQuery()
                    ) {

                        if (!rs.next()) {

                            con.rollback();

                            return DeleteResult
                                    .NOT_FOUND;
                        }
                    }
                }

                /*
                 * Il prodotto viene eliminato
                 * dagli eventuali carrelli.
                 */
                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    removeFromCartsSql
                            )
                ) {

                    ps.setInt(
                            1,
                            idProdotto
                    );

                    ps.executeUpdate();
                }

                /*
                 * Controlliamo se compare
                 * in almeno un ordine.
                 */
                boolean referenced;

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    referencedSql
                            )
                ) {

                    ps.setInt(
                            1,
                            idProdotto
                    );

                    try (
                        ResultSet rs =
                                ps.executeQuery()
                    ) {

                        referenced =
                                rs.next();
                    }
                }

                /*
                 * Se è presente nello storico,
                 * eliminazione logica.
                 */
                if (referenced) {

                    disableProduct(
                            con,
                            disableSql,
                            idProdotto
                    );

                    con.commit();

                    return DeleteResult
                            .DISABLED;
                }

                /*
                 * Se non è referenziato,
                 * proviamo DELETE reale.
                 */
                Savepoint beforeDelete =
                        con.setSavepoint();

                try (
                    PreparedStatement ps =
                            con.prepareStatement(
                                    deleteSql
                            )
                ) {

                    ps.setInt(
                            1,
                            idProdotto
                    );

                    int affected =
                            ps.executeUpdate();

                    con.commit();

                    return affected > 0
                            ? DeleteResult.DELETED
                            : DeleteResult.NOT_FOUND;

                } catch (
                    SQLIntegrityConstraintViolationException e
                ) {

                    /*
                     * Ulteriore sicurezza:
                     * se qualche vincolo impedisce
                     * la cancellazione fisica,
                     * torniamo al savepoint
                     * e facciamo eliminazione logica.
                     */
                    con.rollback(
                            beforeDelete
                    );

                    disableProduct(
                            con,
                            disableSql,
                            idProdotto
                    );

                    con.commit();

                    return DeleteResult
                            .DISABLED;
                }

            } catch (SQLException e) {

                con.rollback();

                throw e;

            } finally {

                try {

                    con.setAutoCommit(true);

                } catch (
                    SQLException ignored
                ) {

                    /*
                     * La connessione verrà comunque
                     * chiusa dal try-with-resources.
                     */
                }
            }
        }
    }

    /*
     * Esegue la disabilitazione logica.
     */
    private void disableProduct(
            Connection con,
            String sql,
            int idProdotto)
            throws SQLException {

        try (
            PreparedStatement ps =
                    con.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProdotto
            );

            ps.executeUpdate();
        }
    }

    /*
     * Parametri condivisi da INSERT e UPDATE.
     */
    private void setProductParameters(
            PreparedStatement ps,
            Product product)
            throws SQLException {

        ps.setString(
                1,
                product.getNome()
        );

        ps.setBigDecimal(
                2,
                product.getPrezzo()
        );

        ps.setBigDecimal(
                3,
                product.getIva()
        );

        ps.setString(
                4,
                product.getDescrizione()
        );

        if (product.getImmagine()
                == null
                || product.getImmagine()
                          .isBlank()) {

            ps.setNull(
                    5,
                    Types.VARCHAR
            );

        } else {

            ps.setString(
                    5,
                    product.getImmagine()
            );
        }

        ps.setBoolean(
                6,
                product.isDisponibilita()
        );

        ps.setInt(
                7,
                product.getQuantita()
        );

        ps.setInt(
                8,
                product.getIdCategoria()
        );

        if (product.getIdSconto()
                == null) {

            ps.setNull(
                    9,
                    Types.INTEGER
            );

        } else {

            ps.setInt(
                    9,
                    product.getIdSconto()
            );
        }
    }

    /*
     * Converte una riga SQL in Product.
     */
    private Product mapProduct(
            ResultSet rs)
            throws SQLException {

        Product product =
                new Product();

        product.setIdProdotto(
                rs.getInt(
                        "ID_prodotto"
                )
        );

        product.setNome(
                rs.getString(
                        "Nome"
                )
        );

        product.setPrezzo(
                rs.getBigDecimal(
                        "Prezzo"
                )
        );

        product.setIva(
                rs.getBigDecimal(
                        "IVA"
                )
        );

        product.setDescrizione(
                rs.getString(
                        "Descrizione"
                )
        );

        product.setImmagine(
                rs.getString(
                        "Immagine"
                )
        );

        product.setVisibile(
                rs.getBoolean(
                        "Visibile"
                )
        );

        product.setDisponibilita(
                rs.getBoolean(
                        "Disponibilita"
                )
        );

        product.setQuantita(
                rs.getInt(
                        "Quantita"
                )
        );

        product.setIdCategoria(
                rs.getInt(
                        "ID_categoria"
                )
        );

        int idSconto =
                rs.getInt(
                        "ID_sconto"
                );

        product.setIdSconto(
                rs.wasNull()
                        ? null
                        : idSconto
        );

        product.setNomeCategoria(
                rs.getString(
                        "Nome_categoria"
                )
        );

        product.setPercentualeSconto(
                rs.getBigDecimal(
                        "Percentuale_sconto"
                )
        );

        product.setPrezzoFinale(
                rs.getBigDecimal(
                        "Prezzo_finale"
                )
        );

        product.setMediaRecensioni(
                rs.getBigDecimal(
                        "Media_recensioni"
                )
        );

        product.setNumeroRecensioni(
                rs.getInt(
                        "Numero_recensioni"
                )
        );

        return product;
    }
}

package controller;

import dao.CategoryDAO;
import dao.ProductDAO;

import model.Category;
import model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/catalog")
public class CatalogServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;

    /* Inizializza le dipendenze utilizzate dal servlet. */
    @Override
    public void init() {
        productDAO = new ProductDAO();
        categoryDAO = new CategoryDAO();
    }

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * =====================================================
         * LETTURA PARAMETRI
         * =====================================================
         */

        String search =
                request.getParameter("q");

        if (search != null) {

            search = search.trim();

            if (search.isEmpty()) {
                search = null;
            }
        }

        Integer category =
                parseInteger(
                        request.getParameter("category")
                );

        BigDecimal minPrice =
                parseBigDecimal(
                        request.getParameter("minPrice")
                );

        BigDecimal maxPrice =
                parseBigDecimal(
                        request.getParameter("maxPrice")
                );

        boolean available =
                "true".equals(
                        request.getParameter("available")
                );

        String sort =
                request.getParameter("sort");

        if (sort == null || sort.isBlank()) {
            sort = "nameAsc";
        }

        /*
         * =====================================================
         * CARICAMENTO PRODOTTI
         * =====================================================
         */

        List<Product> products =
                new ArrayList<>();

        /*
         * Controllo intervallo prezzi.
         */
        if (
                minPrice != null &&
                maxPrice != null &&
                minPrice.compareTo(maxPrice) > 0
        ) {

            request.setAttribute(
                    "filterError",
                    "Il prezzo minimo non può essere maggiore del prezzo massimo."
            );

        } else {

            try {

                products =
                        productDAO.findFiltered(
                                search,
                                category,
                                minPrice,
                                maxPrice,
                                available,
                                sort
                        );

            } catch (SQLException e) {

                throw new ServletException(
                        "Errore durante il caricamento del catalogo.",
                        e
                );
            }
        }

        /*
         * Passiamo i prodotti alla JSP.
         */
        request.setAttribute(
                "products",
                products
        );

        /*
         * =====================================================
         * CARICAMENTO CATEGORIE
         * =====================================================
         */

        try {

            List<Category> categories =
                    categoryDAO.findAll();

            request.setAttribute(
                    "categories",
                    categories
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il caricamento delle categorie.",
                    e
            );
        }

        /*
         * =====================================================
         * MANTENIMENTO VALORI SELEZIONATI
         * =====================================================
         *
         * Servono a catalog.jsp per mantenere
         * ricerca e filtri dopo il submit.
         */

        request.setAttribute(
                "selectedSearch",
                search
        );

        request.setAttribute(
                "selectedCategory",
                category
        );

        request.setAttribute(
                "selectedMinPrice",
                minPrice
        );

        request.setAttribute(
                "selectedMaxPrice",
                maxPrice
        );

        request.setAttribute(
                "onlyAvailable",
                available
        );

        request.setAttribute(
                "selectedSort",
                sort
        );

        /*
         * =====================================================
         * FORWARD ALLA JSP
         * =====================================================
         */

        request.getRequestDispatcher(
                "/catalog.jsp"
        ).forward(request, response);
    }

    /*
     * Converte una String in Integer.
     *
     * Restituisce null se:
     * - manca
     * - è vuota
     * - non è numerica
     * - è <= 0
     */
    private Integer parseInteger(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            int number =
                    Integer.parseInt(value);

            if (number > 0) {
                return number;
            }

        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    /*
     * Converte una String in BigDecimal.
     *
     * Restituisce null se:
     * - manca
     * - è vuota
     * - non è numerica
     * - è negativa
     */
    private BigDecimal parseBigDecimal(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        try {

            BigDecimal number =
                    new BigDecimal(value);

            if (
                    number.compareTo(
                            BigDecimal.ZERO
                    ) >= 0
            ) {

                return number;
            }

        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }
}

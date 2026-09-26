package controller.admin;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ScontoDAO;
import model.Category;
import model.Product;
import service.ProductImageStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/admin/products/*")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15
)
public class AdminProductsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ScontoDAO scontoDAO = new ScontoDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getPathInfo();

        try {
            if (path == null || "/".equals(path)) {
                showList(request, response);
                return;
            }

            if ("/new".equals(path)) {
                request.setAttribute("product", new Product());
                showForm(request, response);
                return;
            }

            if ("/edit".equals(path)) {
                int idProdotto = parsePositiveInt(request.getParameter("id"));
                Product product = productDAO.findById(idProdotto);

                if (product == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                request.setAttribute("product", product);
                showForm(request, response);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificativo prodotto non valido.");
        } catch (SQLException e) {
            throw new ServletException("Errore durante la gestione amministrativa dei prodotti.", e);
        }
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        try {
            if ("/save".equals(path)) {
                saveProduct(request, response);
                return;
            }

            if ("/delete".equals(path)) {
                deleteProduct(request, response);
                return;
            }

            response.sendError(HttpServletResponse.SC_NOT_FOUND);

        } catch (SQLException e) {
            throw new ServletException("Errore durante la gestione amministrativa dei prodotti.", e);
        }
    }

    /* Implementa l’operazione showList usata dalla logica applicativa. */

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        request.setAttribute("products", productDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/admin/products.jsp")
                .forward(request, response);
    }

    /* Implementa l’operazione showForm usata dalla logica applicativa. */

    private void showForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        List<Category> categories = categoryDAO.findAll();
        request.setAttribute("categories", categories);
        request.setAttribute("discounts", scontoDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/admin/product-form.jsp")
                .forward(request, response);
    }

    /* Salva i dati nel database dopo la validazione. */

    private void saveProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        Product product = new Product();
        String idValue = trim(request.getParameter("idProdotto"));
        String immagineEsistente = null;
        String nuovaImmagine = null;

        try {
            if (idValue != null && !idValue.isBlank()) {
                product.setIdProdotto(parsePositiveInt(idValue));

                Product prodottoEsistente = productDAO.findById(product.getIdProdotto());
                if (prodottoEsistente == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                immagineEsistente = prodottoEsistente.getImmagine();
            }

            product.setNome(requireText(
                    request.getParameter("nome"),
                    "Inserisci un nome prodotto valido.",
                    2,
                    120
            ));

            product.setDescrizione(optionalText(request.getParameter("descrizione"), 2000));

            BigDecimal prezzo = parseBigDecimal(request.getParameter("prezzo"), "Inserisci un prezzo valido.");
            BigDecimal iva = parseBigDecimal(request.getParameter("iva"), "Inserisci un valore IVA valido.");
            int quantita = parseNonNegativeInt(request.getParameter("quantita"));
            int idCategoria = parsePositiveInt(request.getParameter("idCategoria"));
            Integer idSconto = parseNullablePositiveInt(request.getParameter("idSconto"));

            if (categoryDAO.findAll().stream().noneMatch(c -> c.getIdCategoria() == idCategoria)) {
                throw new IllegalArgumentException("La categoria selezionata non esiste.");
            }

            if (idSconto != null && scontoDAO.findById(idSconto) == null) {
                throw new IllegalArgumentException("Lo sconto selezionato non esiste.");
            }

            if (prezzo.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Il prezzo deve essere maggiore di zero.");
            }

            if (iva.compareTo(BigDecimal.ZERO) < 0 || iva.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("L'IVA deve essere compresa tra 0 e 100.");
            }

            product.setPrezzo(prezzo);
            product.setIva(iva);
            product.setQuantita(quantita);
            product.setIdCategoria(idCategoria);
            product.setIdSconto(idSconto);

            boolean requestedAvailability = "true".equals(request.getParameter("disponibilita"));
            product.setDisponibilita(requestedAvailability && quantita > 0);

            boolean rimuoviImmagine = "true".equals(request.getParameter("rimuoviImmagine"));

            Part imagePart;
            try {
                imagePart = request.getPart("immagine");
            } catch (IllegalStateException e) {
                throw new IllegalArgumentException("Il file caricato supera la dimensione massima consentita.");
            }

            nuovaImmagine = ProductImageStorage.save(imagePart);

            if (nuovaImmagine != null) {
                product.setImmagine(nuovaImmagine);
            } else if (rimuoviImmagine) {
                product.setImmagine(null);
            } else {
                product.setImmagine(immagineEsistente);
            }

            HttpSession session = request.getSession();

            if (product.getIdProdotto() > 0) {
                if (!productDAO.update(product)) {
                    ProductImageStorage.deleteIfUploaded(nuovaImmagine);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                session.setAttribute("adminMessage", "Prodotto modificato correttamente.");
            } else {
                productDAO.insert(product);
                session.setAttribute("adminMessage", "Prodotto inserito correttamente.");
            }

            if (immagineEsistente != null && !immagineEsistente.equals(product.getImmagine())) {
                ProductImageStorage.deleteIfUploaded(immagineEsistente);
            }

            response.sendRedirect(request.getContextPath() + "/admin/products");

        } catch (IllegalArgumentException e) {
            ProductImageStorage.deleteIfUploaded(nuovaImmagine);
            product.setImmagine(immagineEsistente);
            request.setAttribute("formError", e.getMessage());
            request.setAttribute("product", product);
            showForm(request, response);
        }
    }

    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        int idProdotto;
        try {
            idProdotto = parsePositiveInt(request.getParameter("idProdotto"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificativo prodotto non valido.");
            return;
        }

        Product existing = productDAO.findById(idProdotto);
        ProductDAO.DeleteResult result = productDAO.deleteOrDisable(idProdotto);
        HttpSession session = request.getSession();

        switch (result) {
            case DELETED -> {
                if (existing != null) {
                    ProductImageStorage.deleteIfUploaded(existing.getImmagine());
                }
                session.setAttribute("adminMessage", "Prodotto eliminato correttamente.");
            }
            case DISABLED -> session.setAttribute(
                    "adminMessage",
                    "Il prodotto è presente nello storico o in altre relazioni: è stato disabilitato invece di essere eliminato."
            );
            case NOT_FOUND -> session.setAttribute("adminError", "Prodotto non trovato.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/products");
    }

    /* Converte un parametro in intero positivo e segnala valori non validi. */

    private int parsePositiveInt(String value) {
        int number = Integer.parseInt(value);
        if (number <= 0) {
            throw new NumberFormatException();
        }
        return number;
    }

    /* Converte un parametro in intero non negativo e segnala valori non validi. */

    private int parseNonNegativeInt(String value) {
        try {
            int number = Integer.parseInt(value);
            if (number < 0) {
                throw new IllegalArgumentException("La quantità non può essere negativa.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Inserisci una quantità valida.");
        }
    }

    /* Converte un parametro opzionale in intero positivo oppure restituisce null. */

    private Integer parseNullablePositiveInt(String value) {
        String normalized = trim(value);
        if (normalized == null || normalized.isBlank()) {
            return null;
        }

        try {
            int number = Integer.parseInt(normalized);
            if (number <= 0) {
                throw new IllegalArgumentException("ID sconto non valido.");
            }
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID sconto non valido.");
        }
    }

    /* Converte e valida un valore numerico decimale ricevuto dal form. */

    private BigDecimal parseBigDecimal(String value, String errorMessage) {
        try {
            return new BigDecimal(trim(value));
        } catch (Exception e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /* Valida un testo obbligatorio e lo restituisce ripulito dagli spazi esterni. */

    private String requireText(String value, String errorMessage, int min, int max) {
        String text = trim(value);
        if (text == null || text.length() < min || text.length() > max) {
            throw new IllegalArgumentException(errorMessage);
        }
        return text;
    }

    /* Normalizza un testo facoltativo restituendo null quando è vuoto. */

    private String optionalText(String value, int max) {
        String text = trim(value);
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (text.length() > max) {
            throw new IllegalArgumentException("La descrizione è troppo lunga.");
        }
        return text;
    }

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

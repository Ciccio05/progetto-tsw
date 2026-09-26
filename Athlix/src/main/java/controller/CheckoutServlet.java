package controller;

import dao.CartDAO;
import dao.MetodoPagamentoDAO;
import dao.OrderDAO;
import model.CarrelloBean;
import model.CartItem;
import model.MetodoPagamentoBean;
import model.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}' -]{1,49}$");

    private static final Pattern CITY_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}' .-]{1,49}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern CAP_PATTERN =
            Pattern.compile("^\\d{5}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9 .-]{7,20}$");

    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final MetodoPagamentoDAO metodoPagamentoDAO = new MetodoPagamentoDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UtenteBean utente = getAuthenticatedUser(request, response);
        if (utente == null) {
            return;
        }

        loadCheckout(request, response, utente.getIdUtente());
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        UtenteBean utente = getAuthenticatedUser(request, response);
        if (utente == null) {
            return;
        }

        String nome = trim(request.getParameter("nome"));
        String cognome = trim(request.getParameter("cognome"));
        String email = trim(request.getParameter("email"));
        String indirizzo = trim(request.getParameter("indirizzo"));
        String citta = trim(request.getParameter("citta"));
        String cap = trim(request.getParameter("cap"));
        String telefono = trim(request.getParameter("telefono"));
        String metodoPagamento = trim(request.getParameter("idMetodoPagamento"));

        preserveValues(
                request,
                nome,
                cognome,
                email,
                indirizzo,
                citta,
                cap,
                telefono,
                metodoPagamento
        );

        String validationError = validate(
                nome,
                cognome,
                email,
                indirizzo,
                citta,
                cap,
                telefono,
                metodoPagamento
        );

        if (validationError != null) {
            request.setAttribute("checkoutError", validationError);
            loadCheckout(request, response, utente.getIdUtente());
            return;
        }

        try {
            int idMetodoPagamento = Integer.parseInt(metodoPagamento);
            if (idMetodoPagamento <= 0) {
                throw new NumberFormatException();
            }

            CarrelloBean carrello = buildCart(utente.getIdUtente());

            if (carrello.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/cart");
                return;
            }

            int idOrdine = orderDAO.creaOrdine(
                    utente.getIdUtente(),
                    idMetodoPagamento,
                    carrello.getItems(),
                    indirizzo,
                    citta,
                    cap
            );

            /* L'ordine svuota il carrello persistente: eliminiamo anche la copia di sessione. */
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.removeAttribute("carrello");
            }

            request.setAttribute("idOrdine", idOrdine);
            request.setAttribute("totale", carrello.getTotal());

            request.getRequestDispatcher("/order-confirmation.jsp")
                    .forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("checkoutError", "Metodo di pagamento non valido.");
            loadCheckout(request, response, utente.getIdUtente());

        } catch (SQLException e) {
            throw new ServletException("Errore durante il checkout.", e);
        }
    }

    /* Carica carrello e metodi di pagamento e prepara la pagina di checkout. */

    private void loadCheckout(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            CarrelloBean carrello = buildCart(idUtente);
            List<MetodoPagamentoBean> paymentMethods = metodoPagamentoDAO.findAll();

            request.getSession().setAttribute("carrello", carrello);

            request.setAttribute("cartItems", carrello.getItems());
            request.setAttribute("subtotal", carrello.getSubtotal());
            request.setAttribute("shipping", carrello.getShipping());
            request.setAttribute("total", carrello.getTotal());
            request.setAttribute("emptyCart", carrello.isEmpty());
            request.setAttribute("paymentMethods", paymentMethods);

            request.getRequestDispatcher("/checkout.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Errore nel caricamento del checkout.", e);
        }
    }

    /* Costruisce il bean del carrello calcolando righe, subtotale, spedizione e totale. */

    private CarrelloBean buildCart(int idUtente) throws SQLException {
        List<CartItem> items = cartDAO.getCartItemsByUserId(idUtente);
        BigDecimal subtotal = cartDAO.calculateSubtotal(idUtente);
        BigDecimal shipping = BigDecimal.ZERO;

        CarrelloBean carrello = new CarrelloBean();
        carrello.setItems(items);
        carrello.setSubtotal(subtotal);
        carrello.setShipping(shipping);
        carrello.setTotal(subtotal.add(shipping));
        carrello.setItemCount(cartDAO.countDistinctProducts(idUtente));

        return carrello;
    }

    private UtenteBean getAuthenticatedUser(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        UtenteBean utente = session == null
                ? null
                : (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=checkout");
            return null;
        }

        if (utente.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        return utente;
    }

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private String validate(
            String nome,
            String cognome,
            String email,
            String indirizzo,
            String citta,
            String cap,
            String telefono,
            String metodoPagamento) {

        if (nome == null || !NAME_PATTERN.matcher(nome).matches()) {
            return "Inserisci un nome valido.";
        }

        if (cognome == null || !NAME_PATTERN.matcher(cognome).matches()) {
            return "Inserisci un cognome valido.";
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Inserisci un indirizzo email valido.";
        }

        if (indirizzo == null || indirizzo.length() < 3 || indirizzo.length() > 150) {
            return "Inserisci un indirizzo valido.";
        }

        if (citta == null || !CITY_PATTERN.matcher(citta).matches()) {
            return "Inserisci una città valida.";
        }

        if (cap == null || !CAP_PATTERN.matcher(cap).matches()) {
            return "Il CAP deve contenere esattamente 5 cifre.";
        }

        if (telefono == null || !PHONE_PATTERN.matcher(telefono).matches()) {
            return "Inserisci un numero di telefono valido.";
        }

        if (metodoPagamento == null || !metodoPagamento.matches("^\\d+$")) {
            return "Seleziona un metodo di pagamento.";
        }

        return null;
    }

    /* Conserva nella request i valori inseriti per ripopolare il form dopo un errore. */

    private void preserveValues(
            HttpServletRequest request,
            String nome,
            String cognome,
            String email,
            String indirizzo,
            String citta,
            String cap,
            String telefono,
            String metodoPagamento) {

        request.setAttribute("oldNome", nome);
        request.setAttribute("oldCognome", cognome);
        request.setAttribute("oldEmail", email);
        request.setAttribute("oldIndirizzo", indirizzo);
        request.setAttribute("oldCitta", citta);
        request.setAttribute("oldCap", cap);
        request.setAttribute("oldTelefono", telefono);
        request.setAttribute("oldMetodoPagamento", metodoPagamento);
    }

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}

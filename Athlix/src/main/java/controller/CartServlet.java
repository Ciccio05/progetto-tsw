package controller;

import dao.CartDAO;
import model.CarrelloBean;
import model.CartItem;
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

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CartDAO cartDAO = new CartDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Integer idUtente = getAuthenticatedUserId(request, response);
        if (idUtente == null) {
            return;
        }

        loadCart(request, response, idUtente);
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Integer idUtente = getAuthenticatedUserId(request, response);
        if (idUtente == null) {
            return;
        }

        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        try {
            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione mancante.");
                return;
            }

            int idProdotto = parsePositiveInt(request.getParameter("idProdotto"));
            boolean success;

            switch (action) {
                case "add" -> {
                    int quantita = parsePositiveInt(request.getParameter("quantita"));
                    success = cartDAO.addProductToCart(idUtente, idProdotto, quantita);

                    if (success) {
                        session.setAttribute("cartMessage", "Prodotto aggiunto al carrello.");
                    } else {
                        session.setAttribute(
                                "cartError",
                                "Quantità non disponibile o prodotto non acquistabile."
                        );
                    }
                }

                case "increase" -> {
                    success = cartDAO.increaseQuantity(idUtente, idProdotto);
                    if (!success) {
                        session.setAttribute(
                                "cartError",
                                "Non puoi superare la quantità disponibile in magazzino."
                        );
                    }
                }

                case "decrease" -> {
                    success = cartDAO.decreaseQuantity(idUtente, idProdotto);
                    if (!success) {
                        session.setAttribute(
                                "cartError",
                                "Impossibile diminuire ulteriormente la quantità."
                        );
                    }
                }

                case "remove" -> {
                    success = cartDAO.removeProductFromCart(idUtente, idProdotto);
                    if (success) {
                        session.setAttribute("cartMessage", "Prodotto rimosso dal carrello.");
                    } else {
                        session.setAttribute(
                                "cartError",
                                "Impossibile rimuovere il prodotto dal carrello."
                        );
                    }
                }

                default -> {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Azione non valida.");
                    return;
                }
            }

            /*
             * La sorgente persistente resta il database, ma il carrello viene
             * mantenuto anche in sessione come richiesto dalla checklist del corso.
             * Dopo ogni modifica il GET successivo rigenera l'oggetto di sessione.
             */
            session.removeAttribute("carrello");

            response.sendRedirect(request.getContextPath() + "/cart");

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Parametri del carrello non validi."
            );
        } catch (SQLException e) {
            throw new ServletException("Errore durante la gestione del carrello.", e);
        }
    }

    /* Carica dal database il carrello e prepara gli attributi necessari alla JSP. */

    private void loadCart(
            HttpServletRequest request,
            HttpServletResponse response,
            int idUtente)
            throws ServletException, IOException {

        try {
            CarrelloBean carrello = buildCart(idUtente);

            HttpSession session = request.getSession();
            session.setAttribute("carrello", carrello);

            // Manteniamo anche gli attributi request già usati dalla JSP.
            request.setAttribute("cartItems", carrello.getItems());
            request.setAttribute("subtotal", carrello.getSubtotal());
            request.setAttribute("shipping", carrello.getShipping());
            request.setAttribute("total", carrello.getTotal());
            request.setAttribute("itemCount", carrello.getItemCount());

            request.getRequestDispatcher("/cart.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Errore nel caricamento del carrello.", e);
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

    private Integer getAuthenticatedUserId(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        UtenteBean utente = session == null
                ? null
                : (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=cart");
            return null;
        }

        if (utente.isAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        return utente.getIdUtente();
    }

    /* Converte un parametro in intero positivo e segnala valori non validi. */

    private int parsePositiveInt(String value) {
        int number = Integer.parseInt(value);
        if (number <= 0) {
            throw new NumberFormatException("Il valore deve essere positivo.");
        }
        return number;
    }
}

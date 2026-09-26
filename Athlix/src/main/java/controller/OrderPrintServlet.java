package controller;

import dao.OrderDAO;
import model.CartItem;
import model.OrdineBean;
import model.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/order-print")
public class OrderPrintServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final OrderDAO orderDAO =
            new OrderDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        UtenteBean utente =
                session == null
                        ? null
                        : (UtenteBean)
                        session.getAttribute("utente");

        if (utente == null) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/login?redirect=account"
            );

            return;
        }

        if (utente.isAdmin()) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return;
        }

        int idOrdine;

        try {

            idOrdine =
                    Integer.parseInt(
                            request.getParameter("id")
                    );

            if (idOrdine <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Identificativo ordine non valido."
            );

            return;
        }

        try {

            /*
             * Il controllo utente è fatto direttamente nella query:
             * un cliente non può stampare l'ordine di un altro cliente.
             */
            OrdineBean ordine =
                    orderDAO.findByIdAndUserId(
                            idOrdine,
                            utente.getIdUtente()
                    );

            if (ordine == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            List<CartItem> dettaglio =
                    orderDAO.findItemsByOrderIdAndUserId(
                            idOrdine,
                            utente.getIdUtente()
                    );

            request.setAttribute(
                    "ordine",
                    ordine
            );

            request.setAttribute(
                    "dettaglio",
                    dettaglio
            );

            request.setAttribute(
                    "cliente",
                    utente
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/orders/order-print.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante la generazione del riepilogo ordine.",
                    e
            );
        }
    }
}

package controller;

import dao.OrderDAO;
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
@WebServlet("/orders")
public class OrderListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final OrderDAO orderDAO =
            new OrderDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        UtenteBean utente =
                getAuthenticatedCustomer(
                        request,
                        response
                );

        if (utente == null) {
            return;
        }

        try {

            List<OrdineBean> ordini =
                    orderDAO.findByUserId(
                            utente.getIdUtente()
                    );

            request.setAttribute(
                    "ordini",
                    ordini
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/orders/order-list.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il caricamento degli ordini.",
                    e
            );
        }
    }

    private UtenteBean getAuthenticatedCustomer(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

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

            return null;
        }

        if (utente.isAdmin()) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN
            );

            return null;
        }

        return utente;
    }
}

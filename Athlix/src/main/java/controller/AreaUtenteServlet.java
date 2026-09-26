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
@WebServlet("/account")
public class AreaUtenteServlet extends HttpServlet {

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

        try {

            List<OrdineBean> ordini =
                    orderDAO.findByUserId(
                            utente.getIdUtente()
                    );

            request.setAttribute(
                    "ordini",
                    ordini
            );

            Object profileMessage =
                    session.getAttribute(
                            "profileMessage"
                    );

            if (profileMessage != null) {

                request.setAttribute(
                        "profileMessage",
                        profileMessage
                );

                session.removeAttribute(
                        "profileMessage"
                );
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/account/area_utente.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il caricamento dell'area utente.",
                    e
            );
        }
    }
}

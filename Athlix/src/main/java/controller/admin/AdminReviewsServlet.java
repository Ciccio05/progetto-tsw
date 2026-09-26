package controller.admin;

import dao.RecensioneDAO;
import model.RecensioneBean;

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
@WebServlet("/admin/reviews")
public class AdminReviewsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RecensioneDAO recensioneDAO =
            new RecensioneDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String search =
                request.getParameter("search");

        try {

            List<RecensioneBean> recensioni =
                    recensioneDAO.findAllForAdmin(
                            search
                    );

            request.setAttribute(
                    "recensioni",
                    recensioni
            );

            request.setAttribute(
                    "search",
                    search
            );

            HttpSession session =
                    request.getSession(false);

            if (session != null) {

                Object message =
                        session.getAttribute(
                                "adminReviewMessage"
                        );

                if (message != null) {

                    request.setAttribute(
                            "adminMessage",
                            message
                    );

                    session.removeAttribute(
                            "adminReviewMessage"
                    );
                }
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/reviews.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il caricamento delle recensioni.",
                    e
            );
        }
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int idRecensione;

        try {

            idRecensione =
                    Integer.parseInt(
                            request.getParameter(
                                    "idRecensione"
                            )
                    );

            if (idRecensione <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Recensione non valida."
            );

            return;
        }

        try {

            boolean deleted =
                    recensioneDAO.deleteByAdmin(
                            idRecensione
                    );

            if (!deleted) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            request.getSession()
                    .setAttribute(
                            "adminReviewMessage",
                            "Recensione eliminata correttamente."
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/reviews"
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante l'eliminazione della recensione.",
                    e
            );
        }
    }
}

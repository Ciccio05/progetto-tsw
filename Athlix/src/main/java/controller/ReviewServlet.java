package controller;

import dao.ProductDAO;
import dao.RecensioneDAO;
import model.Product;
import model.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RecensioneDAO recensioneDAO =
            new RecensioneDAO();

    private final ProductDAO productDAO =
            new ProductDAO();

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

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

        int idProdotto;
        int voto;

        try {

            idProdotto =
                    Integer.parseInt(
                            request.getParameter(
                                    "idProdotto"
                            )
                    );

            voto =
                    Integer.parseInt(
                            request.getParameter(
                                    "voto"
                            )
                    );

            if (idProdotto <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Dati recensione non validi."
            );

            return;
        }

        String commento =
                request.getParameter(
                        "commento"
                );

        try {

            Product product =
                    productDAO.findById(
                            idProdotto
                    );

            if (product == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            recensioneDAO.saveOrUpdate(
                    utente.getIdUtente(),
                    idProdotto,
                    voto,
                    commento
            );

            request.getSession()
                    .setAttribute(
                            "reviewMessage",
                            "Recensione salvata correttamente."
                    );

        } catch (SQLException e) {

            request.getSession()
                    .setAttribute(
                            "reviewError",
                            e.getMessage()
                    );
        }

        response.sendRedirect(
                request.getContextPath()
                        + "/product?id="
                        + idProdotto
                        + "#FM-reviews"
        );
    }
}

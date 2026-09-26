package controller;

import dao.ProductDAO;
import dao.RecensioneDAO;
import model.Product;
import model.RecensioneBean;
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
@WebServlet("/product")
public class ProductDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;
    private RecensioneDAO recensioneDAO;

    /* Inizializza le dipendenze utilizzate dal servlet. */
    @Override
    public void init() {
        productDAO = new ProductDAO();
        recensioneDAO = new RecensioneDAO();
    }

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String idParameter =
                request.getParameter("id");

        if (idParameter == null
                || idParameter.trim().isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID prodotto mancante."
            );

            return;
        }

        try {

            int idProdotto =
                    Integer.parseInt(
                            idParameter
                    );

            if (idProdotto <= 0) {
                throw new NumberFormatException();
            }

            Product product =
                    productDAO.findById(
                            idProdotto
                    );

            if (product == null || !product.isVisibile()) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Prodotto non trovato."
                );

                return;
            }

            List<RecensioneBean> recensioni =
                    recensioneDAO.findByProductId(
                            idProdotto
                    );

            double mediaRecensioni =
                    recensioneDAO.getAverageRating(
                            idProdotto
                    );

            int numeroRecensioni =
                    recensioneDAO.countByProductId(
                            idProdotto
                    );

            request.setAttribute(
                    "product",
                    product
            );

            request.setAttribute(
                    "recensioni",
                    recensioni
            );

            request.setAttribute(
                    "mediaRecensioni",
                    mediaRecensioni
            );

            request.setAttribute(
                    "numeroRecensioni",
                    numeroRecensioni
            );

            HttpSession session =
                    request.getSession(false);

            UtenteBean utente =
                    session == null
                            ? null
                            : (UtenteBean)
                            session.getAttribute("utente");

            if (utente != null
                    && !utente.isAdmin()) {

                boolean canReview =
                        recensioneDAO.hasPurchasedProduct(
                                utente.getIdUtente(),
                                idProdotto
                        );

                RecensioneBean userReview =
                        recensioneDAO.findByUserAndProduct(
                                utente.getIdUtente(),
                                idProdotto
                        );

                request.setAttribute(
                        "canReview",
                        canReview
                );

                request.setAttribute(
                        "userReview",
                        userReview
                );
            }

            if (session != null) {

                Object reviewMessage =
                        session.getAttribute(
                                "reviewMessage"
                        );

                if (reviewMessage != null) {

                    request.setAttribute(
                            "reviewMessage",
                            reviewMessage
                    );

                    session.removeAttribute(
                            "reviewMessage"
                    );
                }

                Object reviewError =
                        session.getAttribute(
                                "reviewError"
                        );

                if (reviewError != null) {

                    request.setAttribute(
                            "reviewError",
                            reviewError
                    );

                    session.removeAttribute(
                            "reviewError"
                    );
                }
            }

            request.getRequestDispatcher(
                    "/product-detail.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "ID prodotto non valido."
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il caricamento del prodotto.",
                    e
            );
        }
    }
}

package controller.admin;

import dao.ProductDAO;
import dao.ScontoDAO;
import model.Product;
import model.ScontoBean;

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
@WebServlet("/admin/discounts/products")
public class AdminProductDiscountsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ProductDAO productDAO =
            new ProductDAO();

    private final ScontoDAO scontoDAO =
            new ScontoDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        loadPage(
                request,
                response
        );
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int idProdotto =
                parsePositiveInt(
                        request.getParameter(
                                "idProdotto"
                        )
                );

        if (idProdotto <= 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Prodotto non valido."
            );
            return;
        }

        String idScontoValue =
                request.getParameter(
                        "idSconto"
                );

        Integer idSconto = null;

        if (idScontoValue != null
                && !idScontoValue.isBlank()) {

            int parsed =
                    parsePositiveInt(
                            idScontoValue
                    );

            if (parsed <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Sconto non valido."
                );
                return;
            }

            idSconto = parsed;
        }

        try {
            if (!scontoDAO.assignToProduct(
                    idProdotto,
                    idSconto)) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            HttpSession session =
                    request.getSession();

            session.setAttribute(
                    "adminProductDiscountMessage",
                    idSconto == null
                            ? "Sconto rimosso dal prodotto."
                            : "Sconto assegnato al prodotto."
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/discounts/products"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante l'assegnazione dello sconto.",
                    e
            );
        }
    }

    /* Implementa l’operazione loadPage usata dalla logica applicativa. */

    private void loadPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<Product> products =
                    productDAO.findAll();

            List<ScontoBean> sconti =
                    scontoDAO.findAll();

            request.setAttribute(
                    "products",
                    products
            );

            request.setAttribute(
                    "sconti",
                    sconti
            );

            HttpSession session =
                    request.getSession(false);

            if (session != null) {
                Object message =
                        session.getAttribute(
                                "adminProductDiscountMessage"
                        );

                if (message != null) {
                    request.setAttribute(
                            "adminMessage",
                            message
                    );

                    session.removeAttribute(
                            "adminProductDiscountMessage"
                    );
                }
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/product-discounts.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante il caricamento dei prodotti e degli sconti.",
                    e
            );
        }
    }

    /* Converte un parametro in intero positivo e segnala valori non validi. */

    private int parsePositiveInt(String value) {
        try {
            int number =
                    Integer.parseInt(value);

            return number > 0
                    ? number
                    : -1;

        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

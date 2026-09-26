package controller.admin;

import dao.AdminOrderDAO;
import model.AdminOrderBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/admin/orders")
public class AdminOrdersServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminOrderDAO adminOrderDAO =
            new AdminOrderDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String fromValue =
                trim(request.getParameter("from"));

        String toValue =
                trim(request.getParameter("to"));

        String customer =
                trim(request.getParameter("customer"));

        LocalDate from = null;
        LocalDate to = null;

        try {

            if (fromValue != null && !fromValue.isBlank()) {
                from = LocalDate.parse(fromValue);
            }

            if (toValue != null && !toValue.isBlank()) {
                to = LocalDate.parse(toValue);
            }

        } catch (DateTimeParseException e) {

            request.setAttribute(
                    "adminError",
                    "Intervallo di date non valido."
            );
        }

        if (from != null && to != null && from.isAfter(to)) {

            request.setAttribute(
                    "adminError",
                    "La data iniziale non può essere successiva alla data finale."
            );

            LocalDate temp = from;
            from = to;
            to = temp;
        }

        try {

            List<AdminOrderBean> ordini =
                    adminOrderDAO.findFiltered(
                            from,
                            to,
                            customer
                    );

            request.setAttribute(
                    "ordini",
                    ordini
            );

            request.setAttribute(
                    "from",
                    fromValue
            );

            request.setAttribute(
                    "to",
                    toValue
            );

            request.setAttribute(
                    "customer",
                    customer
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/orders.jsp"
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

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null
                ? null
                : value.trim();
    }
}

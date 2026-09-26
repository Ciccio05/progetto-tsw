package controller.admin;

import dao.AdminOrderDAO;
import model.AdminOrderBean;
import model.AdminOrderItemBean;

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
@WebServlet("/admin/orders/detail")
public class AdminOrderDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AdminOrderDAO adminOrderDAO = new AdminOrderDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idOrdine = parseId(request.getParameter("id"));
        if (idOrdine <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificativo ordine non valido.");
            return;
        }

        loadDetail(request, response, idOrdine);
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        int idOrdine = parseId(request.getParameter("id"));
        if (idOrdine <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Identificativo ordine non valido.");
            return;
        }

        String action = normalizeAction(request.getParameter("action"));

        try {
            if ("cancel".equals(action)) {
                if (!"true".equals(request.getParameter("confirmCancel"))) {
                    throw new SQLException("Devi confermare esplicitamente l'annullamento dell'ordine.");
                }

                adminOrderDAO.annullaOrdine(idOrdine);
                setFlashMessage(
                        request,
                        "Ordine annullato correttamente: stock ripristinato, spedizione annullata e pagamento aggiornato."
                );

            } else if ("update".equals(action)) {
                adminOrderDAO.updateWorkflow(idOrdine, request.getParameter("stato"));
                setFlashMessage(request, "Stato dell'ordine aggiornato correttamente.");

            } else {
                throw new SQLException("Azione amministrativa non valida.");
            }

            response.sendRedirect(
                    request.getContextPath() + "/admin/orders/detail?id=" + idOrdine
            );

        } catch (SQLException e) {
            request.setAttribute("adminError", e.getMessage());
            loadDetail(request, response, idOrdine);
        }
    }

    private void setFlashMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("adminOrderMessage", message);
    }

    /* Implementa l’operazione loadDetail usata dalla logica applicativa. */

    private void loadDetail(HttpServletRequest request, HttpServletResponse response, int idOrdine)
            throws ServletException, IOException {

        try {
            AdminOrderBean ordine = adminOrderDAO.findById(idOrdine);
            if (ordine == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            List<AdminOrderItemBean> items = adminOrderDAO.findItemsByOrderId(idOrdine);
            request.setAttribute("ordine", ordine);
            request.setAttribute("items", items);

            HttpSession session = request.getSession(false);
            if (session != null) {
                Object message = session.getAttribute("adminOrderMessage");
                if (message != null) {
                    request.setAttribute("adminMessage", message);
                    session.removeAttribute("adminOrderMessage");
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/admin/order-detail.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Errore durante il caricamento del dettaglio ordine.", e);
        }
    }

    /* Implementa l’operazione parseId usata dalla logica applicativa. */

    private int parseId(String value) {
        try {
            int id = Integer.parseInt(value);
            return id > 0 ? id : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /* Normalizza il valore ricevuto prima di usarlo nella logica applicativa. */

    private String normalizeAction(String value) {
        return value == null ? "update" : value.trim().toLowerCase();
    }
}

package controller.admin;

import dao.ScontoDAO;
import model.ScontoBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet(urlPatterns = {
        "/admin/discounts",
        "/admin/discounts/new",
        "/admin/discounts/edit",
        "/admin/discounts/delete"
})
public class AdminDiscountsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ScontoDAO scontoDAO =
            new ScontoDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String path =
                request.getServletPath();

        switch (path) {

            case "/admin/discounts" ->
                    showList(request, response);

            case "/admin/discounts/new" ->
                    showForm(request, response, null);

            case "/admin/discounts/edit" -> {
                int idSconto =
                        parsePositiveInt(
                                request.getParameter("id")
                        );

                if (idSconto <= 0) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Identificativo sconto non valido."
                    );
                    return;
                }

                try {
                    ScontoBean sconto =
                            scontoDAO.findById(idSconto);

                    if (sconto == null) {
                        response.sendError(
                                HttpServletResponse.SC_NOT_FOUND
                        );
                        return;
                    }

                    showForm(
                            request,
                            response,
                            sconto
                    );

                } catch (SQLException e) {
                    throw new ServletException(
                            "Errore durante il caricamento dello sconto.",
                            e
                    );
                }
            }

            default ->
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND
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

        String path =
                request.getServletPath();

        switch (path) {

            case "/admin/discounts/new" ->
                    createDiscount(request, response);

            case "/admin/discounts/edit" ->
                    updateDiscount(request, response);

            case "/admin/discounts/delete" ->
                    deleteDiscount(request, response);

            default ->
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND
                    );
        }
    }

    /* Implementa l’operazione showList usata dalla logica applicativa. */

    private void showList(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            List<ScontoBean> sconti =
                    scontoDAO.findAll();

            request.setAttribute(
                    "sconti",
                    sconti
            );

            HttpSession session =
                    request.getSession(false);

            if (session != null) {
                Object message =
                        session.getAttribute(
                                "adminDiscountMessage"
                        );

                if (message != null) {
                    request.setAttribute(
                            "adminMessage",
                            message
                    );

                    session.removeAttribute(
                            "adminDiscountMessage"
                    );
                }
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/discounts.jsp"
            ).forward(
                    request,
                    response
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante il caricamento degli sconti.",
                    e
            );
        }
    }

    /* Implementa l’operazione showForm usata dalla logica applicativa. */

    private void showForm(
            HttpServletRequest request,
            HttpServletResponse response,
            ScontoBean sconto)
            throws ServletException, IOException {

        request.setAttribute(
                "sconto",
                sconto
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/admin/discount-form.jsp"
        ).forward(
                request,
                response
        );
    }

    /* Crea e persiste il nuovo record necessario all’operazione. */

    private void createDiscount(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        ScontoBean sconto =
                buildFromRequest(request);

        if (sconto == null) {
            showForm(request, response, null);
            return;
        }

        try {
            int idSconto =
                    scontoDAO.insert(sconto);

            request.getSession()
                    .setAttribute(
                            "adminDiscountMessage",
                            "Sconto #" + idSconto + " creato correttamente."
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/discounts"
            );

        } catch (SQLException e) {
            request.setAttribute(
                    "adminError",
                    e.getMessage()
            );

            request.setAttribute(
                    "sconto",
                    sconto
            );

            showForm(
                    request,
                    response,
                    sconto
            );
        }
    }

    /* Aggiorna i record interessati nel database. */

    private void updateDiscount(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int idSconto =
                parsePositiveInt(
                        request.getParameter("id")
                );

        if (idSconto <= 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Identificativo sconto non valido."
            );
            return;
        }

        ScontoBean sconto =
                buildFromRequest(request);

        if (sconto == null) {
            /*
             * Manteniamo la pagina in modalità modifica anche quando
             * la validazione fallisce. I valori digitati sono già
             * conservati negli attributi old*.
             */
            ScontoBean invalidForm = new ScontoBean();
            invalidForm.setIdSconto(idSconto);
            showForm(request, response, invalidForm);
            return;
        }

        sconto.setIdSconto(idSconto);

        try {
            if (!scontoDAO.update(sconto)) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            request.getSession()
                    .setAttribute(
                            "adminDiscountMessage",
                            "Sconto aggiornato correttamente."
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/discounts"
            );

        } catch (SQLException e) {
            request.setAttribute(
                    "adminError",
                    e.getMessage()
            );

            showForm(
                    request,
                    response,
                    sconto
            );
        }
    }

    /* Elimina o scollega i record richiesti rispettando i vincoli di integrità. */

    private void deleteDiscount(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int idSconto =
                parsePositiveInt(
                        request.getParameter("id")
                );

        if (idSconto <= 0) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Identificativo sconto non valido."
            );
            return;
        }

        try {
            int prodottiAssociati =
                    scontoDAO.countAssignedProducts(
                            idSconto
                    );

            boolean deleted =
                    scontoDAO.deleteAndDetach(
                            idSconto
                    );

            if (!deleted) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            String message =
                    prodottiAssociati > 0
                            ? "Sconto eliminato e rimosso da "
                              + prodottiAssociati
                              + " prodotti."
                            : "Sconto eliminato correttamente.";

            request.getSession()
                    .setAttribute(
                            "adminDiscountMessage",
                            message
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/discounts"
            );

        } catch (SQLException e) {
            throw new ServletException(
                    "Errore durante l'eliminazione dello sconto.",
                    e
            );
        }
    }

    /* Implementa l’operazione buildFromRequest usata dalla logica applicativa. */

    private ScontoBean buildFromRequest(
            HttpServletRequest request) {

        String percentualeValue =
                trim(
                        request.getParameter(
                                "percentuale"
                        )
                );

        String dataInizioValue =
                trim(
                        request.getParameter(
                                "dataInizio"
                        )
                );

        String dataFineValue =
                trim(
                        request.getParameter(
                                "dataFine"
                        )
                );

        request.setAttribute(
                "oldPercentuale",
                percentualeValue
        );

        request.setAttribute(
                "oldDataInizio",
                dataInizioValue
        );

        request.setAttribute(
                "oldDataFine",
                dataFineValue
        );

        try {
            BigDecimal percentuale =
                    new BigDecimal(
                            percentualeValue
                    );

            LocalDate dataInizio =
                    LocalDate.parse(
                            dataInizioValue
                    );

            LocalDate dataFine =
                    LocalDate.parse(
                            dataFineValue
                    );

            if (percentuale.compareTo(BigDecimal.ZERO) <= 0
                    || percentuale.compareTo(
                            new BigDecimal("100")
                    ) > 0) {

                request.setAttribute(
                        "adminError",
                        "La percentuale deve essere maggiore di 0 e non superiore a 100."
                );

                return null;
            }

            if (dataFine.isBefore(dataInizio)) {
                request.setAttribute(
                        "adminError",
                        "La data di fine non può precedere la data di inizio."
                );

                return null;
            }

            ScontoBean sconto =
                    new ScontoBean();

            sconto.setPercentuale(
                    percentuale
            );

            sconto.setDataInizio(
                    Date.valueOf(dataInizio)
            );

            sconto.setDataFine(
                    Date.valueOf(dataFine)
            );

            return sconto;

        } catch (NumberFormatException
                 | DateTimeParseException
                 | NullPointerException e) {

            request.setAttribute(
                    "adminError",
                    "Compila correttamente percentuale e date."
            );

            return null;
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

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null
                ? null
                : value.trim();
    }
}

package controller;

import dao.UtenteDAO;
import model.UtenteBean;
import util.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/account/password")
public class PasswordChangeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)\\S{8,20}$");

    private final UtenteDAO utenteDAO =
            new UtenteDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        UtenteBean utente =
                getAuthenticatedUser(
                        request,
                        response
                );

        if (utente == null) {
            return;
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/account/change-password.jsp"
        ).forward(
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

        UtenteBean sessionUser =
                getAuthenticatedUser(
                        request,
                        response
                );

        if (sessionUser == null) {
            return;
        }

        String currentPassword =
                request.getParameter("currentPassword");

        String newPassword =
                request.getParameter("newPassword");

        String confirmPassword =
                request.getParameter("confirmPassword");

        String validationError =
                validate(
                        currentPassword,
                        newPassword,
                        confirmPassword
                );

        if (validationError != null) {

            request.setAttribute(
                    "passwordError",
                    validationError
            );

            forwardForm(
                    request,
                    response
            );

            return;
        }

        try {

            /*
             * La password non è salvata nell'oggetto di sessione:
             * il LoginServlet la imposta a null.
             * Per questo recuperiamo sempre l'hash aggiornato dal DB.
             */
            UtenteBean dbUser =
                    utenteDAO.findById(
                            sessionUser.getIdUtente()
                    );

            if (dbUser == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            if (!SecurityUtils.verifyPassword(
                    currentPassword,
                    dbUser.getPassword())) {

                request.setAttribute(
                        "passwordError",
                        "La password attuale non è corretta."
                );

                forwardForm(
                        request,
                        response
                );

                return;
            }

            if (SecurityUtils.verifyPassword(
                    newPassword,
                    dbUser.getPassword())) {

                request.setAttribute(
                        "passwordError",
                        "La nuova password deve essere diversa da quella attuale."
                );

                forwardForm(
                        request,
                        response
                );

                return;
            }

            String newHash =
                    SecurityUtils.hashPassword(
                            newPassword
                    );

            if (!utenteDAO.updatePassword(
                    sessionUser.getIdUtente(),
                    newHash)) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            /*
             * Manteniamo la sessione autenticata.
             * La password non viene mai inserita nella sessione.
             */
            request.getSession()
                    .setAttribute(
                            "profileMessage",
                            "Password aggiornata correttamente."
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/account"
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante il cambio password.",
                    e
            );
        }
    }

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private String validate(
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        if (currentPassword == null
                || currentPassword.isBlank()) {

            return "Inserisci la password attuale.";
        }

        if (newPassword == null
                || !PASSWORD_PATTERN.matcher(newPassword).matches()) {

            return "La nuova password deve avere 8-20 caratteri, almeno una maiuscola e un numero, senza spazi.";
        }

        if (confirmPassword == null
                || !newPassword.equals(confirmPassword)) {

            return "Le due nuove password non coincidono.";
        }

        return null;
    }

    private UtenteBean getAuthenticatedUser(
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

        return utente;
    }

    /* Prepara gli attributi necessari e inoltra la richiesta alla JSP del form. */

    private void forwardForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/account/change-password.jsp"
        ).forward(
                request,
                response
        );
    }
}

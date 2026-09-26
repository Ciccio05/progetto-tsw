package controller;

import dao.UtenteDAO;
import model.UtenteBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.regex.Pattern;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/account/edit")
public class ProfileEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}' -]{1,49}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9 .-]{7,20}$");

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

        request.setAttribute(
                "profilo",
                utente
        );

        forwardForm(
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

        String nome =
                trim(request.getParameter("nome"));

        String cognome =
                trim(request.getParameter("cognome"));

        String email =
                normalizeEmail(
                        request.getParameter("email")
                );

        String dataNascitaValue =
                trim(request.getParameter("dataNascita"));

        String indirizzo =
                trim(request.getParameter("indirizzo"));

        String telefono =
                trim(request.getParameter("telefono"));

        preserveValues(
                request,
                nome,
                cognome,
                email,
                dataNascitaValue,
                indirizzo,
                telefono
        );

        Date dataNascita;

        try {
            dataNascita =
                    parseOptionalDate(
                            dataNascitaValue
                    );

        } catch (DateTimeParseException e) {

            request.setAttribute(
                    "profileError",
                    "La data di nascita non è valida."
            );

            forwardForm(
                    request,
                    response
            );

            return;
        }

        String validationError =
                validate(
                        nome,
                        cognome,
                        email,
                        dataNascita,
                        indirizzo,
                        telefono
                );

        if (validationError != null) {

            request.setAttribute(
                    "profileError",
                    validationError
            );

            forwardForm(
                    request,
                    response
            );

            return;
        }

        try {

            if (utenteDAO.emailExistsForOtherUser(
                    email,
                    sessionUser.getIdUtente())) {

                request.setAttribute(
                        "profileError",
                        "Questa email è già utilizzata da un altro account."
                );

                forwardForm(
                        request,
                        response
                );

                return;
            }

            UtenteBean updated =
                    new UtenteBean();

            updated.setIdUtente(
                    sessionUser.getIdUtente()
            );

            updated.setTipoUtente(
                    sessionUser.getTipoUtente()
            );

            updated.setNomeUtente(
                    nome
            );

            updated.setCognomeUtente(
                    cognome
            );

            updated.setEmail(
                    email
            );

            updated.setDataNascita(
                    dataNascita
            );

            updated.setIndirizzoUtente(
                    indirizzo
            );

            updated.setNumeroTelefono(
                    emptyToNull(telefono)
            );

            if (!utenteDAO.updateProfile(updated)) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );

                return;
            }

            /*
             * Aggiorniamo l'oggetto già presente in sessione:
             * password e ruolo non vengono toccati.
             */
            sessionUser.setNomeUtente(
                    updated.getNomeUtente()
            );

            sessionUser.setCognomeUtente(
                    updated.getCognomeUtente()
            );

            sessionUser.setEmail(
                    updated.getEmail()
            );

            sessionUser.setDataNascita(
                    updated.getDataNascita()
            );

            sessionUser.setIndirizzoUtente(
                    updated.getIndirizzoUtente()
            );

            sessionUser.setNumeroTelefono(
                    updated.getNumeroTelefono()
            );

            request.getSession()
                    .setAttribute(
                            "profileMessage",
                            "Dati personali aggiornati correttamente."
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/account"
            );

        } catch (SQLIntegrityConstraintViolationException e) {

            request.setAttribute(
                    "profileError",
                    "Questa email è già utilizzata da un altro account."
            );

            forwardForm(
                    request,
                    response
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante l'aggiornamento del profilo.",
                    e
            );
        }
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

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private String validate(
            String nome,
            String cognome,
            String email,
            Date dataNascita,
            String indirizzo,
            String telefono) {

        if (nome == null
                || !NAME_PATTERN.matcher(nome).matches()) {

            return "Inserisci un nome valido.";
        }

        if (cognome == null
                || !NAME_PATTERN.matcher(cognome).matches()) {

            return "Inserisci un cognome valido.";
        }

        if (email == null
                || !EMAIL_PATTERN.matcher(email).matches()) {

            return "Inserisci un indirizzo email valido.";
        }

        if (dataNascita != null
                && dataNascita.toLocalDate().isAfter(LocalDate.now())) {

            return "La data di nascita non può essere nel futuro.";
        }

        if (indirizzo == null
                || indirizzo.length() < 3
                || indirizzo.length() > 150) {

            return "Inserisci un indirizzo valido.";
        }

        if (telefono != null
                && !telefono.isBlank()
                && !PHONE_PATTERN.matcher(telefono).matches()) {

            return "Inserisci un numero di telefono valido.";
        }

        return null;
    }

    /* Converte una data opzionale nel tipo SQL Date. */

    private Date parseOptionalDate(
            String value)
            throws DateTimeParseException {

        if (value == null || value.isBlank()) {
            return null;
        }

        return Date.valueOf(
                LocalDate.parse(value)
        );
    }

    /* Conserva nella request i valori inseriti per ripopolare il form dopo un errore. */

    private void preserveValues(
            HttpServletRequest request,
            String nome,
            String cognome,
            String email,
            String dataNascita,
            String indirizzo,
            String telefono) {

        request.setAttribute(
                "oldNome",
                nome
        );

        request.setAttribute(
                "oldCognome",
                cognome
        );

        request.setAttribute(
                "oldEmail",
                email
        );

        request.setAttribute(
                "oldDataNascita",
                dataNascita
        );

        request.setAttribute(
                "oldIndirizzo",
                indirizzo
        );

        request.setAttribute(
                "oldTelefono",
                telefono
        );
    }

    /* Prepara gli attributi necessari e inoltra la richiesta alla JSP del form. */

    private void forwardForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/account/edit-profile.jsp"
        ).forward(
                request,
                response
        );
    }

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null
                ? null
                : value.trim();
    }

    /* Normalizza l’indirizzo email eliminando spazi esterni e convertendolo in minuscolo. */

    private String normalizeEmail(String value) {
        String email = trim(value);

        return email == null
                ? null
                : email.toLowerCase(Locale.ROOT);
    }

    /* Converte una stringa vuota in null per semplificare la persistenza dei campi opzionali. */

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

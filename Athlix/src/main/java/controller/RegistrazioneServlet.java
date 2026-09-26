package controller;

import dao.UtenteDAO;
import model.UtenteBean;
import util.SecurityUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.regex.Pattern;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/register")
public class RegistrazioneServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}' -]{1,49}$");
    private static final Pattern CITY_PATTERN =
            Pattern.compile("^[\\p{L}][\\p{L}' .-]{1,49}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern CAP_PATTERN = Pattern.compile("^\\d{5}$");
    private static final Pattern PROVINCIA_PATTERN = Pattern.compile("^[A-Za-z]{2}$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)\\S{8,20}$");

    private final UtenteDAO utenteDAO = new UtenteDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = trim(request.getParameter("nome"));
        String cognome = trim(request.getParameter("cognome"));
        String email = normalizeEmail(request.getParameter("email"));
        String rawPassword = request.getParameter("password");
        String citta = trim(request.getParameter("citta"));
        String cap = trim(request.getParameter("cap"));
        String provincia = trim(request.getParameter("provincia"));

        preserveValues(request, nome, cognome, email, citta, cap, provincia);

        String validationError = validate(nome, cognome, email, rawPassword, citta, cap, provincia);
        if (validationError != null) {
            request.setAttribute("registrationError", validationError);
            request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
            return;
        }

        try {
            if (utenteDAO.emailExists(email)) {
                request.setAttribute("registrationError", "Questa email è già registrata.");
                request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
                return;
            }

            UtenteBean utente = new UtenteBean();
            utente.setTipoUtente("Cliente");
            utente.setNomeUtente(nome);
            utente.setCognomeUtente(cognome);
            utente.setEmail(email);
            utente.setPassword(SecurityUtils.hashPassword(rawPassword));
            utente.setIndirizzoUtente(citta + ", " + cap + " (" + provincia.toUpperCase() + ")");

            utenteDAO.save(utente);
            response.sendRedirect(request.getContextPath() + "/login?success=registered");

        } catch (SQLIntegrityConstraintViolationException e) {
            request.setAttribute("registrationError", "Questa email è già registrata.");
            request.getRequestDispatcher("/registrazione.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Errore durante la registrazione.", e);
        }
    }

    /* Valida i dati ricevuti e restituisce un messaggio di errore, oppure null se i dati sono corretti. */

    private String validate(
            String nome,
            String cognome,
            String email,
            String password,
            String citta,
            String cap,
            String provincia) {

        if (nome == null || !NAME_PATTERN.matcher(nome).matches()) {
            return "Inserisci un nome valido.";
        }
        if (cognome == null || !NAME_PATTERN.matcher(cognome).matches()) {
            return "Inserisci un cognome valido.";
        }
        if (citta == null || !CITY_PATTERN.matcher(citta).matches()) {
            return "Inserisci una città valida.";
        }
        if (cap == null || !CAP_PATTERN.matcher(cap).matches()) {
            return "Il CAP deve contenere esattamente 5 cifre.";
        }
        if (provincia == null || !PROVINCIA_PATTERN.matcher(provincia).matches()) {
            return "La provincia deve essere una sigla di 2 lettere.";
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            return "Inserisci un indirizzo email valido.";
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            return "La password deve avere 8-20 caratteri, almeno una maiuscola e un numero, senza spazi.";
        }
        return null;
    }

    /* Conserva nella request i valori inseriti per ripopolare il form dopo un errore. */

    private void preserveValues(
            HttpServletRequest request,
            String nome,
            String cognome,
            String email,
            String citta,
            String cap,
            String provincia) {
        request.setAttribute("oldNome", nome);
        request.setAttribute("oldCognome", cognome);
        request.setAttribute("oldEmail", email);
        request.setAttribute("oldCitta", citta);
        request.setAttribute("oldCap", cap);
        request.setAttribute("oldProvincia", provincia);
    }

    /* Rimuove gli spazi iniziali e finali gestendo in sicurezza i valori null. */

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    /* Normalizza l’indirizzo email eliminando spazi esterni e convertendolo in minuscolo. */

    private String normalizeEmail(String email) {
        String value = trim(email);
        return value == null ? null : value.toLowerCase();
    }
}

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
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UtenteDAO utenteDAO = new UtenteDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            UtenteBean utente = (UtenteBean) session.getAttribute("utente");

            if (utente != null) {
                response.sendRedirect(
                        request.getContextPath() + (utente.isAdmin() ? "/admin" : "/account")
                );
                return;
            }
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = normalizeEmail(request.getParameter("email"));
        String password = request.getParameter("password");
        String redirect = normalizeRedirect(request.getParameter("redirect"));

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()
                || password == null || password.isBlank()) {
            response.sendRedirect(loginErrorUrl(request, "invalid", redirect));
            return;
        }

        try {
            UtenteBean utente = utenteDAO.findByEmail(email);

            if (utente == null || !SecurityUtils.verifyPassword(password, utente.getPassword())) {
                response.sendRedirect(loginErrorUrl(request, "credentials", redirect));
                return;
            }

            utente.setPassword(null);

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);
            session.setMaxInactiveInterval(30 * 60);

            String destination;

            if (utente.isAdmin()) {
                destination = "/admin";
            } else {
                destination = destinationForCustomer(redirect);
            }

            response.sendRedirect(request.getContextPath() + destination);

        } catch (SQLException e) {
            throw new ServletException("Errore durante il login.", e);
        }
    }

    /* Normalizza l’indirizzo email eliminando spazi esterni e convertendolo in minuscolo. */

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    /* Accetta soltanto destinazioni interne previste, evitando redirect arbitrari. */

    private String normalizeRedirect(String redirect) {
        if ("account".equals(redirect)
                || "cart".equals(redirect)
                || "checkout".equals(redirect)
                || "orders".equals(redirect)
                || "admin".equals(redirect)) {
            return redirect;
        }
        return null;
    }

    /* Traduce la destinazione logica richiesta nel percorso interno del cliente. */

    private String destinationForCustomer(String redirect) {
        if ("account".equals(redirect)) return "/account";
        if ("cart".equals(redirect)) return "/cart";
        if ("checkout".equals(redirect)) return "/checkout";
        if ("orders".equals(redirect)) return "/orders";
        return "/home";
    }

    /* Costruisce l’URL di ritorno alla pagina di login mantenendo l’eventuale destinazione. */

    private String loginErrorUrl(HttpServletRequest request, String error, String redirect) {
        String url = request.getContextPath() + "/login?error=" + error;
        if (redirect != null) {
            url += "&redirect=" + redirect;
        }
        return url;
    }
}

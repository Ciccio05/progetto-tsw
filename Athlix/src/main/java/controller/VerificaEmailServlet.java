package controller;

import com.google.gson.JsonObject;
import dao.UtenteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/check-email")
public class VerificaEmailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UtenteDAO utenteDAO = new UtenteDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        email = email == null ? "" : email.trim().toLowerCase();

        JsonObject json = new JsonObject();

        if (!EMAIL_PATTERN.matcher(email).matches()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            json.addProperty("valida", false);
            json.addProperty("disponibile", false);

            response.getWriter().write(json.toString());
            return;
        }

        try {

            boolean disponibile =
                    !utenteDAO.emailExists(email);

            json.addProperty("valida", true);
            json.addProperty("disponibile", disponibile);

            response.getWriter().write(json.toString());

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante la verifica dell'email.",
                    e
            );
        }
    }
}

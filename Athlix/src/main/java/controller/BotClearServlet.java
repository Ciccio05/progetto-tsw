package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/bot/clear")
public class BotClearServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final Gson gson =
            new Gson();

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException {

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.removeAttribute(
                    "botHistory"
            );
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType(
                "application/json;charset=UTF-8"
        );

        JsonObject json =
                new JsonObject();

        json.addProperty(
                "success",
                true
        );

        gson.toJson(
                json,
                response.getWriter()
        );
    }
}

package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.BotMessage;
import model.BotReply;
import model.UtenteBean;
import service.BotService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/bot/message")
public class BotServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int MAX_HISTORY_MESSAGES = 20;
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final Gson gson =
            new Gson();

    private final BotService botService =
            new BotService();

    /* Gestisce le richieste POST, valida i dati e applica l’operazione richiesta. */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        response.setCharacterEncoding("UTF-8");
        response.setContentType(
                "application/json;charset=UTF-8"
        );

        String message =
                request.getParameter("message");

        JsonObject json =
                new JsonObject();

        if (message == null
                || message.trim().isEmpty()) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            json.addProperty(
                    "success",
                    false
            );

            json.addProperty(
                    "message",
                    "Scrivi un messaggio."
            );

            gson.toJson(
                    json,
                    response.getWriter()
            );

            return;
        }

        message = message.trim();

        if (message.length() > MAX_MESSAGE_LENGTH) {

            response.setStatus(
                    HttpServletResponse.SC_BAD_REQUEST
            );

            json.addProperty(
                    "success",
                    false
            );

            json.addProperty(
                    "message",
                    "Il messaggio può contenere al massimo 500 caratteri."
            );

            gson.toJson(
                    json,
                    response.getWriter()
            );

            return;
        }

        HttpSession session =
                request.getSession();

        UtenteBean utente =
                (UtenteBean)
                        session.getAttribute(
                                "utente"
                        );

        try {

            BotReply reply =
                    botService.reply(
                            message,
                            utente
                    );

            List<BotMessage> history =
                    getHistory(session);

            history.add(
                    new BotMessage(
                            "utente",
                            message
                    )
            );

            history.add(
                    new BotMessage(
                            "bot",
                            reply.getTesto()
                    )
            );

            trimHistory(history);

            json.addProperty(
                    "success",
                    true
            );

            json.addProperty(
                    "reply",
                    reply.getTesto()
            );

            if (reply.getActionLabel() != null
                    && reply.getActionUrl() != null) {

                json.addProperty(
                        "actionLabel",
                        reply.getActionLabel()
                );

                json.addProperty(
                        "actionUrl",
                        request.getContextPath()
                                + reply.getActionUrl()
                );
            }

            gson.toJson(
                    json,
                    response.getWriter()
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante la risposta del bot.",
                    e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private List<BotMessage> getHistory(
            HttpSession session) {

        List<BotMessage> history =
                (List<BotMessage>)
                        session.getAttribute(
                                "botHistory"
                        );

        if (history == null) {
            history = new ArrayList<>();

            session.setAttribute(
                    "botHistory",
                    history
            );
        }

        return history;
    }

    /* Limita la cronologia del bot per evitare che la sessione cresca senza controllo. */

    private void trimHistory(
            List<BotMessage> history) {

        while (history.size()
                > MAX_HISTORY_MESSAGES) {

            history.remove(0);
        }
    }
}

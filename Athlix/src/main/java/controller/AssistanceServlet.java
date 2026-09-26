package controller;

import model.BotMessage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/assistance")
public class AssistanceServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession();

        @SuppressWarnings("unchecked")
        List<BotMessage> history =
                (List<BotMessage>)
                        session.getAttribute(
                                "botHistory"
                        );

        if (history == null) {
            history = new ArrayList<>();

            history.add(
                    new BotMessage(
                            "bot",
                            "Ciao! Sono l'assistente AthliX. Posso aiutarti con ordini, spedizioni, prodotti, carrello, account, pagamenti e recensioni."
                    )
            );

            session.setAttribute(
                    "botHistory",
                    history
            );
        }

        request.setAttribute(
                "botHistory",
                history
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/support/assistance.jsp"
        ).forward(
                request,
                response
        );
    }
}

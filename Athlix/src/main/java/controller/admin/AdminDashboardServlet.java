package controller.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/admin")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp")
                .forward(request, response);
    }
}

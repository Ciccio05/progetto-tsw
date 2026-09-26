package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ProductDAO productDAO = new ProductDAO();

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Category> categories = categoryDAO.findAll();
            List<Product> featuredProducts = productDAO.findFeatured(4);

            request.setAttribute("categories", categories);
            request.setAttribute("featuredProducts", featuredProducts);
            request.getRequestDispatcher("/WEB-INF/views/home/index.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Errore durante il caricamento della home.", e);
        }
    }
}

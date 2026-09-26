package controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dao.ProductDAO;
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
@WebServlet("/search-products")
public class SearchProductServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProductDAO productDAO;

    /* Inizializza le dipendenze utilizzate dal servlet. */
    @Override
    public void init() {
        productDAO = new ProductDAO();
    }

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Il parametro scritto dall'utente
        String query = request.getParameter("q");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonArray jsonArray = new JsonArray();

        /*
         * Se la ricerca è vuota oppure ha meno di
         * 2 caratteri restituiamo semplicemente [].
         */
        if (query == null || query.trim().length() < 2) {
            response.getWriter().write(jsonArray.toString());
            return;
        }

        query = query.trim();

        try {

            List<Product> products =
                    productDAO.searchByName(query);

            for (Product product : products) {

                JsonObject jsonProduct = new JsonObject();

                jsonProduct.addProperty(
                        "id",
                        product.getIdProdotto()
                );

                jsonProduct.addProperty(
                        "nome",
                        product.getNome()
                );

                jsonProduct.addProperty(
                        "categoria",
                        product.getNomeCategoria()
                );

                jsonProduct.addProperty(
                        "prezzo",
                        product.getPrezzoFinale()
                );

                jsonArray.add(jsonProduct);
            }

            response.getWriter().write(
                    jsonArray.toString()
            );

        } catch (SQLException e) {

            throw new ServletException(
                    "Errore durante la ricerca dei prodotti.",
                    e
            );
        }
    }
}

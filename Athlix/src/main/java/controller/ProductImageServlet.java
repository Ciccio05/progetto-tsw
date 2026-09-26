package controller;

import service.ProductImageStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/* RUOLO DELLA CLASSE: Servlet/controller MVC: riceve la richiesta HTTP, coordina DAO/servizi e seleziona la risposta. */
@WebServlet("/product-image/*")
public class ProductImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /* Gestisce le richieste GET e prepara i dati necessari alla vista. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = pathInfo.substring(1);
        Path file = ProductImageStorage.resolveUploadedFile(fileName);

        if (file == null || !Files.isRegularFile(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(ProductImageStorage.contentTypeFor(fileName));
        response.setContentLengthLong(Files.size(file));
        response.setHeader("Cache-Control", "public, max-age=86400");
        response.setHeader("X-Content-Type-Options", "nosniff");

        try (var input = Files.newInputStream(file);
             var output = response.getOutputStream()) {
            input.transferTo(output);
        }
    }
}

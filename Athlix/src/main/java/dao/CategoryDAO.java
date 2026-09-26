package dao;

import it.athlix.database.DatabaseConnection;
import model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class CategoryDAO {

    public List<Category> findAll() throws SQLException {

        List<Category> categories = new ArrayList<>();

        String sql =
                "SELECT ID_categoria, Nome_categoria " +
                "FROM CATEGORIA_SPORT " +
                "ORDER BY Nome_categoria";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Category category = new Category();

                category.setIdCategoria(
                        resultSet.getInt("ID_categoria")
                );

                category.setNomeCategoria(
                        resultSet.getString("Nome_categoria")
                );

                categories.add(category);
            }
        }

        return categories;
    }
}

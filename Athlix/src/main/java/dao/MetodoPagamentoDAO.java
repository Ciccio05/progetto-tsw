package dao;

import it.athlix.database.DatabaseConnection;
import model.MetodoPagamentoBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class MetodoPagamentoDAO {

    public List<MetodoPagamentoBean> findAll() throws SQLException {
        List<MetodoPagamentoBean> methods = new ArrayList<>();

        String sql = """
            SELECT ID_metodo, Tipo_carta
            FROM METODO_PAGAMENTO
            ORDER BY ID_metodo
        """;

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MetodoPagamentoBean method = new MetodoPagamentoBean();
                method.setIdMetodo(rs.getInt("ID_metodo"));
                method.setTipoCarta(rs.getString("Tipo_carta"));
                methods.add(method);
            }
        }

        return methods;
    }
}

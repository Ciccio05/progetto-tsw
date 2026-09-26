package dao;

import it.athlix.database.DatabaseConnection;
import model.UtenteBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/* RUOLO DELLA CLASSE: DAO: incapsula l’accesso al database tramite query parametrizzate e PreparedStatement. */
public class UtenteDAO {

    public UtenteBean findByEmail(String email) throws SQLException {
        String sql = """
            SELECT ID_utente, Tipo_utente, Nome_utente, Cognome_utente,
                   Email, Password, Data_nascita, Indirizzo_utente, Numero_telefono
            FROM UTENTE
            WHERE LOWER(Email) = LOWER(?)
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizeEmail(email));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUtente(resultSet);
                }
            }
        }

        return null;
    }

    /* Esegue una ricerca nel database e restituisce i dati corrispondenti. */

    public UtenteBean findById(int idUtente) throws SQLException {
        if (idUtente <= 0) {
            return null;
        }

        String sql = """
            SELECT ID_utente, Tipo_utente, Nome_utente, Cognome_utente,
                   Email, Password, Data_nascita, Indirizzo_utente, Numero_telefono
            FROM UTENTE
            WHERE ID_utente = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idUtente);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUtente(resultSet);
                }
            }
        }

        return null;
    }

    /* Salva i dati nel database dopo la validazione. */

    public void save(UtenteBean utente) throws SQLException {
        String sql = """
            INSERT INTO UTENTE
                (Tipo_utente, Nome_utente, Cognome_utente, Email, Password,
                 Data_nascita, Indirizzo_utente, Numero_telefono)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, utente.getTipoUtente());
            statement.setString(2, utente.getNomeUtente());
            statement.setString(3, utente.getCognomeUtente());
            statement.setString(4, normalizeEmail(utente.getEmail()));
            statement.setString(5, utente.getPassword());
            statement.setDate(6, utente.getDataNascita());
            statement.setString(7, utente.getIndirizzoUtente());
            statement.setString(8, utente.getNumeroTelefono());

            statement.executeUpdate();
        }
    }

    /* Aggiorna i record interessati nel database. */

    public boolean updateProfile(UtenteBean utente) throws SQLException {
        if (utente == null || utente.getIdUtente() <= 0) {
            return false;
        }

        String sql = """
            UPDATE UTENTE
            SET Nome_utente = ?,
                Cognome_utente = ?,
                Email = ?,
                Data_nascita = ?,
                Indirizzo_utente = ?,
                Numero_telefono = ?
            WHERE ID_utente = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, utente.getNomeUtente());
            statement.setString(2, utente.getCognomeUtente());
            statement.setString(3, normalizeEmail(utente.getEmail()));
            statement.setDate(4, utente.getDataNascita());
            statement.setString(5, utente.getIndirizzoUtente());
            statement.setString(6, emptyToNull(utente.getNumeroTelefono()));
            statement.setInt(7, utente.getIdUtente());

            return statement.executeUpdate() > 0;
        }
    }

    /* Aggiorna i record interessati nel database. */

    public boolean updatePassword(
            int idUtente,
            String passwordHash) throws SQLException {

        if (idUtente <= 0
                || passwordHash == null
                || passwordHash.isBlank()) {
            return false;
        }

        String sql = """
            UPDATE UTENTE
            SET Password = ?
            WHERE ID_utente = ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setInt(2, idUtente);

            return statement.executeUpdate() > 0;
        }
    }

    /* Implementa l’operazione emailExists usata dalla logica applicativa. */

    public boolean emailExists(String email) throws SQLException {
        String sql = """
            SELECT 1
            FROM UTENTE
            WHERE LOWER(Email) = LOWER(?)
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizeEmail(email));

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /* Implementa l’operazione emailExistsForOtherUser usata dalla logica applicativa. */

    public boolean emailExistsForOtherUser(
            String email,
            int idUtente) throws SQLException {

        String sql = """
            SELECT 1
            FROM UTENTE
            WHERE LOWER(Email) = LOWER(?)
              AND ID_utente <> ?
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, normalizeEmail(email));
            statement.setInt(2, idUtente);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    /* Converte una riga del ResultSet nel corrispondente bean di modello. */

    private UtenteBean mapUtente(ResultSet resultSet) throws SQLException {
        UtenteBean utente = new UtenteBean();

        utente.setIdUtente(resultSet.getInt("ID_utente"));
        utente.setTipoUtente(resultSet.getString("Tipo_utente"));
        utente.setNomeUtente(resultSet.getString("Nome_utente"));
        utente.setCognomeUtente(resultSet.getString("Cognome_utente"));
        utente.setEmail(resultSet.getString("Email"));
        utente.setPassword(resultSet.getString("Password"));
        utente.setDataNascita(resultSet.getDate("Data_nascita"));
        utente.setIndirizzoUtente(resultSet.getString("Indirizzo_utente"));
        utente.setNumeroTelefono(resultSet.getString("Numero_telefono"));

        return utente;
    }

    /* Normalizza l’indirizzo email eliminando spazi esterni e convertendolo in minuscolo. */

    private String normalizeEmail(String email) {
        return email == null
                ? null
                : email.trim().toLowerCase(Locale.ROOT);
    }

    /* Converte una stringa vuota in null per semplificare la persistenza dei campi opzionali. */

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

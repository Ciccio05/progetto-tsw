package it.athlix.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/* RUOLO DELLA CLASSE: Gestione del pool di connessioni e del ciclo di vita delle risorse database. */
public class DatabaseConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/athlix_db"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=Europe/Rome";

    /*
     * Opzionale: se non impostata si usa DEFAULT_URL.
     * Documentata nel file README.md.
     */
    private static final String URL =
            System.getProperty(
                    "ATHLIX_DB_URL",
                    DEFAULT_URL
            );

    private static final String USER =
            System.getProperty(
                    "ATHLIX_DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getProperty(
                    "ATHLIX_DB_PASSWORD"
            );

    private static final HikariDataSource dataSource;

    static {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new IllegalStateException(
                    "Password database non configurata. "
                    + "Imposta -DATHLIX_DB_PASSWORD=... nei VM arguments di Tomcat."
            );
        }

        HikariConfig config =
                new HikariConfig();

        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        config.setDriverClassName(
                "com.mysql.cj.jdbc.Driver"
        );

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        config.setConnectionTimeout(10000);

        config.setPoolName(
                "AthlixPool"
        );

        dataSource =
                new HikariDataSource(
                        config
                );
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection()
            throws SQLException {

        return dataSource.getConnection();
    }

    /* Chiude il pool di connessioni quando l’applicazione viene arrestata. */

    public static void closePool() {

        if (dataSource != null
                && !dataSource.isClosed()) {

            dataSource.close();
        }
    }
}

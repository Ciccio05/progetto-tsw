package it.athlix.database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
/* RUOLO DELLA CLASSE: Gestione del pool di connessioni e del ciclo di vita delle risorse database. */
public class DatabaseContextListener implements ServletContextListener {

    @Override
    /* Chiude le risorse database quando il contesto web viene distrutto. */
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseConnection.closePool();
    }
}

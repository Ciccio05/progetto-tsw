package model;

import java.io.Serializable;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class BotMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ruolo;
    private String testo;

    public BotMessage() {
    }

    public BotMessage(String ruolo, String testo) {
        this.ruolo = ruolo;
        this.testo = testo;
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
}

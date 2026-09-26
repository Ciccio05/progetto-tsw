package model;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class BotReply {

    private String testo;
    private String actionLabel;
    private String actionUrl;

    public BotReply() {
    }

    public BotReply(String testo) {
        this.testo = testo;
    }

    public BotReply(
            String testo,
            String actionLabel,
            String actionUrl) {

        this.testo = testo;
        this.actionLabel = actionLabel;
        this.actionUrl = actionUrl;
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public void setActionLabel(String actionLabel) {
        this.actionLabel = actionLabel;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
}

package model;

import java.math.BigDecimal;
import java.sql.Date;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class OrdineBean {

    private int idOrdine;
    private Date dataOrdine;
    private String stato;
    private BigDecimal totale;

    private String indirizzoSpedizione;
    private String statoSpedizione;
    private Date dataPartenza;
    private Date dataConsegna;

    public OrdineBean() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Date getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(Date dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public BigDecimal getTotale() {
        return totale;
    }

    public void setTotale(BigDecimal totale) {
        this.totale = totale;
    }

    public String getIndirizzoSpedizione() {
        return indirizzoSpedizione;
    }

    public void setIndirizzoSpedizione(String indirizzoSpedizione) {
        this.indirizzoSpedizione = indirizzoSpedizione;
    }

    public String getStatoSpedizione() {
        return statoSpedizione;
    }

    public void setStatoSpedizione(String statoSpedizione) {
        this.statoSpedizione = statoSpedizione;
    }

    public Date getDataPartenza() {
        return dataPartenza;
    }

    public void setDataPartenza(Date dataPartenza) {
        this.dataPartenza = dataPartenza;
    }

    public Date getDataConsegna() {
        return dataConsegna;
    }

    public void setDataConsegna(Date dataConsegna) {
        this.dataConsegna = dataConsegna;
    }

    public boolean isSpedito() {
        return "SPEDITA".equalsIgnoreCase(statoSpedizione)
                || "CONSEGNATA".equalsIgnoreCase(statoSpedizione);
    }

    public boolean isConsegnato() {
        return "CONSEGNATA".equalsIgnoreCase(statoSpedizione);
    }

    public boolean isAnnullato() {
        return "ANNULLATO".equalsIgnoreCase(stato)
                || "ANNULLATA".equalsIgnoreCase(statoSpedizione);
    }
}

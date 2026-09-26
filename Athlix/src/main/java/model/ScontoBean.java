package model;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class ScontoBean {

    private Integer idSconto;
    private BigDecimal percentuale;
    private Date dataInizio;
    private Date dataFine;

    public ScontoBean() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public Integer getIdSconto() {
        return idSconto;
    }

    public void setIdSconto(Integer idSconto) {
        this.idSconto = idSconto;
    }

    public BigDecimal getPercentuale() {
        return percentuale;
    }

    public void setPercentuale(BigDecimal percentuale) {
        this.percentuale = percentuale;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }

    public String getStato() {
        if (dataInizio == null || dataFine == null) {
            return "NON VALIDO";
        }

        LocalDate oggi = LocalDate.now();
        LocalDate inizio = dataInizio.toLocalDate();
        LocalDate fine = dataFine.toLocalDate();

        if (oggi.isBefore(inizio)) {
            return "PROGRAMMATO";
        }

        if (oggi.isAfter(fine)) {
            return "SCADUTO";
        }

        return "ATTIVO";
    }
}

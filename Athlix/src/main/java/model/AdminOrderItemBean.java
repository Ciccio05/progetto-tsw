package model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class AdminOrderItemBean {

    private int idProdotto;
    private String nomeProdotto;
    private int quantita;
    private BigDecimal prezzoAcquisto;
    private BigDecimal ivaAcquisto;

    public AdminOrderItemBean() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getPrezzoAcquisto() {
        return prezzoAcquisto;
    }

    public void setPrezzoAcquisto(BigDecimal prezzoAcquisto) {
        this.prezzoAcquisto = prezzoAcquisto;
    }

    public BigDecimal getIvaAcquisto() {
        return ivaAcquisto;
    }

    public void setIvaAcquisto(BigDecimal ivaAcquisto) {
        this.ivaAcquisto = ivaAcquisto;
    }

    public BigDecimal getSubtotale() {
        if (prezzoAcquisto == null || quantita <= 0) {
            return BigDecimal.ZERO;
        }

        return prezzoAcquisto
                .multiply(BigDecimal.valueOf(quantita))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

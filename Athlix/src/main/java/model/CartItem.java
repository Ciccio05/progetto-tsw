package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idCarrello;
    private int idProdotto;
    private String nomeProdotto;
    private String immagine;
    private BigDecimal prezzo;
    private BigDecimal iva;
    private int quantita;
    private BigDecimal percentualeSconto;

    public CartItem() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public int getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(int idCarrello) {
        this.idCarrello = idCarrello;
    }

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

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getPercentualeSconto() {
        return percentualeSconto;
    }

    public void setPercentualeSconto(BigDecimal percentualeSconto) {
        this.percentualeSconto = percentualeSconto;
    }

    public BigDecimal getPrezzoScontato() {
        if (prezzo == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        if (percentualeSconto == null || percentualeSconto.compareTo(BigDecimal.ZERO) <= 0) {
            return prezzo.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sconto = prezzo
                .multiply(percentualeSconto)
                .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        return prezzo.subtract(sconto).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotale() {
        return getPrezzoScontato()
                .multiply(BigDecimal.valueOf(quantita))
                .setScale(2, RoundingMode.HALF_UP);
    }
}

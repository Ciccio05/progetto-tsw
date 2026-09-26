package model;

import java.math.BigDecimal;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class Product {

    private int idProdotto;
    private String nome;
    private BigDecimal prezzo;
    private BigDecimal iva;
    private String descrizione;
    private String immagine;
    private boolean visibile = true;
    private boolean disponibilita;
    private int quantita;

    private int idCategoria;
    private Integer idSconto;

    // Dati ottenuti tramite JOIN, utili per il catalogo
    private String nomeCategoria;
    private BigDecimal percentualeSconto;
    private BigDecimal prezzoFinale;
    private BigDecimal mediaRecensioni;
    private int numeroRecensioni;

    public Product() {
    }

    public Product(
            int idProdotto,
            String nome,
            BigDecimal prezzo,
            BigDecimal iva,
            String descrizione,
            boolean disponibilita,
            int quantita,
            int idCategoria,
            Integer idSconto) {

        this.idProdotto = idProdotto;
        this.nome = nome;
        this.prezzo = prezzo;
        this.iva = iva;
        this.descrizione = descrizione;
        this.disponibilita = disponibilita;
        this.quantita = quantita;
        this.idCategoria = idCategoria;
        this.idSconto = idSconto;
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public int getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(int idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    public boolean isDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(boolean disponibilita) {
        this.disponibilita = disponibilita;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Integer getIdSconto() {
        return idSconto;
    }

    public void setIdSconto(Integer idSconto) {
        this.idSconto = idSconto;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public BigDecimal getPercentualeSconto() {
        return percentualeSconto;
    }

    public void setPercentualeSconto(BigDecimal percentualeSconto) {
        this.percentualeSconto = percentualeSconto;
    }

    public BigDecimal getPrezzoFinale() {
        return prezzoFinale;
    }

    public void setPrezzoFinale(BigDecimal prezzoFinale) {
        this.prezzoFinale = prezzoFinale;
    }

    public BigDecimal getMediaRecensioni() {
        return mediaRecensioni;
    }

    public void setMediaRecensioni(BigDecimal mediaRecensioni) {
        this.mediaRecensioni = mediaRecensioni;
    }

    public int getNumeroRecensioni() {
        return numeroRecensioni;
    }

    public void setNumeroRecensioni(int numeroRecensioni) {
        this.numeroRecensioni = numeroRecensioni;
    }
}

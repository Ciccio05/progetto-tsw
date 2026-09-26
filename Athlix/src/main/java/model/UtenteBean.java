package model;

import java.io.Serializable;
import java.sql.Date;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class UtenteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int idUtente;
    private String tipoUtente;
    private String nomeUtente;
    private String cognomeUtente;
    private String email;
    private String password;
    private Date dataNascita;
    private String indirizzoUtente;
    private String numeroTelefono;

    public UtenteBean() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getTipoUtente() {
        return tipoUtente;
    }

    public void setTipoUtente(String tipoUtente) {
        this.tipoUtente = tipoUtente;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public String getCognomeUtente() {
        return cognomeUtente;
    }

    public void setCognomeUtente(String cognomeUtente) {
        this.cognomeUtente = cognomeUtente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDataNascita() {
        return dataNascita;
    }

    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    public String getIndirizzoUtente() {
        return indirizzoUtente;
    }

    public void setIndirizzoUtente(String indirizzoUtente) {
        this.indirizzoUtente = indirizzoUtente;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public boolean isAdmin() {
        return "Admin".equalsIgnoreCase(tipoUtente);
    }
}

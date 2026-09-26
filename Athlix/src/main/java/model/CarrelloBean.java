package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/* RUOLO DELLA CLASSE: Bean di modello: rappresenta i dati dell’applicazione e li espone tramite proprietà JavaBean. */
public class CarrelloBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<CartItem> items = new ArrayList<>();
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shipping = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private int itemCount;

    public CarrelloBean() {
    }

    /* ACCESSORI JAVABEAN: getter e setter espongono le proprietà alle Servlet e alle JSP tramite Expression Language. */
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public void setShipping(BigDecimal shipping) {
        this.shipping = shipping == null ? BigDecimal.ZERO : shipping;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total == null ? BigDecimal.ZERO : total;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = Math.max(itemCount, 0);
    }

    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
}

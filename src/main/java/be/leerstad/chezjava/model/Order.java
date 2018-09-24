package be.leerstad.chezjava.model;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDate;

public class Order implements Serializable {
    private static final long serialVersionUID = -775207729966522625L;
    private static Logger logger = Logger.getLogger(Order.class.getName());
    private Item item;
    private LocalDate date;
    private Integer quantity;

    public Order(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
        date = LocalDate.now();
        logger.debug("Order Created:" + this.toString());
    }

    public Order(Item item, int quantity, LocalDate date) {
        this(item, quantity);
        this.date = date;
        logger.debug("Order Created:" + this.toString());

    }

    //
    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        if (getQuantity() != 0)
            quantity--;
    }

    public double getOrderPrice() {
        return getItem().getPrice() * getQuantity();
    }

    public Integer getQuantity() {
        return quantity;
    }

    //
    public void setQuantity(int qty) {
        quantity = qty;
    }

    public Item getItem() {
        return item;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getItemId() {
        return item.getItemID();
    }

    //
    public String getItemName() {
        return item.getItemName();
    }

    public Double getItemPrice() {
        return item.getPrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!getItem().equals(order.getItem())) return false;
        return getDate() != null ? getDate().equals(order.getDate()) : order.getDate() == null;
    }

    @Override
    public int hashCode() {
        int result = getItem().hashCode();
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Beverage: ").append(getItem()).append(" Qty: ").append(getQuantity()).append(" Date: ").append(getDate()).append(System.getProperty("line.separator"));
        return sb.toString();
    }
}

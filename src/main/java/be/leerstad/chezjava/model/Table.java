package be.leerstad.chezjava.model;

import be.leerstad.chezjava.database.DAOOrderMySql;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Formatter;

public class Table implements Serializable, Comparable<Table> {
    private static final long serialVersionUID = -5220343347493680894L;
    private static Logger logger = Logger.getLogger(Table.class.getName());
    private String name;
    private int tableNr;
    private Waiter waiter;
    private ArrayList<Order> orders = new ArrayList<>();

    public Table(String name, int tableNr) {
        this.name = name;
        this.tableNr = tableNr;
        logger.debug("Table Created:" + this.toString());

    }

    //
    public void addOrder(Order order) {
        if (this.waiter != null) {
            if (orders.contains(order)) {
                int addQuantity = order.getQuantity();
                int place = orders.indexOf(order);
                int oldQuantity = orders.get(place).getQuantity();
                int sum = oldQuantity + addQuantity;
                if (sum < 0)
                    sum = 0;
                orders.get(place).setQuantity(sum);
            } else
                orders.add(order);
        }
    }

    public double getTotalOrders() {
        return getOrders().stream().mapToDouble(Order::getOrderPrice).sum();
    }

    public boolean payOrders() {
        if (DAOOrderMySql.getInstance().addOrder(orders, this.getWaiter())) {
            logger.info("Orders to Pay");
            logger.info(this.toString());
            orders.clear();
            logger.debug(" Table Orders Cleared");
            this.setWaiter(null);
            logger.debug("Table's open Orders are payed ");
            return true;
        }
        return false;
    }

    // returns true when waiter is set from null to current user  or to null after payment
    public boolean setWaiter(Waiter waiter) {
        boolean result = false;
        if (this.getWaiter() == null) {
            this.waiter = waiter;
            result = true;
            logger.debug("Set Waiter to: " + this.waiter.toString());
        }
        if (waiter == null) {
            this.waiter = null;
            result = true;
            logger.debug("cleared Waiter:");

        }
        return result;
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public int getTableNr() {
        return tableNr;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public Order getOrder(String beverageName) {
        Order result = new Order(new Beverage(0, "Empty", 0), 0);

        for (Order o : orders) {
            if (o.getItem().getItemName().equals(reformatName(beverageName))) {
                result = o;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter fm = new Formatter(sb);

        fm.format("Date: %1$tF Time: %1$tT %n", LocalDateTime.now());
        fm.format("%-2s : %-20s %n", tableNr, name);
        if (this.waiter != null)
            fm.format("Waiter: %s %n", waiter);
        if (!(this.orders.isEmpty())) {
            fm.format("Saldo: %n");

            for (Order order : this.orders
                    ) {
                if (order.getQuantity() > 0)
                    fm.format("%-3dx %-20s %-5.2f€ %-5.2f€ %n", order.getQuantity(), order.getItemName(), order.getItemPrice(), order.getOrderPrice());
            }
            fm.format("%nTotal: %1.2f€", this.getTotalOrders());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        if (getTableNr() != table.getTableNr()) return false;
        if (!getName().equals(table.getName())) return false;
        if (getWaiter() != null ? !getWaiter().equals(table.getWaiter()) : table.getWaiter() != null) return false;
        return getOrders() != null ? getOrders().equals(table.getOrders()) : table.getOrders() == null;
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getTableNr();
        result = 31 * result + (getWaiter() != null ? getWaiter().hashCode() : 0);
        result = 31 * result + (getOrders() != null ? getOrders().hashCode() : 0);
        return result;
    }

    private String reformatName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    @Override
    public int compareTo(Table o) {
        if (!(this.getTableNr() == (o.getTableNr())))
            return this.getTableNr() - (o.getTableNr());
        return this.getName().compareTo(o.getName());
    }
}

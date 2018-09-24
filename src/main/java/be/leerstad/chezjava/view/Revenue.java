package be.leerstad.chezjava.view;

import be.leerstad.chezjava.model.Waiter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Revenue {
    private Waiter waiter;
    private SimpleDoubleProperty total;

    public Revenue(Waiter waiter, Double total) {
        this.waiter = waiter;
        this.total = new SimpleDoubleProperty();
        this.total.setValue(total);
    }

    public Waiter getWaiter() {
        return waiter;
    }

    public double getTotal() {
        return total.get();
    }

    public DoubleProperty totalProperty() {
        return total;
    }
}

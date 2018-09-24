package be.leerstad.chezjava.model;

import java.io.Serializable;

abstract class Item implements Comparable<Item>, Serializable {
    private static final long serialVersionUID = -2155147556310718101L;
    private String itemName;
    private int itemID;
    private double price;


    public Item(String itemName, int itemID, double price) {
        this.itemName = cleanName(itemName);
        this.itemID = itemID;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemID() {
        return itemID;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int compareTo(Item o) {
        return this.getItemName().compareTo(o.getItemName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return getItemName().equals(item.getItemName());
    }

    @Override
    public int hashCode() {
        return getItemName().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getItemName()).append(" ").append(getPrice());
        return sb.toString();
    }

    private String cleanName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }
}

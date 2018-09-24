package be.leerstad.chezjava.model;

import org.apache.log4j.Logger;

import java.io.Serializable;

public class Beverage extends Item implements Serializable {
    private static final long serialVersionUID = -3933837465084705752L;
    private static Logger logger = Logger.getLogger(Beverage.class.getName());


    public Beverage(int itemID, String itemName, double price) {
        super(itemName, itemID, price);
        logger.debug("Beverage Created:" + this.toString());
    }


}

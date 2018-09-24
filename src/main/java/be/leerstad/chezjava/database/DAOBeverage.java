package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Beverage;

import java.util.List;

public interface DAOBeverage {
    List<Beverage> getAllBeverages();

    Beverage getBeverage(int beverageID);

    Beverage getBeverage(String name);

}

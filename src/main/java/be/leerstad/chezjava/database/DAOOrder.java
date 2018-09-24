/*
 * Copyright (c) 2017. All code is used during Programmeren 3 module at CVO Leerstad
 */

package be.leerstad.chezjava.database;


import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.User;

import java.time.LocalDate;
import java.util.List;


public interface DAOOrder {
    List<Order> getAllSales();

    List<Order> getAllSales(LocalDate dateFrom, LocalDate dateTo);

    List<Order> getAllSales(User user);

    boolean addOrder(Order order, User user);
}

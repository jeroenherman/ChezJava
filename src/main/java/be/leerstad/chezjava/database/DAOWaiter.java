package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Waiter;

import java.util.List;

public interface DAOWaiter {
    List<Waiter> getAllWaiters();

    int getWaiterId(String firstName, String lastName);
}

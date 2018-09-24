package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Waiter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class DAOWaiterMySqlTest {
    @Test
    public void getAllWaiters() throws Exception {
        List<Waiter> waiters = DAOWaiterMySql.getInstance().getAllWaiters();
        assertFalse("List waiters is Empty", waiters.isEmpty());
    }

}
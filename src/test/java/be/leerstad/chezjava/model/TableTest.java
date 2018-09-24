package be.leerstad.chezjava.model;


import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class TableTest {
    Table t1;
    Table t2;
    Order o1;
    Order o2;
    Order o3;
    Order o4;

    Beverage b1;
    Beverage b2;
    Waiter w1;

    @Before
    public void setUp() throws Exception {
        w1 = new Waiter("Wout", "Peters", "password");
        t1 = new Table("Bar", 1);
        t2 = new Table("Bar", 1);
        b1 = new Beverage(1, "Cola", 2.00);
        b2 = new Beverage(2, "Stella", 2.00);
        o1 = new Order(b1, 1);
        o2 = new Order(b2, 2);
        o3 = new Order(b1, 4);
        // decrease order
        o4 = new Order (b2, -2);
    }

    @Test
    public void setWaiter() throws Exception {
        assertTrue("table waiter is not set", t1.setWaiter(w1));
        assertTrue("Waiter t1 is not wout", t1.getWaiter() == w1);
    }
    @Test
    public void equals() throws Exception {
        assertEquals("Tables are not equal", t1,t2);
        assertTrue(t1.hashCode()==t2.hashCode());
        t2 = new Table("London", 2);
        assertFalse("Tables are not equal", t1.equals(t2));
        TreeSet<Table> tables = new TreeSet<>();
        tables.add(t1);
        tables.add(t2);
        assertTrue("Bar is not first table",tables.first().equals(t1));

    }

    @Test
    public void addOrder() throws Exception {
        setWaiter();
        t1.addOrder(o1);
        t1.addOrder(o2);
        assertTrue("order o1 is not in tables", t1.getOrders().contains(o1));
        assertTrue("order o2 is not in tables", t1.getOrders().contains(o2));
        assertEquals("qty Stella is not 2", 2, t1.getOrders().get(1).getQuantity(), 0);

        t1.addOrder(o3);
        assertEquals("qty cola is not 5", 5, t1.getOrders().get(0).getQuantity(), 0);
        t1.addOrder(o3);
        assertEquals("qty cola is not 9", 9, t1.getOrders().get(0).getQuantity(), 0);
        t1.addOrder(o2);
        assertEquals("qty  stella is not 4 ", 4, t1.getOrders().get(1).getQuantity(), 0);
        // decrease order
        t1.addOrder(o4);
        assertEquals("qty  stella is not 2 ", 2, t1.getOrders().get(1).getQuantity(), 0);
        t1.addOrder(o4);
        assertEquals("qty  stella is not 0 ", 0, t1.getOrders().get(1).getQuantity(), 0);
        t1.addOrder(o4);
        assertEquals("qty  stella is not 0 ", 0, t1.getOrders().get(1).getQuantity(), 0);


    }

    @Test
    public void getOrdersByBeverageName() throws Exception {
        setWaiter();
        t1.addOrder(new Order(b1, 1));
        assertEquals("qty Cola is not equal to 1", 1, t1.getOrder("Cola").getQuantity(), 0);
        t1.addOrder(new Order(b1, 1));
        assertEquals("qty Cola is not equal to 2", 2, t1.getOrder("cola").getQuantity(), 0);
        t1.addOrder(new Order(b1, 1));
        assertEquals("qty Cola is not equal to 3", 3, t1.getOrder("Cola").getQuantity(), 0);
        assertEquals("qty Colo is not equal to 0", 0, t1.getOrder("colo").getQuantity(), 0);
        t1.addOrder(new Order(b2, 1));
        assertEquals("qty stella is not equal to 1", 1, t1.getOrder("stella").getQuantity(), 0);
        t1.addOrder(new Order(b2, 1));
        assertEquals("qty stella is not equal to 2", 2, t1.getOrder("Stella").getQuantity(), 0);
        t1.addOrder(new Order(b2, 5));
        assertEquals("qty stella is not equal to 7", 7, t1.getOrder("stella").getQuantity(), 0);
        t1.addOrder(new Order(b2, 2));
        assertEquals("qty stella is not equal to 9", 9, t1.getOrder("StElLa").getQuantity(), 0);
        t1.addOrder(new Order(b2, 1));
        assertEquals("qty stella is not equal to 10", 10, t1.getOrder("steLLA").getQuantity(), 0);

    }

    @Test
    public void getName() throws Exception {
        assertEquals("name is not Bar", "Bar", t1.getName());
    }

    @Test
    public void getTableNr() throws Exception {
        assertEquals("tablenr is not 1", 1, t1.getTableNr());
    }

    @Test
    public void testGetTotalSumOrders() throws Exception {
        assertTrue("waiter table is not set", t1.setWaiter(w1));
        t1.addOrder(new Order(b1, 2));
        System.out.println(t1);
        assertEquals("Total is not 4.0", 4.0, t1.getTotalOrders(), 0);
        t1.addOrder(new Order(b2, 1));
        assertEquals("Total is not 6.0", 6.0, t1.getTotalOrders(), 0);

    }

    @Test
    public void payOrders() throws Exception {
        addOrder();
        t1.payOrders();
        assertFalse("order o1  is in tables", t1.getOrders().contains(o1));
        assertFalse("order o2 is in tables", t1.getOrders().contains(o2));

    }

}
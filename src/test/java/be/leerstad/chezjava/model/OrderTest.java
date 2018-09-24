package be.leerstad.chezjava.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderTest {

    Item b1;
    Item b2;
    Order o1;
    Order o2;

    @Before
    public void setUp() throws Exception {
        b1 = new Beverage(1, "Cola", 2.00);
        b2 = new Beverage(2, "Stella", 2.00);
    }

    @Test
    public void testConstructor() {
        o1 = new Order(b1, 1);
        assertEquals("beverage name is not cola", "Cola", o1.getItem().getItemName());
        assertTrue("date is not now", o1.getDate().equals(LocalDate.now()));
        assertEquals("quantity is not 1", 1, o1.getQuantity(), 0);
        o2 = new Order(b2, 1);
        ;
        assertEquals("beverage name is not Stella", "Stella", o2.getItem().getItemName());
        assertTrue("date is not now", o2.getDate().equals(LocalDate.now()));

    }
    @Test
    public void testEqualsAndHashcode() {
        o1 = new Order(b1,2);
        o2 = new Order(b1,2);
        assertEquals("Orders are not the same",o1,o2);
        assertEquals("Orders are not the same",o2,o1);
        assertTrue(o1.hashCode()==o2.hashCode());
    }

    @Test
    public void increaseQuantity() throws Exception {
        o1 = new Order(b1, 1);
        assertEquals("quantity is not 1", o1.getQuantity(), 1, 0);
        o1.increaseQuantity();
        assertEquals("quantity is not 2", o1.getQuantity(), 2, 0);
        o1.increaseQuantity();
        assertEquals("quantity is not 3", o1.getQuantity(), 3, 0);

    }

    @Test
    public void decreaseQuantity() throws Exception {
        o1 = new Order(b1, 2);
        assertEquals("quantity is not 2", o1.getQuantity(), 2, 0);
        o1.decreaseQuantity();
        assertEquals("quantity is not 1", o1.getQuantity(), 1, 0);
        o1.decreaseQuantity();
        assertEquals("quantity is not 0", o1.getQuantity(), 0, 0);
        o1.decreaseQuantity();
        assertEquals("quantity is not 0", o1.getQuantity(), 0, 0);

    }

    @Test
    public void setQuantity() throws Exception {
        o1 = new Order(b1, 2);
        assertEquals("quantity is not 2", o1.getQuantity(), 2, 0);
        o1.setQuantity(4);
        assertEquals("quantity is not 4", o1.getQuantity(), 4, 0);
        decreaseQuantity();
        o1.setQuantity(4);
        assertEquals("quantity is not 4", o1.getQuantity(), 4, 0);


    }
}
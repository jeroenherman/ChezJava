package be.leerstad.chezjava.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeverageTest {
    Beverage b1;
    Beverage b2;
    Beverage b3;

    @Before
    public void setUp() throws Exception {
        b1 = new Beverage(1, "Leffe", 2.50);
        b2 = new Beverage(2, "Leffe", 3.50);
        b3 = new Beverage(3, "Cola", 1.50);
    }

    @Test
    public void testEquals() {
        assertTrue("b1 is not equals b2", b1.equals(b2));
        assertTrue("b2 is not equals b1", b1.equals(b2));
        assertTrue(b2.hashCode()==b1.hashCode());
    }

    @Test
    public void compareTo() throws Exception {
        assertTrue("Cola is not sorted before Leffe", (b3.compareTo(b2) < 0));
        assertTrue("Leffe is not sorted after Cola", (b2.compareTo(b3) > 0));
        assertTrue("Leffe equals Leffe", (b1.compareTo(b2) == 0));
        assertTrue("Leffe equals Leffe", (b2.compareTo(b1) == 0));

    }

    @Test
    public void getItemName() throws Exception {
        assertEquals("name is not Cola", "Cola", b3.getItemName());
        assertEquals("name is not Leffe", "Leffe", b2.getItemName());
        assertEquals("name is not Leffe", "Leffe", b1.getItemName());

    }

    @Test
    public void getItemID() throws Exception {
        assertEquals("ID is not 1", 1, b1.getItemID());
        assertEquals("ID is not 2", 2, b2.getItemID());
        assertEquals("ID is not 3", 3, b3.getItemID());

    }

    @Test
    public void getPrice() throws Exception {
        assertEquals("Price is not right", 2.50, b1.getPrice(), 0);
        assertEquals("Price is not right", 3.50, b2.getPrice(), 0);
        assertEquals("Price is not right", 1.50, b3.getPrice(), 0);

    }


    @Test
    public void testToString() throws Exception {
        assertEquals("to string is nor right", "Leffe 2.5", b1.toString());
        assertEquals("to string is nor right", "Leffe 3.5", b2.toString());
        assertEquals("to string is nor right", "Cola 1.5", b3.toString());

    }

}
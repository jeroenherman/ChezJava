package be.leerstad.chezjava.model;

import javafx.collections.transformation.SortedList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

public class WaiterTest {
    Waiter jeroen;
    Waiter jeroen2;
    Waiter an;
    Waiter an2;
    @Before
    public void setup() {
        jeroen = new Waiter("Jeroen", "Herman", "password");
        jeroen2 = new Waiter("jeroen", "herman", "password");
        an = new Waiter("An", "Loquet", "password");
        an2 = new Waiter("an", "loquet", "password");
    }

    @Test
    public void getFirstName() throws Exception {
        assertEquals("firstname not the same",jeroen.getFirstName(),"Jeroen");
        assertEquals("firstname not the same",jeroen2.getFirstName(),"Jeroen");
        assertEquals("firstname not the same",an.getFirstName(),"An");
        assertEquals("firstname not the same",an2.getFirstName(),"An");

    }

    @Test
    public void getLastName() throws Exception {
        assertEquals("name not the same",jeroen.getLastName(),"Herman");
        assertEquals("name not the same",jeroen2.getLastName(),"Herman");
        assertEquals("name not the same",an.getLastName(),"Loquet");
        assertEquals("name not the same",an2.getLastName(),"Loquet");
    }

    @Test
    public void getPassword() throws Exception {
        assertEquals("password is not password",jeroen.getPassword(),"password");
        assertEquals("password is not password",jeroen2.getPassword(),"password");
        assertEquals("password is not password",an.getPassword(),"password");
        assertEquals("password is not password",an2.getPassword(),"password");
    }

    @Test
    public void testToString() throws Exception {
        assertTrue("to string not correct", jeroen.toString().contains("Jeroen Herman"));
        assertTrue("to string not correct", an.toString().contains("An Loquet"));
        assertTrue("to string not correct", jeroen2.toString().contains("Jeroen Herman"));
        assertTrue("to string not correct", an2.toString().contains("An Loquet"));

    }

    @Test
    public void compareTo() throws Exception {
        assertEquals("jeroen is not the same", jeroen,jeroen2);
        assertEquals("an is not the same", an,an2);
        TreeSet<Waiter> waiters = new TreeSet<>();
        waiters.add(jeroen);
        waiters.add(jeroen2);
        waiters.add(an);
        waiters.add(an2);
        assertTrue("an is not sorted before jeroen", waiters.first().equals(an));

    }

}
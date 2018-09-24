package be.leerstad.chezjava.database;


import be.leerstad.chezjava.model.Beverage;
import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.User;
import be.leerstad.chezjava.model.Waiter;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class DAOOrderMySqlTest {

    private final String workingDir = System.getProperty("user.dir") + "/src/main/resources/";
    private final String propertiesName = "db.properties";
    private Properties goodProperties =new Properties();
    private Properties badProperties =new Properties();
    private Logger logger = Logger.getLogger(DAOOrderMySqlTest.class.getName());

    @Before
    public void setUp() throws Exception {
        loadProperties();

    }

    @Test
    public void getInstance() throws Exception {
      DAOOrderMySql  daoOrderMySql = DAOOrderMySql.getInstance();
        assertTrue("only one instance possible", daoOrderMySql == DAOOrderMySql.getInstance());
    }

    @Test
    public void getAllSales() throws Exception {
        List<Order> orders = DAOOrderMySql.getInstance().getAllSales();
        assertFalse("orders is  empty", orders.isEmpty());
    }

    @Test
    public void getAllSalesByDate() throws Exception {
        List<Order> orders = DAOOrderMySql.getInstance().getAllSales(LocalDate.of(2009, Month.DECEMBER, 25), LocalDate.of(2009, Month.DECEMBER, 25));
        assertFalse("orders is  empty", orders.isEmpty());
        assertEquals("list is not 3 records long", 3, orders.size(), 10);
        orders = DAOOrderMySql.getInstance().getAllSales(LocalDate.of(2050, Month.DECEMBER, 25), LocalDate.of(2050, Month.DECEMBER, 25));
        assertTrue("orders is  empty", orders.isEmpty());
        assertEquals("list is not 0 records long", 0, orders.size(), 10);

    }

    @Test
    public void getAllSalesByWaiter() throws Exception {
        User waiter = new Waiter("Wout", "Peters", "password");
        List<Order> orders = DAOOrderMySql.getInstance().getAllSales(waiter);
        assertFalse("Orders list from waiter with orderID =1 is empty", orders.isEmpty());
        waiter = new Waiter("an", "loquet", "password");
         orders = DAOOrderMySql.getInstance().getAllSales(waiter);
        assertTrue("Orders list from non existing waiter is empty", orders.isEmpty());

    }

    @Test
    public void getAllSalesByWaiterAndDate() throws Exception {
        User waiter = new Waiter("Wout", "Peters", "password");
        List<Order> orders = DAOOrderMySql.getInstance().getAllSales(waiter, LocalDate.of(2009, Month.DECEMBER, 25), LocalDate.of(2009, Month.DECEMBER, 25));
        assertFalse("Orders list from waiter with orderID =1 is empty", orders.isEmpty());
        assertEquals("list is not 1 records long", 1, orders.size(), 0);
        waiter = new Waiter("an", "loquet", "password");
        orders = DAOOrderMySql.getInstance().getAllSales(waiter, LocalDate.of(2009, Month.DECEMBER, 25), LocalDate.of(2009, Month.DECEMBER, 25));
        assertTrue("Orders list from non existing waiter is empty", orders.isEmpty());
        assertEquals("list is not 0 records long", 0, orders.size(), 0);
    }

    @Test
    public void addOrder() throws Exception {
        Waiter waiter = new Waiter("Wout", "Peters", "password");
        Order order = new Order(new Beverage(1, "Cola", 1.50), 5);
        assertTrue("Order not updated in database ", DAOOrderMySql.getInstance().addOrder(order, waiter));
        waiter = new Waiter("an", "loquet", "password");
        assertFalse("Order not updated in database ", DAOOrderMySql.getInstance().addOrder(order, waiter));

    }

    @Test
    public void deleteOrdersFromUser() throws Exception {
        Waiter waiter = new Waiter("Wout", "Peters", "password");
        DAOOrderMySql.getInstance().deleteOrder(waiter, LocalDate.now());
        // insert 1 order in database
        addOrder();
        assertEquals("Numbers of rows deleted is not 1", 1, DAOOrderMySql.getInstance().deleteOrder(waiter, LocalDate.now()), 0);
    }

    @Test
    public void addOrders() throws Exception {
        Waiter waiter = new Waiter("Wout", "Peters", "password");
        Order order = new Order(new Beverage(1, "Cola", 1.50), 4);
        Order order1 = new Order(new Beverage(2, "Leffe", 2.20), 5);
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        orders.add(order1);
        assertTrue("Orders are not updated in database ", DAOOrderMySql.getInstance().addOrder(orders, waiter));

    }
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Test
    public void ExceptionsTest() throws Exception {



    }
    private void changeProperties (String key, String value) {
        FileOutputStream output;

        try {
            output = new FileOutputStream(workingDir + propertiesName);
            // set the properties value
            badProperties.setProperty(key, value);
            badProperties.store(output, key +" Changed");
            output.close();
        } catch (IOException ioe) {
            logger.error("An IO exception occurred: " + ioe.getMessage());
        }
    }
    private void loadProperties ()
    {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertiesName)) {
            goodProperties.load(inputStream);
            badProperties.load(inputStream);

        } catch (IOException e) {
            logger.error("Load properties Failed",e);
        }
    }
    @After
    public void resetProperties()
    {
        FileOutputStream output;

        try {
            output = new FileOutputStream(workingDir + propertiesName);
            // set the properties value
            goodProperties.store(output, "Properties restored after test");
            output.close();
        } catch (IOException ioe) {
            logger.error("An IO exception occurred: " + ioe.getMessage());
        }
    }

}
package be.leerstad.chezjava.model;


import be.leerstad.chezjava.database.DAOBeverageMySql;
import be.leerstad.chezjava.database.DAOWaiterMySql;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.*;

public class CafeTest {

    Table table1;
    Table table2;
    Table table3;
    Table table4;
    Waiter w1;
    Waiter w2;
    Beverage b1;
    Beverage b2;
    Beverage b3;
    Cafe cafe;
    Order o1;
    Order o2;
    File tablesSerFile;
    private final String  outpath = System.getProperty("user.dir") + "//src//main//resources/";
    @Before
    public void Setup() {
        tablesSerFile = new File(outpath+"tables.ser");
        tablesSerFile.delete();
        table1 = new Table("London", 1);
        table2 = new Table("Paris", 2);
        w1 = new Waiter("Wout", "Peters", "password");
        w2 = new Waiter("Nathalie", "Segers", "password");
        b1 = new Beverage(1, "Cola", 2.00);
        b2 = new Beverage(2, "Koffie", 2.50);
        b3 = new Beverage(3, "Duvel", 3.50);
        o1 = new Order(b1, 1);
        o2 = new Order(b2, 2);
        cafe = new Cafe("Chez Java");
    }

    @After
    public void tearDown() throws Exception {

        table1 = table2 = table3 = table4 = null;
        cafe = null;
        w1 = w2 = null;
    }
    @Test
    public void logOn() throws Exception {

        assertFalse("logon succeeded with empty password", cafe.logOn("wout", "peters", ""));
        assertFalse("boolean logged on is not false after wrong login", cafe.isLoggedIn());
        assertFalse("logon succeeded with wrong password", cafe.logOn("wout", "peters", "pass"));
        assertFalse("boolean logged on is not false after wrong login", cafe.isLoggedIn());
        assertTrue("logon failed", cafe.logOn("wout", "peters", "password"));
        assertTrue("boolean logged on is not true after sucessfull login", cafe.isLoggedIn());
        assertEquals("firstName current user is not Wout", "Wout", cafe.getUser().getFirstName());
        assertEquals("lastName current user is not Peters", "Peters", cafe.getUser().getLastName());
        assertEquals("password current user is not password", "password", cafe.getUser().getPassword());
        assertFalse("logon succeeded without logoff first", cafe.logOn("wout", "peters", "password"));


    }

    @Test
    public void logOff() throws Exception {
        cafe.logOff();
        assertTrue("user is still logged on", cafe.getUser() == null);
        assertFalse("boolean logged on is not false after logOff", cafe.isLoggedIn());
    }

    @Test
    public void getName() throws Exception {
        assertEquals("cafe name is not chez Java", cafe.getName(), "Chez Java");
    }

    @Test
    public void getUser() throws Exception {
        cafe.logOn("wout", "peters", "password");
        assertEquals("logged on user is not Wout", "Wout", cafe.getUser().getFirstName());
    }

    @Test
    public void getAllBeverages() throws Exception {
        cafe.loadAllBeverages();
        assertTrue("Beverage list from database is not equal on list cafe", DAOBeverageMySql.getInstance().getAllBeverages().equals(cafe.getBeverages()));
    }

    @Test
    public void getAllWaiters() throws Exception {
        cafe.loadAllWaiters();
        assertTrue("Waiter List equals not database", DAOWaiterMySql.getInstance().getAllWaiters().equals(cafe.getWaiters()));
    }

    @Test
    public void setTables() throws Exception {

        cafe.setupTables();
        assertEquals("list tables is not 6 depends on properties file cafe.properties", 6, cafe.getTables().size());

    }

    @Test
    public void testOrder() {
        // fill cafe with tables
        cafe.setupTables();
        try {
            logOn();
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("logged on user is not w1", cafe.getUser(), w1);
        cafe.setCurrentTable(table1);
        assertEquals("Current table is not table1", cafe.getCurrentTable(), table1);
        cafe.setCurrentTable(table1.getTableNr());
        assertEquals("Current table is not table1", cafe.getCurrentTable().getName(), table1.getName());
        assertEquals("Current table is not table1", cafe.getCurrentTable().getTableNr(), table1.getTableNr());
        assertEquals("Current table is not table1", cafe.getCurrentTable().getTotalOrders(), table1.getTotalOrders(),0);


        cafe.Order(o1);
        assertTrue("first order table1 does not contain o1 -- index 0", cafe.getCurrentTable().getOrders().contains(o1) == true);

    }

    @Test
    public void payOrders() {
        // select table
        cafe.setCurrentTable(1);
        // logon Wout
        cafe.logOn("Wout", "Peters", "password");
        assertEquals("current user is not waut", w1, cafe.getUser());
        // place order1
        cafe.Order("Cola", 2);
        // check order
        o1 = new Order(new Beverage(1, "Cola", 1.50), 2);
        assertEquals("current table does not have order", o1, cafe.getCurrentTable().getOrders().get(0));
        // place new Order2
        cafe.Order("leffe", 2);
        // check order
        o2 = new Order(new Beverage(2, "Leffe", 2.20), 2);
        assertEquals("current table does not have order", o2, cafe.getCurrentTable().getOrders().get(1));

        // Select Table2
        cafe.setCurrentTable(2);
        assertEquals("current tableNr is not 2", 2, cafe.getCurrentTable().getTableNr());
        // Place order
        cafe.Order("koffie", 2);
        assertTrue("current table does not have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(3, "Koffie", 2.0), 2)));
        assertTrue("current table does not have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(3, "Koffie", 2.0), 2)));
        // Switch user
        cafe.logOff();
        assertTrue("current user is not null", cafe.getUser() == null);
        cafe.logOn("nathalie", "segers", "password");
        // Check logon
        assertEquals("current user is not nathalie", w2, cafe.getUser());
        // place order on occupied table
        cafe.Order("cola", 2);
        assertFalse("current table does  have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(1, "Cola", 1.5), 2)));
        //
        cafe.setCurrentTable(3);
        cafe.Order("cola", 2);
        cafe.Order("whisky", 1);
        cafe.Order("duvel", 1);
        assertTrue("current table does not have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(1, "Cola", 1.5), 2)));
        assertTrue("current table does not have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(5, "Whisky", 4.5), 1)));
        assertTrue("current table does not have order", cafe.getCurrentTable().getOrders().contains(new Order(new Beverage(10, "Duvel", 2.5), 1)));
        // Check total Due
        assertEquals("Total Due is not 10 euro", 10, cafe.getCurrentTable().getTotalOrders(), 0);
        // Pay current table
        assertTrue("Orders are not updated to database", cafe.getCurrentTable().payOrders());
        assertTrue("table orders are not empty", cafe.getCurrentTable().getOrders().isEmpty());
        assertTrue("waiter current table is not null", cafe.getCurrentTable().getWaiter() == null);
        // Switch user
        cafe.logOff();
        assertTrue("current user is not null", cafe.getUser() == null);
        // logon Wout
        cafe.logOn("Wout", "Peters", "password");
        cafe.setCurrentTable(1);
        // Pay current table
        assertTrue("Orders are not updated to database", cafe.getCurrentTable().payOrders());
        assertTrue("table orders are not empty", cafe.getCurrentTable().getOrders().isEmpty());
        assertTrue("waiter current table is not null", cafe.getCurrentTable().getWaiter() == null);

    }

    @Test
    public void testSerializable() {
        assertFalse("tables ser file does exist", tablesSerFile.exists());
        testOrder();
        cafe.logOff(); // logging off saves all tables to tables.ser
        assertTrue("tables ser file does not exist", tablesSerFile.exists());
        cafe.getTables().clear();
        cafe.setupTables();
        cafe.logOn("wout","peters","password");
        assertEquals("Table Londen does not contain 1 Cola", cafe.getCurrentTable().getOrder("Cola").getQuantity(),1,0);
    }

    @Test
    public void getTotalRevenueUser() throws Exception {
        logOn();
        assertFalse("Total Revenue is null ",  cafe.getTotalRevenue(cafe.getUser()).equals(null));
    }

    @Test
    public void getTotalRevenues() throws Exception {
        HashMap<User, Double> totalRevenues = new HashMap<>(cafe.getTotalRevenues());
        assertFalse("Total Revenues are null ",  totalRevenues.equals(null));
    }


    @Test
    public void getTotalRevenuesSorted() throws Exception {
        LinkedHashMap<User, Double> topWaiters = new LinkedHashMap<>(cafe.getTotalRevenuesSorted());
        assertFalse("Top Waiters is null ",  topWaiters.equals(null));
        Double result1;
        Double result2=0.00;
        Iterator results =topWaiters.values().iterator();
        while (results.hasNext()) {
            result1 = (Double) results.next();
            if (results.hasNext())
            result2 = (Double) results.next();
            else result2 = 0.00;
        if (!(result2.equals(result1)))
            assertTrue("revenue is not sorted", result1>result2);
        }
    }

    @Test
    public void getTopWaiters() throws Exception {
        LinkedHashMap<User, Double> topWaiters = new LinkedHashMap<>(cafe.getTopWaiters());
        // System.out.println(topWaiters);
    }
    @Test
    public void getAllSalesByPeriod() {
        Double total = cafe.getAllSales(LocalDate.of(2009,01,01),LocalDate.of(2009,12,31)).stream().mapToDouble(Order::getOrderPrice).sum();
        assertEquals("Total Period sales 2009 is not 262.20€",262.20, total,1 );

    } @Test
    public void getAllSalesByUserAndPeriod() {
        Double total = cafe.getAllSales(w1,LocalDate.of(2009,01,01),LocalDate.of(2009,12,31)).stream().mapToDouble(Order::getOrderPrice).sum();
        assertEquals("Wout Total Period sales 2009 is not 122.50€",122.50, total,1 );
    }

    @Test
    public void emailDayReport() throws Exception {
        payOrders();
        assertTrue("Email is not send", cafe.revenueReport(LocalDate.now()));
    }
    @Test
    public void emailPeriodReport() throws Exception {
        assertTrue("Email is not send", cafe.revenueReport(LocalDate.now(),LocalDate.now()));
    }

    @Test
    public void emailWaiterReport() throws Exception {
        assertTrue("Email is not send", cafe.waiterReport());

    }
    @Test
    public void getBeverages() throws Exception {
        List<Beverage> beverageList=  cafe.getBeverages();
        assertEquals("List is not 13 long",13,beverageList.size(),0 );
    }

    @Test
    public void setupTables() throws Exception {
        cafe.setupTables();
        assertEquals("Tables size is not 6",6, cafe.getTables().size());
    }


    @Test
    public void TestToString() throws Exception {
     cafe.setCurrentTable(1);
     cafe.logOn("wout", "peters","password");
     cafe.getCurrentTable().addOrder(o1);
     assertTrue(cafe.toString().contains("Wout Peters"));
     assertTrue(cafe.toString().contains("London"));

    }

    @Test
    public void resetTables() throws Exception {
        cafe.resetTables();
        assertEquals("Tables size is not 6",6, cafe.getTables().size());
        for (Table t: cafe.getTables()) {
            assertTrue("Table is not empty",t.getOrders().isEmpty());
        }
    }

}


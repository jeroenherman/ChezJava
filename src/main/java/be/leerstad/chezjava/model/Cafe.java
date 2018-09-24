package be.leerstad.chezjava.model;

import be.leerstad.chezjava.database.DAOBeverageMySql;
import be.leerstad.chezjava.database.DAOOrderMySql;
import be.leerstad.chezjava.database.DAOWaiterMySql;
import be.leerstad.chezjava.reports.Email;
import be.leerstad.chezjava.reports.EmailThread;
import be.leerstad.chezjava.reports.RevenueReport;
import be.leerstad.chezjava.reports.TopWaitersReport;
import org.apache.log4j.Logger;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Cafe implements Serializable, Report {
    private static final long serialVersionUID = 6963703585573105619L;
    private static final String propertiesName = "cafe.properties";
    private static Logger logger = Logger.getLogger(Cafe.class.getName());
    private static Properties props = new Properties();
    private final String outpath = System.getProperty("user.dir") + "//src//main//resources/";
    private String name;
    private Waiter user;
    private Boolean loggedIn = false;
    private Table currentTable;
    private List<Beverage> beverages;
    private List<Waiter> waiters = new ArrayList<>();
    private List<Table> tables = new ArrayList<>();
    private String[] tableNames;

    public Cafe() {
        loadProperties();
        setupTables();
        beverages = loadAllBeverages();
        waiters = loadAllWaiters();
    }

    public Cafe(String name) {
        this();
        this.name = name;
    }

    public List<Beverage> getBeverages() {
        return beverages;
    }

    // Setup
    private void loadProperties() {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertiesName)) {

            props.load(inputStream);
            this.name = props.getProperty("cafe.name");
            tableNames = props.getProperty("cafe.tables").split(",");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupTables() {
        File serialFile = new File(outpath + "tables.ser");
        if (serialFile.exists() && !serialFile.isDirectory()) {
            restoreTables();
        } else {
            tables.clear();
            for (int i = 0; i < tableNames.length; i++) {
                tables.add(new Table(tableNames[i], i));
            }
        }
    }

    public void resetTables() {
        File serialFile = new File(outpath + "tables.ser");
        serialFile.delete();
        setupTables();
    }

    //
    public void Order(Order order) {
        if ((currentTable != null) && (user != null)) {
            if (currentTable.getWaiter() == null) {
                currentTable.setWaiter(user);
            }
            if (currentTable.getWaiter().equals(user))
                currentTable.addOrder(order);
            else
                logger.error("Add order failed: Table is occupied");
        }
    }

    public void Order(String name, int qty) {
        Order order = new Order(getBeverageByName(name), qty);
        Order(order);
    }

    //
    public boolean logOn(String firstName, String lastName, String password) {
        boolean result = false;
        Waiter waiter = new Waiter(cleanName(firstName), cleanName(lastName), password);
        if (getWaiters().contains(waiter) && this.loggedIn.equals(false)) {
            user = waiter;
            loggedIn = true;
            result = true;
            logger.debug(waiter + "logged on");
        } else
            logger.debug(waiter + "logon Failed");
        return result;
    }

    private void restoreTables() {
        try (
                FileInputStream fis = new FileInputStream(outpath + "tables.ser");
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            this.tables.clear();
            List<Table> restoredTables = (List<Table>) ois.readObject();
            this.tables.addAll(restoredTables);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logOff() {
        logger.debug(getUser() + " logged off");
        user = null;
        loggedIn = false;
        this.saveTables();
    }

    //Serialise cafe to file

    private void saveTables() {
        try (
                FileOutputStream fs = new FileOutputStream(
                        outpath + "tables.ser");

                ObjectOutputStream os = new ObjectOutputStream(fs)) {
            logger.debug("Tables saved");
            os.writeObject(tables);
        } catch (IOException e) {
            logger.error("tables.ser file not found", e);
        }
    }

    public String getName() {
        return name;
    }

    public List<Table> getTables() {
        return tables;
    }

    public User getUser() {
        return user;
    }

    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(int tablenr) {
        this.currentTable = tables.get(tablenr);

    }

    public List<Waiter> getWaiters() {
        return waiters;
    }

    public Boolean isLoggedIn() {
        return loggedIn;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
        logger.debug("Selected Table: " + currentTable.getName());
    }

    public List<Beverage> loadAllBeverages() {
        beverages = DAOBeverageMySql.getInstance().getAllBeverages();
        return beverages;
    }

    public List<Waiter> loadAllWaiters() {
        waiters = DAOWaiterMySql.getInstance().getAllWaiters();
        return waiters;
    }

    @Override
    public boolean revenueReport(LocalDate from) {
        try {
            RevenueReport revenueReport = new RevenueReport(from, from, getAllOrders(from, from));
            Email email = new Email(revenueReport.createPDF(), "Revenue Report");
            EmailThread emailThread = new EmailThread(email);
            emailThread.start();
            logger.debug("revenue Day report Email thread started");
            return true;
        } catch (Exception e) {
            logger.error("Day report failed", e);
            return false;
        }
    }

    public boolean revenueReport(LocalDate from, LocalDate to) {
        try {
            RevenueReport revenueReport = new RevenueReport(from, to, getAllOrders(from, to));
            Email email = new Email(revenueReport.createPDF(), "PeriodReport");
            EmailThread emailThread = new EmailThread(email);
            emailThread.start();
            logger.debug("revenue Period report Email thread started");
            return true;
        } catch (Exception e) {
            logger.error("Revenue report failed", e);
            return false;
        }

    }

    @Override
    public boolean waiterReport() {
        try {
            TopWaitersReport topWaitersReport = new TopWaitersReport(LocalDate.now(), getTopWaiters());
            Email email = new Email(topWaitersReport.createPDF(), "Top Waiter Report");
            EmailThread emailThread = new EmailThread(email);
            emailThread.start();
            logger.debug("Waiter report Email thread started");

            return true;
        } catch (Exception e) {
            logger.error("waiterreport failed", e);
            return false;
        }
    }

    protected Map<User, List<Order>> getAllOrders(LocalDate from, LocalDate until) {
        HashMap<User, List<Order>> allOrders = new HashMap<>();
        for (User u : this.getWaiters()) {
            List<Order> orders = DAOOrderMySql.getInstance().getAllSales(u, from, until);
            allOrders.put(u, orders);
        }
        return allOrders;
    }

    public List<Order> getAllSales(User user, LocalDate from, LocalDate to) {
        return DAOOrderMySql.getInstance().getAllSales(user, from, to);
    }

    public List<Order> getAllSales(LocalDate from, LocalDate to) {
        return DAOOrderMySql.getInstance().getAllSales(from, to);
    }

    public Double getTotalRevenue(User user) {
        Double result;
        result = DAOOrderMySql.getInstance().getAllSales(user).stream().mapToDouble(Order::getOrderPrice).sum();
        return result;
    }

    public Map<User, Double> getTotalRevenues() {
        Map<User, Double> totalRevenues = new HashMap<>();
        for (User u : this.getWaiters()) {
            Double totalRevenue = getTotalRevenue(u);
            totalRevenues.put(u, totalRevenue);
        }
        return totalRevenues;
    }

    public Map<User, Double> getTopWaiters() {
        LinkedHashMap<User, Double> result = getTotalRevenues().entrySet().stream()
                .sorted(Entry.comparingByValue(Comparator.reverseOrder())).limit(3)
                .collect(Collectors.toMap(
                        Entry::getKey,
                        Entry::getValue,
                        (x, y) -> {
                            throw new AssertionError();
                        },
                        LinkedHashMap::new
                ));
        return result;
    }

    public Map<User, Double> getTotalRevenuesSorted() {

        List<Entry<User, Double>> list = new LinkedList<>(getTotalRevenues().entrySet());
        Collections.sort(list, new RevenueComparator());

        Map<User, Double> sortedMap = new LinkedHashMap<>();
        for (Entry<User, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Welcome to ").append(this.getName()).append(System.getProperty("line.separator"));
        if (user != null)
            sb.append("Current logged on Waiter: ").append(getUser()).append(System.getProperty("line.separator"));
        if (currentTable != null)
            sb.append("Current Table: ").append(getCurrentTable()).append(System.getProperty("line.separator"));

        return sb.toString();
    }

    private String cleanName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        return name;
    }

    public Beverage getBeverageByName(String name) {
        Optional<Beverage> matchingObject = beverages.stream().filter(b -> b.getItemName().equals(cleanName(name))).findFirst();
        Beverage beverage = matchingObject.orElse(null);
        return beverage;
    }

    class RevenueComparator implements Comparator<Entry<User, Double>> {
        public int compare(Entry<User, Double> o1, Entry<User, Double> o2) {
            return o2.getValue().compareTo(o1.getValue());
        }
    }
}


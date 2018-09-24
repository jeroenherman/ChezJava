package be.leerstad.chezjava.database;


import be.leerstad.chezjava.model.Beverage;
import be.leerstad.chezjava.model.Order;
import be.leerstad.chezjava.model.User;
import org.apache.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class DAOOrderMySql extends BaseDAO implements DAOOrder {
    private static Logger logger = Logger.getLogger(DAOOrderMySql.class.getName());
    private static DAOOrderMySql instance = new DAOOrderMySql();

    private DAOOrderMySql() {
    }

    public static DAOOrderMySql getInstance() {
        if (instance == null)
            instance = new DAOOrderMySql();
        return instance;
    }

    @Override
    public List<Order> getAllSales() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT beverages.beverageID, beverages.beverageName, beverages.price, sum(orders.qty) as qty, orders.date " +
                "from beverages, orders " +
                "where beverages.beverageID = orders.beverageID \n" +
                "group by beverages.beverageID, beverages.beverageName, beverages.price";
        try (Statement statement = getConnection().createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Beverage beverage = new Beverage(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                Order order = new Order(beverage, rs.getInt(4), rs.getDate(5).toLocalDate());
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("failed getting all Sales from Database", e);
        }

        return orders;
    }

    /**
     * Returns List of orders  from date to date , grouped by beverage
     *
     * @param dateFrom
     * @param dateTo
     * @return
     */

    public List<Order> getAllSales(LocalDate dateFrom, LocalDate dateTo) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT beverages.beverageID, beverages.beverageName, beverages.price, sum(orders.qty) as qty, orders.date \n" +
                "from beverages, orders\n" +
                "where beverages.beverageID = orders.beverageID \n" +
                "and date between ? and ?\n " +
                "group by beverages.beverageID, beverages.beverageName, beverages.price";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setDate(1, Date.valueOf(dateFrom));
            preparedStatement.setDate(2, Date.valueOf(dateTo));
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Beverage beverage = new Beverage(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                Order order = new Order(beverage, rs.getInt(4), rs.getDate(5).toLocalDate());
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("failed getting all Sales By from / To Date from Database", e);
        }

        return orders;
    }

    /**
     * Returns list of orders for a specified user, from date to date , grouped by beverage
     *
     * @param user
     * @param dateFrom
     * @param dateTo
     * @return
     */
    public List<Order> getAllSales(User user, LocalDate dateFrom, LocalDate dateTo) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT beverages.beverageID, beverages.beverageName, beverages.price, sum(orders.qty) as qty, orders.date \n" +
                "from beverages, orders, waiters \n" +
                "where beverages.beverageID = orders.beverageID \n" +
                "AND orders.waiterID = waiters.waiterID\n" +
                "and date between ? and ? \n" +
                "AND orders.waiterID = ?\n" +
                "group by beverages.beverageID, beverages.beverageName, beverages.price";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setDate(1, Date.valueOf(dateFrom));
            preparedStatement.setDate(2, Date.valueOf(dateTo));
            preparedStatement.setInt(3, user.getUserID());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Beverage beverage = new Beverage(rs.getInt(1), rs.getString(2), rs.getDouble(3));
                Order order = new Order(beverage, rs.getInt(4), rs.getDate(5).toLocalDate());
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("failed getting all Sales by User, From date, To Date from Database", e);
        }

        return orders;
    }

    public List<Order> getAllSales(User user) {
        List<Order> orders = new ArrayList<>();
        String sqlSelect = "SELECT beverages.beverageID, beverages.beverageName, beverages.price, orders.qty, orders.date \n" +
                "from beverages, orders, waiters  \n" +
                "WHERE beverages.beverageID = orders.beverageID \n" +
                "AND orders.waiterID = waiters.waiterID\n" +
                "AND orders.waiterID = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sqlSelect)) {
            preparedStatement.setInt(1, user.getUserID());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Beverage beverage = new Beverage(rs.getInt("beverageID"), rs.getString("beverageName"), rs.getDouble("price"));
                Order order = new Order(beverage, rs.getInt("qty"), rs.getDate("date").toLocalDate());
                orders.add(order);
            }

        } catch (SQLException e) {
            logger.error("failed getting all Sales by User from Database", e);
        }
        return orders;
    }

    public boolean addOrder(Order order, User waiter) {
        int inserted = 0;
        String sql = "insert into orders ( orders.beverageID, orders.qty, orders.date, orders.waiterID)" + "values (?, ?, ?,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getItemId());
            statement.setInt(2, order.getQuantity());
            statement.setString(3, order.getDate().format(DateTimeFormatter.BASIC_ISO_DATE));
            statement.setInt(4, waiter.getUserID());
            inserted = statement.executeUpdate();

        } catch (SQLException e) {
            logger.error("failed adding Order to Database", e);
        }
        return inserted == 1;
    }

    public boolean addOrder(List<Order> orders, User waiter) {

        String sql = "insert into orders ( orders.beverageID, orders.qty, orders.date, orders.waiterID)" + "values (?, ?, ?,?)";
        Connection connection = null;
        PreparedStatement statement = null;
        boolean result = false;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
            for (Order order : orders) {
                statement.setInt(1, order.getItemId());
                statement.setInt(2, order.getQuantity());
                statement.setString(3, order.getDate().format(DateTimeFormatter.BASIC_ISO_DATE));
                statement.setInt(4, waiter.getUserID());
                statement.addBatch();
            }

            int[] affectedRows = statement.executeBatch();
            // check affected rows
            int sumAffectedRows = IntStream.of(affectedRows).sum();
            if (sumAffectedRows == orders.size()) {
                result = true;
                connection.commit();
                logger.debug("Successfull Order Batchupdate ");
            }

        } catch (BatchUpdateException be) {
            logger.error("Order Batchupdate error", be);
            // commit the entire executed statements, or rollback the entire batch
            try {
                connection.rollback();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        } catch (SQLException e) {
            logger.error("Order SQL error", e);
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                logger.error(e);
            }

        }
        return result;
    }

    public int deleteOrder(User waiter, LocalDate date) {
        int result = 0;
        String sql = "delete FROM orders " +
                "where orders.date = ? " +
                "and waiterID = ? ";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, date.format(DateTimeFormatter.BASIC_ISO_DATE));
            preparedStatement.setInt(2, waiter.getUserID());
            result = preparedStatement.executeUpdate();
            logger.debug("Order Deleted successfully");
        } catch (SQLException e) {
            logger.error("failed deleting Order from Database", e);
        }
        return result;
    }

}

package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Waiter;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOWaiterMySql extends BaseDAO implements DAOWaiter {
    private static Logger logger = Logger.getLogger(DAOWaiterMySql.class.getName());
    private static DAOWaiterMySql instance = new DAOWaiterMySql();

    //
    private DAOWaiterMySql() {
    }

    //
    public static DAOWaiterMySql getInstance() {
        if (instance == null)
            instance = new DAOWaiterMySql();
        return instance;
    }

    @Override
    public List<Waiter> getAllWaiters() {
        List<Waiter> result = new ArrayList<>();
        String sql = "SELECT * from waiters";
        try (Statement statement = getConnection().createStatement();
             ResultSet rs = statement.executeQuery(sql);
        ) {
            while (rs.next()) {
                Waiter waiter = new Waiter(rs.getString("firstName"), rs.getString("lastName"), rs.getString("password"));
                result.add(waiter);
            }

        } catch (SQLException e) {
            logger.error("failed getting all Waiters from Database", e);
        }

        return result;
    }

    @Override
    public int getWaiterId(String firstName, String lastName) {
        int userId = 0;
        String sql = "select waiterId from waiters where firstName = ? and lastName = ?";
        try (PreparedStatement preparedStatement = getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                userId = rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("failed getting WaiterID from Database by firstname, lastname", e);
        }
        return userId;
    }

}

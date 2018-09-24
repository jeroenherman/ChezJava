package be.leerstad.chezjava.database;

import be.leerstad.chezjava.model.Beverage;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DAOBeverageMySql extends BaseDAO implements DAOBeverage {
    private static Logger logger = Logger.getLogger(DAOBeverageMySql.class.getName());

    private static DAOBeverageMySql instance = new DAOBeverageMySql();

    private DAOBeverageMySql() {

    }

    public static DAOBeverageMySql getInstance() {
        if (instance == null)
            instance = new DAOBeverageMySql();
        return instance;
    }

    @Override
    public List<Beverage> getAllBeverages() {
        List<Beverage> beverages = new ArrayList<>();
        String sql = "SELECT * FROM beverages";
        try (Statement statement = getConnection().createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                Beverage beverage = new Beverage(rs.getInt("beverageID"), rs.getString("beverageName"), rs.getDouble("price"));
                beverages.add(beverage);
            }

        } catch (SQLException e) {
            logger.error("failed getting beverages from Database", e);
        }

        return beverages;
    }

    @Override
    public Beverage getBeverage(int beverageID) {
        Beverage beverage = new Beverage(0, "NotFound", 0);
        String sql = "select beverageName, price from beverages where beverageID = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setInt(1, beverageID);
            ResultSet rs = statement.executeQuery();
            rs.next();
            beverage = new Beverage(beverageID, rs.getString(1), rs.getDouble(2));

        } catch (SQLException e) {
            logger.error("failed getting beverage on BeverageID from Database:" + e.getMessage() + "ErrorCode: " + e.getErrorCode());
        }
        return beverage;
    }

    @Override
    public Beverage getBeverage(String name) {
        Beverage beverage = new Beverage(0, "NotFound", 0);
        String sql = "select beverageID,  price from beverages where beverageName = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            rs.next();
            beverage = new Beverage(rs.getInt(1), name, rs.getDouble(2));

        } catch (SQLException e) {
            logger.error("failed getting beverage on Beverage Name from Database", e);
        }
        return beverage;
    }

}

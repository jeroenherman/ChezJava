/*
 * Copyright (c) 2017. All code is used during Programmeren 3 module at CVO Leerstad
 */

package be.leerstad.chezjava.database;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

abstract class BaseDAO implements DAO {
    private static final String propertiesName = "db.properties";
    private static Logger logger = Logger.getLogger(BaseDAO.class.getName());
    private static String dbUrl;
    private static Properties props = new Properties();
    private static String userName;
    private static String password;

    public BaseDAO() {

        loadProperties();
    }

    private static void loadProperties() {

        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(propertiesName)) {

            props.load(inputStream);
            userName = props.getProperty("db.username");
            password = props.getProperty("db.password");
            dbUrl = props.getProperty("db.conn.url");

        } catch (IOException e) {
            logger.error("Load properties Failed", e);
        }
    }

    public Connection getConnection() {
        Connection c = null;
        try {
            c = DriverManager.getConnection(dbUrl, userName, password);
        } catch (SQLException e) {
            logger.error("No connection to Database, Check Url, username, password", e);

        }

        return c;
    }
}

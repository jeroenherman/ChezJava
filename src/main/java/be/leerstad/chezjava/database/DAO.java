/*
 * Copyright (c) 2017. All code is used during Programmeren 3 module at CVO Leerstad
 */

package be.leerstad.chezjava.database;


import java.sql.Connection;
import java.sql.SQLException;

public interface DAO {
    Connection getConnection() throws SQLException;
}
package com.napier.sem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles the connection to the MySQL database.
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://db:3306/world?useSSL=false";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "example";

    private Connection con;

    /**
     * Connects to the MySQL database and creates a new connection.
     * Connection is attempted 10 times with a 5-second delay between each attempt.
     */
    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 1; i <= retries; i++) {
            System.out.println("Connecting to database... Attempt " + i + "/" + retries);
            try {
                con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Successfully connected");
                return;
            } catch (SQLException ex) {
                System.err.println("Failed to connect to database: " + ex.getMessage());
            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                System.err.println("Thread interrupted");
            }
        }
        System.err.println("Connection to database failed after " + retries + " attempts");
    }

    /**
     * Disconnects from the MySQL database and closes the connection.
     */
    public void disconnect() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Disconnected from database");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return con;
    }
}

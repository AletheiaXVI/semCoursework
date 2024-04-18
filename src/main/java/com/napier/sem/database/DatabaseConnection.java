package com.napier.sem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * Class that handles the connection to the MySQL database.
 * @author Oskar Budzisz
 */
public class DatabaseConnection {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    private Connection con;

    /**
     * Constructor for the DatabaseConnection class. It is private to prevent instantiation of the class
     *
     * @param dbUrl      URL of the MySQL database
     * @param dbUser     Username to connect to the MySQL database
     * @param dbPassword Password to connect to the MySQL database
     */
    private DatabaseConnection(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    /**
     * Factory method to create a new DatabaseConnection object.
     *
     * @param dbUrl      URL of the MySQL database
     * @param dbUser     Username to connect to the MySQL database
     * @param dbPassword Password to connect to the MySQL database
     * @return A new DatabaseConnection object
     */
    public static DatabaseConnection from(String dbUrl, String dbUser, String dbPassword) {
        assert dbUrl != null : "Database URL cannot be null";
        assert dbUser != null : "Database user cannot be null";
        assert dbPassword != null : "Database password cannot be null";

        return new DatabaseConnection(dbUrl, dbUser, dbPassword);
    }


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
                con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                System.out.println("Successfully connected");
                return;
            } catch (SQLException ex) {
                System.err.println("Failed to connect to database: " + ex.getMessage());
            }

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // interrupt the thread if sleep is interrupted
                System.err.println("Thread interrupted");
            }
        }
        System.err.println("Connection to database failed after " + retries + " attempts");
        System.exit(-1);

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

        } catch (SQLException | NullPointerException e) {

            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return con;
    }
}

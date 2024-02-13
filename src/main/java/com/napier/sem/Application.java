package com.napier.sem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Application {

    // Connection to MySQL database.
    private Connection con = null;


    /**
     * Connects to the MySQL database and creates a new connection.
     * Connection is attempted 10 times with a 5-second delay between each attempt.
     */
    public void connect()
    {

        // Make sure the SQL driver is available
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                Thread.sleep(5000); // sleep the main thread for 5 seconds between retries
                con = DriverManager.getConnection("jdbc:mysql://db:3306/world?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException ex)
            {
                System.out.println("Failed to connect to database attempt " + i + "/" + retries);
                System.out.println(ex.getMessage());
            }
            catch (InterruptedException ex)
            {
                System.out.println("Thread interrupted? Should not happen."); // this should never happen
            }
        }
    }

    /**
     * Disconnects from the MySQL database and closes the connection.
     */
    public void disconnect()
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Runs the application.
     */
    public void run()
    {
        this.connect();
        this.disconnect();
    }
}

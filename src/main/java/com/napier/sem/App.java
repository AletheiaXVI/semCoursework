package com.napier.sem;

import java.sql.SQLException;

public class App
{
    /**
     * Main method
     * @param args command arguments
     */

    public static void main(String[] args) throws SQLException {
        // Create our new Application and run it
        final Application app = new Application();
        app.run();
    }
}

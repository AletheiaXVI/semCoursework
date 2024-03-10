package com.napier.sem;


import com.napier.sem.database.DatabaseConnection;

public class Application {

    private final DatabaseConnection dbCon = DatabaseConnection.of(
            "jdbc:mysql://db:3306/world?useSSL=false",
            "root",
            "example"
    );

    /**
     * Runs the application.
     */
    public void run()
    {
        dbCon.connect();



        dbCon.disconnect();
    }
}

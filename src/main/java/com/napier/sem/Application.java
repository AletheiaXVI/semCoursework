package com.napier.sem;


import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.CountryReport;


public class Application {

    public static ObjectMapper objectMapper;

    private final DatabaseConnection dbCon = DatabaseConnection.from(
            "jdbc:mysql://db:3306/world?useSSL=false",
            "root",
            "example"
    );

    public void query1() {
        var results = objectMapper.getObjectsFromDatabase("SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, country.Capital  FROM country ORDER BY country.Population ASC", CountryReport::new);
        String test = results.toString();
        System.out.println(test);
    }

    /**
     * Runs the application.
     */
    public void run()
    {
        dbCon.connect();
        objectMapper = new ObjectMapper(dbCon.getConnection());
        query1();
        dbCon.disconnect();
    }

}

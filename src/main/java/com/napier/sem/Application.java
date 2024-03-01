package com.napier.sem;


import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.City;

import java.util.List;

public class Application {

    private final DatabaseConnection dbCon = new DatabaseConnection();
    private ObjectMapper objectMapper;

    /**
     * Runs the application.
     */
    public void run()
    {
        dbCon.connect();
        objectMapper = new ObjectMapper(dbCon.getConnection());

        // Example usage of the ObjectMapper
        City city = objectMapper.getObjectFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 1);

        System.out.println("Mapping single city:");

        System.out.println(city);

        List<City> cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city", City::new);

        System.out.println("Mapping 10 first cities:");

        cities.stream().limit(10).forEach(System.out::println);

        dbCon.disconnect();
    }
}

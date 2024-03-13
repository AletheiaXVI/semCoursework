package com.napier.sem.test;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.City;
import com.napier.sem.database.model.Country;
import com.napier.sem.database.model.CountryLanguage;
import com.napier.sem.database.model.CountryReport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {

    private static DatabaseConnection dbCon;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        dbCon = DatabaseConnection.from(
                "jdbc:mysql://localhost:33060/world?allowPublicKeyRetrieval=true&useSSL=false",
                "root",
                "example"
        );
        dbCon.connect();
        objectMapper = new ObjectMapper(dbCon.getConnection());
    }

    @AfterAll
    static void tearDown() {
        dbCon.disconnect();
        objectMapper = null;
    }

    /**
     * Tests getting the result of a query to get a country from the database given the code GBR which should be United Kingdom
     */
    @Test
    void testGetCountry() {
        var gbr = objectMapper.getObjectFromDatabase("SELECT * FROM country WHERE Code = ?", Country::new, "GBR");
        System.out.println(gbr);
        assertEquals("United Kingdom", gbr.getName());
    }

    /**
     * Tests getting the result of a query to get a country from the database, should be null given the code is invalid
     */
    @Test
    void testGetCountryNull() {
        var gbr = objectMapper.getObjectFromDatabase("SELECT * FROM country WHERE Code = ?", Country::new, "XXX");
        assertNull(gbr);
    }

    /**
     * Tests getting the result of a query to get a city from the database given the id 1 which should be Kabul
     */
    @Test
    void testGetCity() {
        var city = objectMapper.getObjectFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 1);
        System.out.println(city);
        assertEquals("Kabul", city.getName());
    }

    /**
     * Tests getting the result of a query to get a city from the database, should be null given the id is invalid
     */
    @Test
    void testGetCityNull() {
        var city = objectMapper.getObjectFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 999999);
        assertNull(city);
    }

    /**
     * Tests getting the result of a query to get all the cities from the database, should return 10 given the limit is 10
     */
    @Test
    void testGetCityList() {
        var cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city LIMIT 10", City::new);
        System.out.println(cities);
        assertEquals(10, cities.size());
    }

    /**
     * Tests getting the result of a query to get all the cities from the database, should be empty given the id is invalid
     */
    @Test
    void testGetCityListEmpty() {
        var cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 999999);
        assertEquals(0, cities.size());
    }

    /**
     * Tests getting the result of a query to get all the countries in the world that speak English
     */
    @Test
    void testCountryLanguage() {
        var languages = objectMapper.getObjectsFromDatabase("SELECT * FROM countrylanguage WHERE CountryCode = ?", CountryLanguage::new, "GBR");
        System.out.println(languages);
        assertEquals(3, languages.size());
    }

    /**
     * Tests getting the result to generate a report of the top 10 countries by population
     * @author Shea Tait
     */
    @Test
    void testCountryReport() {
        var results = objectMapper.getObjectsFromDatabase("SELECT country.Code, country.Name, country.Continent, country.Region, country.Population, country.Capital  FROM country ORDER BY country.Population ASC LIMIT 10", CountryReport::new);
        assertEquals(10, results.size());
    }

}

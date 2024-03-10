package com.napier.sem.test;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.City;
import com.napier.sem.database.model.Country;
import com.napier.sem.database.model.CountryLanguage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SQLTests {

    private static DatabaseConnection dbCon;
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        dbCon = DatabaseConnection.of(
                "jdbc:mysql://localhost:33060/world?useSSL=false",
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

    @Test
    void testGetCountry() {
        var gbr = objectMapper.getObjectFromDatabase("SELECT * FROM country WHERE Code = ?", Country::new, "GBR");
        System.out.println(gbr);
        assertEquals("United Kingdom", gbr.getName());
    }

    @Test
    void testGetCountryNull() {
        var gbr = objectMapper.getObjectFromDatabase("SELECT * FROM country WHERE Code = ?", Country::new, "XXX");
        assertNull(gbr);
    }

    @Test
    void testGetCity() {
        var city = objectMapper.getObjectFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 1);
        System.out.println(city);
        assertEquals("Kabul", city.getName());
    }

    @Test
    void testGetCityNull() {
        var city = objectMapper.getObjectFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 999999);
        assertNull(city);
    }

    @Test
    void testGetCityList() {
        var cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city LIMIT 10", City::new);
        System.out.println(cities);
        assertEquals(10, cities.size());
    }

    @Test
    void testGetCityListEmpty() {
        var cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city WHERE ID = ?", City::new, 999999);
        assertEquals(0, cities.size());
    }

    @Test
    void testCountryLanguage() {
        var languages = objectMapper.getObjectsFromDatabase("SELECT * FROM countrylanguage WHERE CountryCode = ?", CountryLanguage::new, "GBR");
        System.out.println(languages);
        assertEquals(3, languages.size());
    }

}

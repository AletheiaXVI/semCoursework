package com.napier.sem;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.List;

public class Application {


    /**
     * Runs the application.
     */
    public void run() throws SQLException {
        DatabaseConnection dbCon = DatabaseConnection.from(
                //"jdbc:mysql://db:3306/world?useSSL=false",
                "jdbc:mysql://localhost:33060/world?sslMode=DISABLED",
                "root",
                "example"
        );
        dbCon.connect();

        // makes a connection to database then executes a statement to allow GROUP BY's to work with multiple concatenated fields
        Connection connection = dbCon.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("SET SESSION sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';");
        statement.close();


        ObjectMapper objectMapper = new ObjectMapper(dbCon.getConnection());

        // 4 lists of distinct continents, regions, countries & districts for use in queries
        List<Distinct> continents = objectMapper.getObjectsFromDatabase("SELECT DISTINCT continent FROM country ORDER BY continent ASC", rs -> new Distinct(rs, "Continent"));
        List<Distinct> regions = objectMapper.getObjectsFromDatabase("SELECT DISTINCT region FROM country ORDER BY region ASC", rs -> new Distinct(rs, "Region"));
        List<Distinct> countries = objectMapper.getObjectsFromDatabase("SELECT DISTINCT name as 'country' FROM country ORDER BY 'country' ASC", rs -> new Distinct(rs, "Country"));
        List<Distinct> districts = objectMapper.getObjectsFromDatabase("SELECT DISTINCT district FROM city ORDER BY district ASC", rs -> new Distinct(rs, "District"));

        Scanner scanner = new Scanner(System.in);
        int N;

        System.out.println("Outputting Reports");

        try {
            N = scanner.nextInt();
            scanner.nextLine();
            // Process input
        } catch (NoSuchElementException e) {
            System.out.println("No input provided, using 1 as default variable");
            N = 1;
        }

        int finalN = N;

        System.out.println("Country Reports");

        // All the countries in the world organised by largest population to smallest.
        List<CountryReport> countryReport = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC", CountryReport::new);
        System.out.println(countryReport);

        //All the countries in a continent organised by largest population to smallest.
        List<CountryReport> countriesByContinent = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY continent ASC, population DESC", CountryReport::new);
        System.out.println(countriesByContinent);

        //All the countries in a region organised by largest population to smallest.
        List<CountryReport> countriesByRegion = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY region ASC, population DESC", CountryReport::new);
        System.out.println(countriesByRegion);

        //The top N populated countries in the world where N is provided by the user.
        List<CountryReport> countryReportsN = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC LIMIT ?", CountryReport::new, finalN);
        System.out.println(countryReportsN);

        //The top N populated countries in a continent where N is provided by the user.
        continents.stream()
                .map(Distinct::getName)
                .forEach(continentName -> {
                    List<CountryReport> countryReportsContinentN = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND continent = '?'  ORDER BY continent ASC, population DESC LIMIT ?", CountryReport::new, continentName, finalN);
                    System.out.println(countryReportsContinentN);
                });


        //The top N populated countries in a region where N is provided by the user.
        regions.stream()
                .map(Distinct::getName)
                .forEach(regionName -> {
                    List<CountryReport> countryReportsRegionN = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND region = '?'  ORDER BY region ASC, population DESC LIMIT ?", CountryReport::new, regionName, finalN);
                    System.out.println(countryReportsRegionN);
                });

        System.out.println("Capital City Reports");

        //All the capital cities in the world organised by largest population to smallest.
        List<CapitalCityReport> capitalCityReport = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC", CapitalCityReport::new);
        System.out.println(capitalCityReport);

        //All the capital cities in a continent organised by largest population to smallest.
        List<CapitalCityReport> capitalCityReportContinents = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC", CapitalCityReport::new);
        System.out.println(capitalCityReportContinents);

        //All the capital cities in a region organised by largest population to smallest.
        List<CapitalCityReport> capitalCityReportRegions = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC", CapitalCityReport::new);
        System.out.println(capitalCityReportRegions);

        //The top N populated capital cities in the world where N is provided by the user.
        List<CapitalCityReport> capitalCityReportsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT ?", CapitalCityReport::new, finalN);
        System.out.println(capitalCityReportsN);

        //The top N populated capital cities in a continent where N is provided by the user.
        continents.stream()
                .map(Distinct::getName)
                .forEach(continentName -> {
                    List<CapitalCityReport> capitalCityReportContinentsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.continent = ? AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT ?", CapitalCityReport::new, continentName, finalN);
                    System.out.println(capitalCityReportContinentsN);
                });

        //The top N populated capital cities in a region where N is provided by the user.
        regions.stream()
                .map(Distinct::getName)
                .forEach(regionName -> {
                    List<CapitalCityReport> capitalCityReportRegionsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = ? AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT ?", CapitalCityReport::new, regionName, finalN);
                    System.out.println(capitalCityReportRegionsN);
                });

        System.out.println("City Reports");

        //All the cities in the world organised by largest population to smallest.
        List<CityReport> cityReport = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC", CityReport::new);
        System.out.println(cityReport);

        //All the cities in a continent organised by largest population to smallest.
        List<CityReport> cityReportContinents = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC", CityReport::new);
        System.out.println(cityReportContinents);

        //All the cities in a region organised by largest population to smallest.
        List<CityReport> cityReportRegions = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC", CityReport::new);
        System.out.println(cityReportRegions);


        //All the cities in a country organised by largest population to smallest.
        List<CityReport> cityReportCountries = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.name ASC, city.population DESC", CityReport::new);
        System.out.println(cityReportCountries);


        //All the cities in a district organised by largest population to smallest.
        List<CityReport> cityReportDistricts = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.district ASC, city.population DESC", CityReport::new);
        System.out.println(cityReportDistricts);

        //The top N populated cities in the world where N is provided by the user.
        List<CityReport> cityReportN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT ?", CityReport::new, finalN);
        System.out.println(cityReportN);

        //The top N populated cities in a continent where N is provided by the user.
        continents.stream()
                .map(Distinct::getName)
                .forEach(continentName -> {
                    List<CityReport> cityReportContinentsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.continent = ? AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT ?", CityReport::new, continentName, finalN);
                    System.out.println(cityReportContinentsN);
                });

        //The top N populated cities in a region where N is provided by the user.
        regions.stream()
                .map(Distinct::getName)
                .forEach(regionName -> {
                    List<CityReport> cityReportRegionsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.region = ? AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT ?", CityReport::new, regionName, finalN);
                    System.out.println(cityReportRegionsN);
                });

        //The top N populated cities in a country where N is provided by the user
        countries.stream()
                .map(Distinct::getName)
                .forEach(countryName -> {
                    List<CityReport> cityReportCountriesN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.name = ? AND country.code = city.countryCode ORDER BY 'country' ASC, city.population DESC LIMIT ?", CityReport::new, countryName, finalN);
                    System.out.println(cityReportCountriesN);
                });

        //The top N populated cities in a district where N is provided by the user.
        districts.stream()
                .map(Distinct::getName)
                .forEach(districtName -> {
                    List<CityReport> cityReportDistrictsN = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE city.district = ? AND country.code = city.countryCode ORDER BY city.district ASC, city.population DESC LIMIT ?", CityReport::new, districtName, finalN);
                    System.out.println(cityReportDistrictsN);
                });

        System.out.println("Population Reports");

        String populationContinentQuery = "SELECT country.continent AS name, " +
                "SUM(country.population) as population, " +
                "(SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.continent = ? AND country.code = city.countrycode)) as cityPopulation, " +
                "SUM(country.population) - (SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.continent = ? AND country.code = city.countrycode)) as ruralPopulation " +
                "FROM country " +
                "WHERE country.continent = ? " +
                "GROUP BY country.continent";

        continents.stream()
                .map(Distinct::getName)
                .forEach(continentName -> {
                    List<PopulationReport> report = objectMapper.getObjectsFromDatabase(populationContinentQuery, PopulationReport::new, continentName, continentName, continentName);
                    System.out.println(report);
                });

        String populationRegionQuery = "SELECT country.region AS name, " +
                "SUM(country.population) as population, " +
                "(SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.region = ? AND country.code = city.countrycode)) as cityPopulation, " +
                "SUM(country.population) - (SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.region = ? AND country.code = city.countrycode)) as ruralPopulation " +
                "FROM country " +
                "WHERE country.region = ? " +
                "GROUP BY country.region";

        regions.stream()
                .map(Distinct::getName)
                .forEach(regionName -> {
                    List<PopulationReport> report = objectMapper.getObjectsFromDatabase(populationRegionQuery, PopulationReport::new, regionName, regionName, regionName);
                    System.out.println(report);
                });

        String populationCountryQuery = "SELECT country.name AS name, " +
                "SUM(country.population) as population, " +
                "(SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.name = ? AND country.code = city.countrycode)) as cityPopulation, " +
                "SUM(country.population) - (SELECT SUM(city.population) FROM city WHERE city.countrycode IN " +
                "(SELECT DISTINCT city.countrycode FROM city, country WHERE country.name = ? AND country.code = city.countrycode)) as ruralPopulation " +
                "FROM country " +
                "WHERE country.name = ? " +
                "GROUP BY country.name";

        countries.stream()
                .map(Distinct::getName)
                .forEach(countryName -> {
                    List<PopulationReport> report = objectMapper.getObjectsFromDatabase(populationCountryQuery, PopulationReport::new, countryName, countryName, countryName);
                    System.out.println(report);
                });

        // this query uses a sub-query using group by to get the five major languages percentage and population
        // percentage equation is (country population * (language percentage/100) / world Population) * 100
        // population equation is country population * (language percentage/100)
        String languageQuery = "SELECT cl.Language, " +
                "(SUM(c.population * (cl.percentage/100)) / (SELECT sum(population) FROM country)) * 100 as Percentage, " +
                "SUM(c.population * (cl.percentage/100)) as Population " +
                "FROM countrylanguage cl " +
                "JOIN country c ON cl.countryCode = c.code " +
                "WHERE cl.Language IN ('Chinese', 'English', 'Hindi', 'Spanish', 'Arabic') " +
                "GROUP BY cl.Language " +
                "ORDER BY Percentage DESC;";
        List<LanguageReport> languageReport = objectMapper.getObjectsFromDatabase(languageQuery, LanguageReport::new);
        System.out.println(languageReport);

        // cleanup
        scanner.close();
        dbCon.disconnect();
    }
}
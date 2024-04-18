package com.napier.sem;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.List;

public class Application {


    private final DatabaseConnection dbCon = DatabaseConnection.from(
            "jdbc:mysql://db:3306/world?useSSL=false",
            //"jdbc:mysql://localhost:33060/world?allowPublicKeyRetrieval=true&useSSL=false",
            "root",
            "example"
    );

    private static ObjectMapper objectMapper; // Object mapper for interacting with the database

    /**
     * @param query query for accessing information from the country table with specific fields for report purposes
     * @return List of CountryReport objects for use in report generation
     */

    private static List<CountryReport> countryReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, CountryReport::new);
    }

    private static List<CapitalCityReport> capitalCityReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, CapitalCityReport::new);
    }

    private static List<CityReport> CityReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, CityReport::new);
    }

    private static List<LanguageReport> LanguageReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, LanguageReport::new);
    }

    private static List<PopulationReport> PopulationReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, PopulationReport::new);
    }

public class Application {

    private final DatabaseConnection dbCon = DatabaseConnection.from(
            "jdbc:mysql://db:3306/world?useSSL=false",
            "root",
            "example"
    );

    /**
     * Runs the application.
     */
    public void run() throws SQLException {
        dbCon.connect();

        // makes a connection to database then executes a statement to allow GROUP BY's to work with multiple concatenated fields
        Connection connection = dbCon.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("SET SESSION sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';");
        statement.close();


        objectMapper = new ObjectMapper(dbCon.getConnection());

        // 4 lists of distinct continents, regions, countries & districts for use in queries
        List<Distinct> continents = objectMapper.getObjectsFromDatabase("SELECT DISTINCT continent FROM country ORDER BY continent ASC", rs -> new Distinct(rs, "Continent"));
        List<Distinct> regions = objectMapper.getObjectsFromDatabase("SELECT DISTINCT region FROM country ORDER BY region ASC", rs -> new Distinct(rs, "Region"));
        List<Distinct> countries = objectMapper.getObjectsFromDatabase("SELECT DISTINCT name as 'country' FROM country ORDER BY 'country' ASC", rs -> new Distinct(rs, "Country"));
        List<Distinct> districts = objectMapper.getObjectsFromDatabase("SELECT DISTINCT district FROM city ORDER BY district ASC", rs -> new Distinct(rs, "District"));

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        int choice = 0;
        int n;

        System.out.println("What type of report would you like?");
        System.out.println("1. Country Report");
        System.out.println("2. Capital City Report");
        System.out.println("3. City Report");
        System.out.println("4. Country Language Report");
        System.out.print("Enter your choice: ");

        try {
            choice = scanner.nextInt();
            // Process input
        } catch (NoSuchElementException e) {
            System.out.println("No input provided.");
            dbCon.disconnect();
            System.exit(0);
        }
        scanner.nextLine();

        switch (choice) {
            case 1: {
                while (!exit) {
                    List<CountryReport> report;

                    System.out.println("Country Reports");
                    System.out.println("1. Generate report for all countries");
                    System.out.println("2. Generate report for countries by continent");
                    System.out.println("3. Generate report for countries by region");
                    System.out.println("4. Generate top N populated countries in the world");
                    System.out.println("5. Generate top N populated countries in a continent");
                    System.out.println("6. Generate top N populated countries in a region");
                    System.out.println("7. Exit");
                    System.out.print("Enter your choice: ");

                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    switch (choice) {
                        case 1:
                            //All the countries in the world organised by largest population to smallest.
                            report = new ArrayList<>(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 2:
                            //All the countries in a continent organised by largest population to smallest.
                            report = new ArrayList<>(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY continent ASC, population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 3:
                            //All the countries in a region organised by largest population to smallest.
                            report = new ArrayList<>(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY region ASC, population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 4:
                            //The top N populated countries in the world where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC LIMIT " + n));
                            System.out.println(report.toString());
                            break;
                        case 5:
                            //The top N populated countries in a continent where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct continent : continents) {
                                String continentName = continent.getName();
                                report.addAll(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND continent = '" + continentName + "'  ORDER BY continent ASC, population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 6:
                            //The top N populated countries in a region where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(countryReportOut("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND region = '" + regionName + "'  ORDER BY region ASC, population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 7:
                            exit = true;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }

            case 2: {
                while (!exit) {
                    List<CapitalCityReport> report;

                    System.out.println("Capital City Reports");
                    System.out.println("1. Generate report for all capital cities");
                    System.out.println("2. Generate report for all capital cities by continent");
                    System.out.println("3. Generate report for all capital cities by region");
                    System.out.println("4. Generate top N populated capital cities in the world");
                    System.out.println("5. Generate top N populated capital cities in a continent");
                    System.out.println("6. Generate top N populated capital cities in a region");
                    System.out.println("7. Exit");
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    switch (choice) {
                        case 1:
                            //All the capital cities in the world organised by largest population to smallest.
                            report = new ArrayList<>(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 2:
                            //All the capital cities in a continent organised by largest population to smallest.
                            report = new ArrayList<>(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 3:
                            //All the capital cities in a region organised by largest population to smallest.
                            report = new ArrayList<>(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 4:
                            //The top N populated capital cities in the world where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT " + n));
                            System.out.println(report.toString());
                            break;
                        case 5:
                            //The top N populated capital cities in a continent where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct continent : continents) {
                                String continentName = continent.getName();
                                report.addAll(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.continent = '" + continentName + "' AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 6:
                            //The top N populated capital cities in a region where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(capitalCityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = '" + regionName + "' AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 7:
                            exit = true;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }

            case 3: {
                while (!exit) {
                    List<CityReport> report;

                    System.out.println("City Reports");
                    System.out.println("1. Generate report for all cities");
                    System.out.println("2. Generate report for all cities by continent ordered");
                    System.out.println("3. Generate report for all cities by region");
                    System.out.println("4. Generate report for all cities by country");
                    System.out.println("5. Generate report for all cities by district");
                    System.out.println("6. Generate top N populated cities in the world");
                    System.out.println("7. Generate top N populated cities in a continent");
                    System.out.println("8. Generate top N populated cities in a region");
                    System.out.println("9. Generate top N populated cities in a country");
                    System.out.println("10. Generate top N populated cities in a district");
                    System.out.println("11. Exit");
                    System.out.print("Enter your choice: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    switch (choice) {
                        case 1:
                            //All the cities in the world organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC"));
                            System.out.println(report.toString());
                            break;

                        case 2:
                            //All the cities in a continent organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;

                        case 3:
                            //All the cities in a region organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;

                        case 4:
                            //All the cities in a country organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.name ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;

                        case 5:
                            //All the cities in a district organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.district ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;

                        case 6:
                            //The top N populated cities in the world where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT " + n));
                            System.out.println(report.toString());
                            break;

                        case 7:
                            //The top N populated cities in a continent where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct continent : continents) {
                                String continentName = continent.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.continent = '" + continentName + "' AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;

                        case 8:
                            //The top N populated cities in a region where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            // Goes through a list of regions and runs a query against one to get an independent set of results based on N
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.region = '" + regionName + "' AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;

                        case 9:
                            //The top N populated cities in a country where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct country : countries) {
                                String countryName = country.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.name = '" + countryName + "' AND country.code = city.countryCode ORDER BY 'country' ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;

                        case 10:
                            //The top N populated cities in a district where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct district : districts) {
                                String districtName = district.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE city.district = '" + districtName + "' AND country.code = city.countryCode ORDER BY city.district ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;

                        case 11:
                            exit = true;
                            break;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
            case 4: {
                while (!exit) {
                    List<LanguageReport> languageReport;
                    List<PopulationReport> populationReport;

                    System.out.println("Population Reports");
                    System.out.println("1. Generate report for number & % of people livings in cities (or not living in cities) in a continent");
                    System.out.println("2. Generate report for number & % of people livings in cities (or not living in cities) in a region");
                    System.out.println("3. Generate report for number & % of people livings in cities (or not living in cities) in a country");
                    System.out.println("4. Generate report for percentage and number of those who speak the major 5 languages in the world");
                    System.out.print("Enter your choice: ");

                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    switch (choice) {

                        case 1:
                            populationReport = new ArrayList<>();
                            for (Distinct continent : continents) {
                                String continentName = continent.getName();
                                populationReport.addAll(PopulationReportOut(""));
                            }
                            System.out.println(populationReport);
                            break;

                        case 2:
                            populationReport = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                populationReport.addAll(PopulationReportOut(""));
                            }
                            break;

                        case 3:
                            populationReport = new ArrayList<>();
                            for (Distinct country : countries) {
                                String regionName = country.getName();
                                populationReport.addAll(PopulationReportOut(""));
                            }
                            break;

                        case 4:
                            // this query uses a subquery using group by to get the five major languages percentage and population
                            // percentage equation is (country population * (language percentage/100) / world Population) * 100
                            // population equation is country population * (language percentage/100)
                            String query = "SELECT cl.Language, " +
                                    "(SUM(c.population * (cl.percentage/100)) / " +
                                    "(SELECT sum(population) FROM country)) * 100 as Percentage, " +
                                    "SUM(c.population * (cl.percentage/100)) as Population " +
                                    "FROM countrylanguage cl " +
                                    "JOIN country c ON cl.countryCode = c.code " +
                                    "WHERE cl.Language IN ('Chinese', 'English', 'Hindi', 'Spanish', 'Arabic') " +
                                    "GROUP BY cl.Language " +
                                    "ORDER BY Percentage DESC;";
                            languageReport = new ArrayList<>(LanguageReportOut(query));
                            System.out.println(languageReport.toString());
                            break;

                        case 5:
                            exit = true;
                            break;
                    }
                }
            }
        }
        scanner.close();
        dbCon.disconnect();
    }
}


}
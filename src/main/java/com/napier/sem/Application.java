package com.napier.sem;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.CapitalCityReport;
import com.napier.sem.database.model.CountryLanguage;
import com.napier.sem.database.model.CityReport;
import com.napier.sem.database.model.Distinct;
import com.napier.sem.database.model.CountryReport;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;

public class Application {


    private final DatabaseConnection dbCon = DatabaseConnection.from(
            //"jdbc:mysql://db:3306/world?useSSL=false",
            "jdbc:mysql://localhost:33060/world?allowPublicKeyRetrieval=true&useSSL=false",
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

    /**
     * Runs the application.
     */
    public void run() {
        dbCon.connect();

        objectMapper = new ObjectMapper(dbCon.getConnection());

        // 2 lists of distinct continents & regions for use in queries
        List<Distinct> continents = objectMapper.getObjectsFromDatabase("SELECT DISTINCT continent FROM country ORDER BY continent ASC", rs -> new Distinct(rs, "Continent"));
        List<Distinct> regions = objectMapper.getObjectsFromDatabase("SELECT DISTINCT region FROM country ORDER BY region ASC", rs -> new Distinct(rs, "Region"));

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        int choice;
        int n;

        System.out.println("What type of report would you like?");
        System.out.println("1. Country Report");
        System.out.println("2. Capital City Report");
        System.out.println("3. City Report");
        System.out.println("4. Country Language Report");
        System.out.print("Enter your choice: ");

        choice = scanner.nextInt();
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
                        report = new ArrayList<>(countryReportOut("SELECT * FROM country ORDER BY population DESC"));
                        System.out.println(report.toString());
                        break;
                    case 2:
                        //All the countries in a continent organised by largest population to smallest.
                        report = new ArrayList<>(countryReportOut("SELECT * FROM country ORDER BY continent ASC, population DESC"));
                        System.out.println(report.toString());
                        break;
                    case 3:
                        //All the countries in a region organised by largest population to smallest.
                        report = new ArrayList<>(countryReportOut("SELECT * FROM country ORDER BY region ASC, population DESC"));
                        System.out.println(report.toString());
                        break;
                    case 4:
                        //The top N populated countries in the world where N is provided by the user.
                        System.out.print("Please select N: ");
                        n = scanner.nextInt();
                        scanner.nextLine();
                        report = new ArrayList<>(countryReportOut("SELECT * FROM country ORDER BY population DESC LIMIT " + n));
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
                            report.addAll(countryReportOut("SELECT * FROM country WHERE continent = '" + continentName + "'  ORDER BY continent ASC, population DESC LIMIT " + n));
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
                            report.addAll(countryReportOut("SELECT * FROM country WHERE region = '" + regionName + "'  ORDER BY region ASC, population DESC LIMIT " + n));
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
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 2:
                            //All the cities in a continent organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 3:
                            //All the cities in a region organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 4:
                            //All the cities in a country organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.name ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 5:
                            //All the cities in a district organised by largest population to smallest.
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.district ASC, city.population DESC"));
                            System.out.println(report.toString());
                            break;
                        case 6:
                            //The top N populated cities in the world where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT " + n));
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
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.continent = '" + continentName + "' AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 8:
                            //The top N populated cities in a region where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = '" + regionName + "' AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 9:
                            //The top N populated cities in a country where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = '" + regionName + "' AND country.code = city.countryCode ORDER BY country.name ASC, city.population DESC LIMIT " + n));
                            }
                            System.out.println(report.toString());
                            break;
                        case 10:
                            //The top N populated cities in a district where N is provided by the user.
                            System.out.print("Please select N: ");
                            n = scanner.nextInt();
                            scanner.nextLine();
                            report = new ArrayList<>();
                            for (Distinct region : regions) {
                                String regionName = region.getName();
                                report.addAll(CityReportOut("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = '" + regionName + "' AND country.code = city.countryCode ORDER BY city.district ASC, city.population DESC LIMIT " + n));
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
        }
        scanner.close();
        dbCon.disconnect();
    }
}
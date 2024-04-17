package com.napier.sem;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.Distinct;
import com.napier.sem.database.model.CountryReport;

import java.util.ArrayList;
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
     *
     * @param query query for accessing information from the country table with specific fields for report purposes
     * @return List of CountryReport objects for use in report generation
     */

    private static List<CountryReport> countryReportOut(String query) {
        return objectMapper.getObjectsFromDatabase(query, CountryReport::new);
    }

    /**
     * Runs the application.
     */
    public void run()
    {
        dbCon.connect();

        objectMapper = new ObjectMapper(dbCon.getConnection());

        // 2 lists of distinct continents & regions for use in queries
        List<Distinct> continents = objectMapper.getObjectsFromDatabase("SELECT DISTINCT continent FROM country ORDER BY continent ASC", rs -> new Distinct(rs, "Continent"));
        List<Distinct> regions = objectMapper.getObjectsFromDatabase("SELECT DISTINCT region FROM country ORDER BY region ASC", rs -> new Distinct(rs, "Region"));


        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("Country Report CLI");
            System.out.println("1. Generate report for all countries");
            System.out.println("2. Generate report for countries by continent");
            System.out.println("3. Generate report for countries by region");
            System.out.println("4. Generate top N populated countries in the world");
            System.out.println("5. Generate top N populated countries in a continent");
            System.out.println("6. Generate top N populated countries in a region");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline left-over

            int n;
            List<CountryReport> report;

            switch (choice) {
                case 1:
                    //All the countries in the world organised by largest population to smallest.
                    countryReportOut("SELECT * FROM country ORDER BY population DESC");
                    break;
                case 2:
                    //All the countries in a continent organised by largest population to smallest.
                    countryReportOut("SELECT * FROM country ORDER BY continent ASC, population DESC");
                    break;
                case 3:
                    //All the countries in a region organised by largest population to smallest.
                    countryReportOut("SELECT * FROM country ORDER BY region ASC, population DESC");
                    break;
                case 4:
                    //The top N populated countries in the world where N is provided by the user.
                    System.out.print("Please select N: ");
                    n = scanner.nextInt();
                    scanner.nextLine();
                    countryReportOut("SELECT * FROM country ORDER BY population DESC LIMIT " + n);
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
        scanner.close();
        dbCon.disconnect();
    }
    }
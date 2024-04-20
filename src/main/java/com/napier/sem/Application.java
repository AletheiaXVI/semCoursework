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
                "jdbc:mysql://db:3306/world?useSSL=false",
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
        boolean exit = false;
        int choice = 0;
        int input;

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

                        // All the countries in the world organised by largest population to smallest.
                        case 1 -> {
                            List<CountryReport> countryReport = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC", CountryReport::new);
                            System.out.println(countryReport);
                        }

                        //All the countries in a continent organised by largest population to smallest.
                        case 2 -> {
                            List<CountryReport> countriesByContinent = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY continent ASC, population DESC", CountryReport::new);
                            System.out.println(countriesByContinent);
                        }

                        //All the countries in a region organised by largest population to smallest.
                        case 3 -> {
                            List<CountryReport> countriesByRegion = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY region ASC, population DESC", CountryReport::new);
                            System.out.println(countriesByRegion);
                        }

                        //The top N populated countries in the world where N is provided by the user.
                        case 4 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();
                            List<CountryReport> countryReports = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id ORDER BY population DESC LIMIT ?", CountryReport::new, input);
                            System.out.println(countryReports);
                        }

                        //The top N populated countries in a continent where N is provided by the user.
                        case 5 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalN = input; // lazy loading of final variable
                            continents.stream()
                                    .map(Distinct::getName)
                                    .forEach(continentName -> {
                                        List<CountryReport> report = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND continent = '?'  ORDER BY continent ASC, population DESC LIMIT ?", CountryReport::new, continentName, finalN);
                                        System.out.println(report);
                                    });

                        }

                        //The top N populated countries in a region where N is provided by the user.
                        case 6 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalN = input; // lazy loading of final variable
                            regions.stream()
                                    .map(Distinct::getName)
                                    .forEach(regionName -> {
                                        List<CountryReport> report = objectMapper.getObjectsFromDatabase("SELECT country.code, country.name, country.continent, country.region, country.population, city.name as 'capital' FROM country, city WHERE country.capital = city.id AND region = '?'  ORDER BY region ASC, population DESC LIMIT ?", CountryReport::new, regionName, finalN);
                                        System.out.println(report);
                                    });

                        }
                        case 7 -> exit = true;
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                }
            }

            case 2: {
                while (!exit) {
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

                        //All the capital cities in the world organised by largest population to smallest.
                        case 1 -> {
                            List<CapitalCityReport> cityReport = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC", CapitalCityReport::new);
                            System.out.println(cityReport);
                        }

                        //All the capital cities in a continent organised by largest population to smallest.
                        case 2 -> {
                            List<CapitalCityReport> cityReport = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC", CapitalCityReport::new);
                            System.out.println(cityReport);
                        }

                        //All the capital cities in a region organised by largest population to smallest.
                        case 3 -> {
                            List<CapitalCityReport> cityReport = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC", CapitalCityReport::new);
                            System.out.println(cityReport);
                        }

                        //The top N populated capital cities in the world where N is provided by the user.
                        case 4 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();
                            List<CapitalCityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT ?", CapitalCityReport::new, input);
                            System.out.println(cityReports);
                        }

                        //The top N populated capital cities in a continent where N is provided by the user.
                        case 5 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalN = input; // lazy loading of final variable
                            continents.stream()
                                    .map(Distinct::getName)
                                    .forEach(continentName -> {
                                        List<CapitalCityReport> report = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.continent = '?' AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT ?", CapitalCityReport::new, continentName, finalN);
                                        System.out.println(report);
                                    });
                        }

                        //The top N populated capital cities in a region where N is provided by the user.
                        case 6 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalN = input; // lazy loading of final variable
                            regions.stream()
                                    .map(Distinct::getName)
                                    .forEach(regionName -> {
                                        List<CapitalCityReport> report = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country_name, city.population FROM country, city WHERE country.region = '?' AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT ?", CapitalCityReport::new, regionName, finalN);
                                        System.out.println(report);
                                    });
                        }
                        case 7 -> exit = true;
                        default -> System.out.println("Invalid choice. Please try again.");
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

                        //All the cities in the world organised by largest population to smallest.
                        case 1 -> {
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC", CityReport::new);
                            System.out.println(cityReports);
                        }

                        //All the cities in a continent organised by largest population to smallest.
                        case 2 -> {
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC", CityReport::new);
                            System.out.println(cityReports);
                        }

                        //All the cities in a region organised by largest population to smallest.
                        case 3 -> {
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.region ASC, city.population DESC", CityReport::new);
                            System.out.println(cityReports);
                        }

                        //All the cities in a country organised by largest population to smallest.
                        case 4 -> {
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY country.name ASC, city.population DESC", CityReport::new);
                            System.out.println(cityReports);
                        }

                        //All the cities in a district organised by largest population to smallest.
                        case 5 -> {
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.district ASC, city.population DESC", CityReport::new);
                            System.out.println(cityReports);                        }

                        //The top N populated cities in the world where N is provided by the user.
                        case 6 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();
                            List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.code = city.countryCode ORDER BY city.population DESC LIMIT ?", CityReport::new, input);
                            System.out.println(cityReports);                        }

                        //The top N populated cities in a continent where N is provided by the user.
                        case 7 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalInput = input; // lazy loading of final variable
                            continents.stream()
                                    .map(Distinct::getName)
                                    .forEach(continentName -> {
                                        List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.continent = '?' AND country.code = city.countryCode ORDER BY country.continent ASC, city.population DESC LIMIT ?", CityReport::new, continentName, finalInput);
                                        System.out.println(cityReports);
                                    });
                        }

                        //The top N populated cities in a region where N is provided by the user.
                        case 8 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalInput = input; // lazy loading of final variable
                            regions.stream()
                                    .map(Distinct::getName)
                                    .forEach(regionName -> {
                                        List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.region = '?' AND country.code = city.countryCode ORDER BY country.region ASC, city.population DESC LIMIT ?", CityReport::new, regionName, finalInput);
                                        System.out.println(cityReports);
                                    });
                        }

                        //The top N populated cities in a country where N is provided by the user.
                        case 9 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalInput = input; // lazy loading of final variable
                            countries.stream()
                                    .map(Distinct::getName)
                                    .forEach(countryName -> {
                                        List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE country.name = '?' AND country.code = city.countryCode ORDER BY 'country' ASC, city.population DESC LIMIT ?", CityReport::new, countryName, finalInput);
                                        System.out.println(cityReports);
                                    });
                        }

                        //The top N populated cities in a district where N is provided by the user.
                        case 10 -> {
                            System.out.print("Please select N: ");
                            input = scanner.nextInt();
                            scanner.nextLine();

                            int finalInput = input; // lazy loading of final variable
                            districts.stream()
                                    .map(Distinct::getName)
                                    .forEach(districtName -> {
                                        List<CityReport> cityReports = objectMapper.getObjectsFromDatabase("SELECT city.name, country.name AS country, city.district, city.population FROM country, city WHERE city.district = '?' AND country.code = city.countryCode ORDER BY city.district ASC, city.population DESC LIMIT ?", CityReport::new, districtName, finalInput);
                                        System.out.println(cityReports);
                                    });
                        }
                        case 11 -> exit = true;
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
            case 4: {
                while (!exit) {
                    System.out.println("Population Reports");
                    System.out.println("1. Generate report for number & % of people livings in cities (or not living in cities) in a continent");
                    System.out.println("2. Generate report for number & % of people livings in cities (or not living in cities) in a region");
                    System.out.println("3. Generate report for number & % of people livings in cities (or not living in cities) in a country");
                    System.out.println("4. Generate report for percentage and number of those who speak the major 5 languages in the world");
                    System.out.print("Enter your choice: ");

                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over

                    switch (choice) {

                        case 1 -> {
                            continents.stream()
                                    .map(Distinct::getName)
                                    .forEach(continentName -> {
                                        List<PopulationReport> report = objectMapper.getObjectsFromDatabase("TODO", PopulationReport::new);
                                        System.out.println(report);
                                    });
                        }

                        case 2 -> {
                            regions.stream()
                                    .map(Distinct::getName)
                                    .forEach(regionName -> {
                                        List<PopulationReport> report = objectMapper.getObjectsFromDatabase("TODO", PopulationReport::new);
                                        System.out.println(report);
                                    });
                        }

                        case 3 -> {
                            countries.stream()
                                    .map(Distinct::getName)
                                    .forEach(countryName -> {
                                        List<PopulationReport> report = objectMapper.getObjectsFromDatabase("TODO", PopulationReport::new);
                                        System.out.println(report);
                                    });
                        }

                        case 4 -> {
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
                            List<LanguageReport> languageReport = objectMapper.getObjectsFromDatabase(query, LanguageReport::new);
                            System.out.println(languageReport);
                        }

                        case 5 -> exit = true;
                    }
                }
            }
        }

        // cleanup
        scanner.close();
        dbCon.disconnect();
    }
}
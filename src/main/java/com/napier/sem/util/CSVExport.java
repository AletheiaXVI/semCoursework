package com.napier.sem.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.napier.sem.database.DatabaseConnection;
import com.napier.sem.database.ObjectMapper;
import com.napier.sem.database.model.City;
import com.napier.sem.database.model.Country;


/**
 * This class exports data to a CSV format.
 * @author James Vincent
 */
public class CSVExport {

    private static DatabaseConnection dbCon; // Database connection for data retrieval
    private static ObjectMapper objectMapper; // Object mapper for interacting with the database

    /**
     * Sets up database connection and object mapper.
     */
    static void init() {

        dbCon = DatabaseConnection.from(
                "jdbc:mysql://localhost:33060/world?allowPublicKeyRetrieval=true&useSSL=false",
                "root",
                "example"
        );
        dbCon.connect();
        objectMapper = new ObjectMapper(dbCon.getConnection());
    }

    private static final String COMMA = ","; // Delimiter for separating fields in CSV
    private static final String DOUBLE_QUOTES = "\""; // Character used to enclose fields in CSV
    private static final String EMBEDDED_DOUBLE_QUOTES = "\"\""; // Escape sequence for quotes within a field

    /**
     * Formats a field for CSV by adding quotes if it has special characters.
     *
     * @param field The string representing a field in the CSV data.
     * @return The formatted field string with proper escaping if necessary.
     */

    private String formatField(String field) {
        if (field == null) {
            return ""; // or some other default value
        }
        if (field.contains(COMMA) || field.contains(DOUBLE_QUOTES) || field.contains("\n") || field.contains("\r")) {
            return DOUBLE_QUOTES + field.replace(DOUBLE_QUOTES, EMBEDDED_DOUBLE_QUOTES) + DOUBLE_QUOTES;
        }
        return field;
    }


    /**
     * Prepares a row of data for CSV by escaping special characters.
     *
     * @param row The list of strings representing a row in the CSV file.
     * @return A new array of strings with formatted fields for CSV output.
     */
    private String[] formatRow(String[] row) {
        return Stream.of(row)
                .map(this::formatField)
                .toArray(String[]::new);
    }

    /**
     * Converts a list of strings representing a row to a formatted CSV string.
     *
     * @param row The list of strings representing a row in the CSV file.
     * @return The formatted CSV string representation of the row.
     */
    private String convertToCSV(String[] row) {
        return String.join(COMMA, formatRow(row));
    }

    /**
     * Writes the provided data to a CSV file at the specified file path.
     *
     * @param data The list of string arrays representing the data to be written.
     * @param filePath The path to the CSV file where the data will be written.
     * @throws IOException If an error occurs during file writing.
     */
    public void writeToCSV(List<String[]> data, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(convertToCSV(row));
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        init(); // Connects to the database


        // Country Report Stuff
        // Function to convert Country object to String array for CSV
        Function<Country, String[]> countryToStringArray = country -> new String[]{
                country.getCode(),
                country.getName(),
                country.getContinent(),
                country.getRegion(),
                String.valueOf(country.getPopulation()),
                String.valueOf(country.getCapital()),
                country.getCode2()
        };

        //Retrieve countries and convert to CSV data
        List<Country> countries = objectMapper.getObjectsFromDatabase("SELECT * FROM country", Country::new);

        List<String[]> countryData = new ArrayList<>();
        for (Country country : countries) {
            countryData.add(countryToStringArray.apply(country));
        }

        CSVExport writerCountry = new CSVExport();
        writerCountry.writeToCSV(countryData, "Countries.csv");

        // City Report Stuff

        // Function to convert City object to String array for CSV
        Function<City, String[]> cityToStringArray = city -> new String[]{
                String.valueOf(city.getId()),
                city.getName(),
                city.getCountryCode(),
                city.getDistrict(),
                String.valueOf(city.getPopulation()),
        };

        // Retrieve cities and convert to CSV data
        List<City> cities = objectMapper.getObjectsFromDatabase("SELECT * FROM city", City::new);

        List<String[]> cityData = new ArrayList<>();
        for (City city : cities) {
            cityData.add(cityToStringArray.apply(city));
        }

        CSVExport writerCity = new CSVExport();
        writerCity.writeToCSV(cityData, "Cities.csv");

        // Capital City Report Stuff
        // Function to extract capital city data from Country object
        Function<Country, String[]> countryToCapitalArray = country -> {
            int capitalCityID = country.getCapital();

            if (capitalCityID <= 0) {
                return new String[]{"", "", ""}; // Handle invalid ID
            }

            List<City> potentialCapitals = objectMapper.getObjectsFromDatabase("SELECT * FROM city", City::new);
            City capitalCity = potentialCapitals.stream()
                    .filter(city -> city.getId() == capitalCityID)
                    .findFirst()
                    .orElse(null);

            if (capitalCity == null) {
                return new String[]{"", "", ""}; // Handle missing capital
            }

            String capitalName = capitalCity.getName();
            String countryName = country.getName();
            long capitalPopulation = capitalCity.getPopulation();

            return new String[]{capitalName, countryName, String.valueOf(capitalPopulation)};
        };

        // Retrieve countries and process capital city data
        List<Country> capitals = objectMapper.getObjectsFromDatabase("SELECT * FROM country", Country::new);

        List<String[]> capitalData = new ArrayList<>();
        for (Country capital : capitals) {
            capitalData.add(countryToCapitalArray.apply(capital));
        }

        CSVExport writerCapital = new CSVExport();
        writerCapital.writeToCSV(capitalData, "Capitals.csv");

        // Population Report Stuff

        // Function to calculate urban and rural population percentages
        Function<Country, String[]> countryToPopulationData = country -> {
            int totalPopulation = country.getPopulation();

            // Find total city population for the country
            List<City> allCities = objectMapper.getObjectsFromDatabase(
                    "SELECT * FROM city WHERE CountryCode = ?", City::new, country.getCode()
            );
            int cityPopulation = allCities.stream().mapToInt(City::getPopulation).sum();

            // Calculate rural population
            int ruralPopulation = totalPopulation - cityPopulation;

            // Calculate percentages
            double urbanPercentage = (double) cityPopulation / totalPopulation * 100;
            double ruralPercentage = (double) ruralPopulation / totalPopulation * 100;

            return new String[]{
                    country.getContinent(),
                    country.getRegion(),
                    country.getName(),
                    String.valueOf(totalPopulation),
                    String.format("%.2f%%", urbanPercentage),
                    String.format("%.2f%%", ruralPercentage),
            };
        };

        // Retrieve countries and process population data
        List<Country>allCountries = objectMapper.getObjectsFromDatabase("SELECT * FROM country", Country::new);

        List<String[]> populationData = new ArrayList<>();
        for (Country country : allCountries) {
            populationData.add(countryToPopulationData.apply(country));
        }

        CSVExport writerPopulation = new CSVExport();
        writerPopulation.writeToCSV(populationData, "Populations.csv");

    }
}

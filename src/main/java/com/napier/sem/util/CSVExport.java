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

         // Converts a Country object to a String array suitable for CSV output
        Function<Country, String[]> countryToStringArray = country -> new String[] {
                country.getCode(),
                country.getName(),
                country.getContinent().toString(),
                country.getRegion(),
                String.valueOf(country.getSurfaceArea()),
                String.valueOf(country.getIndepYear()),
                String.valueOf(country.getPopulation()),
                String.valueOf(country.getLifeExpectancy()),
                String.valueOf(country.getGnp()),
                String.valueOf(country.getGnpOld()),
                country.getLocalName(),
                country.getGovernmentForm(),
                country.getHeadOfState(),
                String.valueOf(country.getCapital()),
                country.getCode2()
        };

        List<Country> countries = objectMapper.getObjectsFromDatabase("SELECT * FROM country", Country::new);

        List<String[]> data = new ArrayList<>();
        for (Country country : countries) {
            data.add(countryToStringArray.apply(country));
        }

        CSVExport writerCountry = new CSVExport();
        writerCountry.writeToCSV(data, "Countries.csv");
    }
}

package com.napier.sem.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import com.napier.sem.database.model.Country;

// CSVExport class - Takes data and outputs .csv file
public class CSVExport {
    // Constants for CSV formatting
    private static final String COMMA = ",";
    private static final String DOUBLE_QUOTES = "\"";
    private static final String EMBEDDED_DOUBLE_QUOTES = "\"\"";

    // Method to write data to a CSV file
    public void writeToCSV(List<String[]> data, String filePath) throws IOException {
        // Use try-with-resources to ensure the writer is closed properly
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Iterate over each row of data
            for (String[] row : data) {
                // Convert the row to CSV format and write it to the file
                writer.write(convertToCSV(row));
                // Write a newline character to start a new row
                writer.newLine();
            }
        }
    }

    // Method to convert a row of data to CSV format
    private String convertToCSV(String[] row) {
        // Join the row elements with commas
        return String.join(COMMA, formatRow(row));
    }

    // Method to format each row of data
    private String[] formatRow(String[] row) {
        // Formats each field in the row
        return Stream.of(row)
                .map(this::formatField)
                .toArray(String[]::new);
    }

    // Method to format a field, handling special characters
    private String formatField(String field) {
        // If the field contains a comma, double quote, or newline, enclose it in double quotes
        if (field.contains(COMMA) || field.contains(DOUBLE_QUOTES) || field.contains("\n") || field.contains("\r")) {
            // Replace any existing double quotes in the field with two double quotes
            return DOUBLE_QUOTES + field.replace(DOUBLE_QUOTES, EMBEDDED_DOUBLE_QUOTES) + DOUBLE_QUOTES;
        }
        // Otherwise, return the field as is
        return field;
    }

    //Main method for testing
    public static void main(String[] args) throws IOException {
        // Example data to write to a CSV file
        List<String[]> data = List.of(
                new String[]{"Name", "Age", "Location"},
                new String[]{"Sneebs", "27", "Fife"},
                new String[]{"Bim Jimbus", "72", "308 Negra Arroyo Lane, Albuquerque, New Mexico, 87104"},
                new String[]{"Chumba Wumbus", "59", "A Tesco car park in the middle of Scunthorpe" },
                new String[]{"Andrew Cuomo", "17 months", "Bannamin Papil Bridge-End Burra, Shetland, ZE2 9UY"}
        );


        //ArrayList<String[]> countryData = new CSVExport();
        //countryData.add(Country::getCode());

        // Create a CsvWriter instance and write the data to a file
        CSVExport writer = new CSVExport();
        // Filepath goes to /semCoursework
        writer.writeToCSV(data, "output.csv");

        //CSVExport writerCountry = new CSVExport();
        //writerCountry.writeToCSV();
    }
}

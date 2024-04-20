package com.napier.sem.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class exports data to a CSV format.
 * @author James Vincent, Oskar Budzisz
 */
public class CSVExport {

    private static final String COMMA = ","; // Delimiter for separating fields in CSV
    private static final String DOUBLE_QUOTES = "\""; // Character used to enclose fields in CSV
    private static final String EMBEDDED_DOUBLE_QUOTES = "\"\""; // Escape sequence for quotes within a field

    private final String filePath; // Path to the CSV file to be written

    /**
     * Creates a new CSVExport object with a specified file path.
     *
     * @param filePath The path to the CSV file to be written.
     */
    public CSVExport(String filePath) {
        assert filePath != null : "File path must not be null";
        assert !filePath.isEmpty() : "File path must not be empty";

        this.filePath = filePath;
    }

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
     * @throws IOException If an error occurs during file writing.
     */
    public void writeToCSV(List<String[]> data) throws IOException {
        assert data != null : "Data must not be null";
        assert !data.isEmpty() : "Data must not be empty";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(convertToCSV(row));
                writer.newLine();
            }
        }
    }
}

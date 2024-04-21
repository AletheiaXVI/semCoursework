package com.napier.sem.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class exports data to a Markdown format.
 * @author James Vincent
 */
public class MDExport {

    private static final String NEW_LINE = "\n"; // New line character for Markdown
    private final String filePath; // Path to the Markdown file to be written

    /**
     * Creates a new MDExport object with a specified file path.
     *
     * @param filePath The path to the Markdown file to be written.
     */
    public MDExport(String filePath) {
        assert filePath != null : "File path must not be null";
        assert !filePath.isEmpty() : "File path must not be empty";

        this.filePath = filePath;
    }

    /**
     * Formats a field for Markdown by adding quotes if it has special characters.
     *
     * @param field The string representing a field in the Markdown data.
     * @return The formatted field string with proper escaping if necessary.
     */
    private String formatField(String field) {
        if (field == null) {
            return "";
        }
        return field;
    }

    /**
     * Prepares a row of data for Markdown by formatting fields.
     *
     * @param row The list of strings representing a row in the Markdown file.
     * @return A new array of strings with formatted fields for Markdown output.
     */
    private String[] formatRow(String[] row) {
        return Stream.of(row)
                .map(this::formatField)
                .toArray(String[]::new);
    }

    /**
     * Converts a list of strings representing a row to a formatted Markdown string.
     *
     * @param row The list of strings representing a row in the Markdown file.
     * @return The formatted Markdown string representation of the row.
     */
    private String convertToMD(String[] row) {
        String[] formattedRow = formatRow(row);
        return String.join(" | ", formattedRow) + NEW_LINE;
    }


    /**
     * Writes the provided data to a Markdown file at the specified file path.
     *
     * @param data The list of string arrays representing the data to be written.
     * @throws IOException If an error occurs during file writing.
     */
    public void writeToMD(List<String[]> data) throws IOException {
        assert data != null : "Data must not be null";
        assert !data.isEmpty() : "Data must not be empty";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String[] row : data) {
                writer.write(convertToMD(row));
            }
        }
    }
}


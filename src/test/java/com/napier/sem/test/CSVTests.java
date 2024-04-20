package com.napier.sem.test;

import com.napier.sem.util.CSVExport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CSVTests {

    private final String fileName = "test.csv";

    /**
     * Test that the CSVExport constructor throws an error when the file path is null.
     */
    @Test
    void testAssertErrorNullCSVExport() {
        assertThrows(AssertionError.class, () -> new CSVExport(null));
    }

    /**
     * Test that the CSVExport constructor throws an error when the file path is empty.
     */
    @Test
    void testAssertErrorEmptyCSVExportFile() {
        assertThrows(AssertionError.class, () -> new CSVExport(""));
    }

    /**
     * Test that the CSVExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileNullWrite() {
        CSVExport csvExport = new CSVExport(fileName);
        assertThrows(AssertionError.class, () -> csvExport.writeToCSV(null));
    }

    /**
     * Test that the CSVExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileEmptyWrite() {
        CSVExport csvExport = new CSVExport(fileName);
        assertThrows(AssertionError.class, () -> csvExport.writeToCSV(List.of()));
    }

    /**
     * Test that the CSVExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileWrite() throws IOException {
        CSVExport csvExport = new CSVExport(fileName);
        csvExport.writeToCSV(List.of(new String[]{"line 1", "line 2"}, new String[]{"test1", "test2"}));

        File file = new File(fileName);
        Files.readAllLines(file.toPath()).forEach(System.out::println);
    }

    /**
     * Clean up the test file after all tests have run.
     */
    @AfterEach
    void afterEach() {
        new File("test.csv").delete();
    }
}

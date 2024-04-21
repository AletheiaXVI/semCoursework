package com.napier.sem.test;

import com.napier.sem.util.MDExport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MDTests {

    private final String fileName = "test.md";

    /**
     * Test that the MDExport constructor throws an error when the file path is null.
     */
    @Test
    void testAssertErrorNullMarkdownExport() {
        assertThrows(AssertionError.class, () -> new MDExport(null));
    }

    /**
     * Test that the MDExport constructor throws an error when the file path is empty.
     */
    @Test
    void testAssertErrorEmptyMarkdownExportFile() {
        assertThrows(AssertionError.class, () -> new MDExport(""));
    }

    /**
     * Test that the MDExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileNullWrite() {
        MDExport mdExport = new MDExport(fileName);
        assertThrows(AssertionError.class, () -> mdExport.writeToMD(null));
    }

    /**
     * Test that the MDExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileEmptyWrite() {
        MDExport mdExport = new MDExport(fileName);
        assertThrows(AssertionError.class, () -> mdExport.writeToMD(List.of()));
    }

    /**
     * Test that the MDExport constructor does not throw an error when the file path is valid.
     */
    @Test
    void testFileWrite() throws IOException {
        MDExport mdExport = new MDExport(fileName);
        mdExport.writeToMD(List.of(new String[]{"Header1", "Header2"}, new String[]{"Row1Value1", "Row1Value2"}));

        File file = new File(fileName);
        Files.readAllLines(file.toPath()).forEach(System.out::println);
    }

    /**
     * Clean up the test file after all tests have run.
     */
    @AfterEach
    void afterEach() {
        new File("test.md").delete();
    }
}

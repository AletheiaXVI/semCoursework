package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a row in the CountryLanguage table in the database.
 */
public class LanguageReport {

    private final String language;
    private final double percentage;
    private final long population;

    public LanguageReport(ResultSet rs)  {
        try {
            this.language = rs.getString("Language");
            this.percentage = rs.getDouble("Percentage");
            this.population = rs.getLong("Population");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLanguage() {
        return language;
    }

    public double getPercentage() {
        return percentage;
    }

    public long getPopulation() { return population; }

    @Override
    public String toString() {
        return "language='" + language + '\'' +
                ", percentage=" + percentage + "%" +
                ", population=" + population +
                '\n';
    }
}

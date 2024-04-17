package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents the output for country reports from a SQL query
 */
public class CountryReport {
    private final String code;
    private final String name;
    private final String continent;
    private final String region;
    private final int population;
    private final int capital;

    public CountryReport(ResultSet rs) {
        try {
            this.code = rs.getString("Code");
            this.name = rs.getString("Name");
            this.continent = rs.getString("Continent");
            this.region = rs.getString("Region");
            this.population = rs.getInt("Population");
            this.capital = rs.getInt("Capital");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getContinent() { return continent; }
    public String getRegion() {
        return region;
    }
    public int getPopulation() {
        return population;
    }

    public int getCapital() {
        return capital;
    }

    @Override
    public String toString() {
        return  "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", continent=" + continent + '\'' +
                ", region='" + region + '\'' +
                ", population=" + population +
                ", capital=" + capital +
                '\n';
    }
}
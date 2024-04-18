package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a row in the Country table in the database.
 */
public class CapitalCityReport {
    private final String name;
    private final String capital;
    private final int population;

    public CapitalCityReport(ResultSet rs) {
        try {
            this.capital = rs.getString("Name");
            this.name = rs.getString("Country_Name");
            this.population = rs.getInt("Population");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public String getCapital() {
        return capital;
    }


    @Override
    public String toString() {
        return ", capital='" + capital + '\'' +
                ", country='" + name + '\'' +
                ", population=" + population +
                '\n';
    }
}


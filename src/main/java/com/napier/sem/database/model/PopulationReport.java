package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a row in the CountryLanguage table in the database.
 */
public class PopulationReport {

    private final String name;
    private final long population;
    private final double cityPercentage;
    private final double ruralPercentage;

    public PopulationReport(ResultSet rs)  {
        try {
            this.name = rs.getString("Name");
            this.population = rs.getLong("Population");
            this.cityPercentage = rs.getDouble("CityPercentage");
            this.ruralPercentage = rs.getDouble("RuralPercentage");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() { return name; }

    public long getPopulation() { return population; }
    public double getCityPercentage() { return cityPercentage; }
    public double getRuralPercentage() { return ruralPercentage; }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", population=" + population +
                ", city percentage=" + cityPercentage + "%" +
                ", rural percentage=" + ruralPercentage + "%" +
                '\n';
    }
}

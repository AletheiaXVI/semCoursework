package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Represents a row in the CountryLanguage table in the database.
 */
public class PopulationReport {

    private final String name;
    private final long population;
    private final long cityPopulation;
    private final long ruralPopulation;
    private final double cityPercentage;
    private final double ruralPercentage;

    public PopulationReport(ResultSet rs)  {
        try {
            this.name = rs.getString("Name");
            this.population = rs.getLong("Population");
            this.cityPopulation = rs.getLong("CityPopulation");
            this.ruralPopulation = rs.getLong("RuralPopulation");
            this.cityPercentage = (double) cityPopulation / population * 100;
            this.ruralPercentage = (double) ruralPopulation / population * 100;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() { return name; }

    public long getPopulation() { return population; }
    public long getCityPopulation() { return cityPopulation; }
    public long getRuralPopulation() { return ruralPopulation; }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", population=" + population +
                ", city population=" + cityPopulation +
                ", city percentage=" + cityPercentage + "%" +
                ", rural percentage=" + ruralPopulation +
                ", rural percentage=" + ruralPercentage + "%" +
                '\n';
    }
}

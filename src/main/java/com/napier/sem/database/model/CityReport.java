package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CityReport {

    private final String name;

    private final String country;

    private final String district;

    private final int population;

    public CityReport(ResultSet rs) {
        try {
            this.name = rs.getString("Name");
            this.country = rs.getString("Country");
            this.district = rs.getString("District");
            this.population = rs.getInt("Population");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getDistrict() {
        return district;
    }

    public int getPopulation() {
        return population;
    }

    @Override
    public String toString() {
        return "City{" +
                ", name='" + name + '\'' +
                ", countryCode='" + country + '\'' +
                ", district='" + district + '\'' +
                ", population=" + population +
                '}';
    }
}

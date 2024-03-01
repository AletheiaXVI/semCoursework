package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class City {
    private final int id;
    private final String name;
    private final String countryCode;
    private final String district;
    private final int population;

    public City(ResultSet rs){
        try {
            this.id = rs.getInt("ID");
            this.name = rs.getString("Name");
            this.countryCode = rs.getString("CountryCode");
            this.district = rs.getString("District");
            this.population = rs.getInt("Population");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", district='" + district + '\'' +
                ", population=" + population +
                '}';
    }
}

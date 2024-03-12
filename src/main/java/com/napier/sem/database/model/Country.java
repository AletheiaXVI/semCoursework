package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Represents a row in the Country table in the database.
 */
public class Country {
    private final String code;
    private final String name;
    private final Continent continent;
    private final String region;
    private final double surfaceArea;
    private final int indepYear;
    private final int population;
    private final double lifeExpectancy;
    private final double gnp;
    private final double gnpOld;
    private final String localName;
    private final String governmentForm;
    private final String headOfState;
    private final int capital;
    private final String code2;

    public Country(ResultSet rs) {
        try {
            this.code = rs.getString("Code");
            this.name = rs.getString("Name");
            this.continent = Continent.valueOf(rs.getString("Continent").toUpperCase());
            this.region = rs.getString("Region");
            this.surfaceArea = rs.getDouble("SurfaceArea");
            this.indepYear = rs.getInt("IndepYear");
            this.population = rs.getInt("Population");
            this.lifeExpectancy = rs.getDouble("LifeExpectancy");
            this.gnp = rs.getDouble("GNP");
            this.gnpOld = rs.getDouble("GNPOld");
            this.localName = rs.getString("LocalName");
            this.governmentForm = rs.getString("GovernmentForm");
            this.headOfState = rs.getString("HeadOfState");
            this.capital = rs.getInt("Capital");
            this.code2 = rs.getString("Code2");
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

    public Continent getContinent() {
        return continent;
    }

    public String getRegion() {
        return region;
    }

    public double getSurfaceArea() {
        return surfaceArea;
    }

    public int getIndepYear() {
        return indepYear;
    }

    public int getPopulation() {
        return population;
    }

    public double getLifeExpectancy() {
        return lifeExpectancy;
    }

    public double getGnp() {
        return gnp;
    }

    public double getGnpOld() {
        return gnpOld;
    }

    public String getLocalName() {
        return localName;
    }

    public String getGovernmentForm() {
        return governmentForm;
    }

    public String getHeadOfState() {
        return headOfState;
    }

    public int getCapital() {
        return capital;
    }

    public String getCode2() {
        return code2;
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", continent=" + continent +
                ", region='" + region + '\'' +
                ", surfaceArea=" + surfaceArea +
                ", indepYear=" + indepYear +
                ", population=" + population +
                ", lifeExpectancy=" + lifeExpectancy +
                ", gnp=" + gnp +
                ", gnpOld=" + gnpOld +
                ", localName='" + localName + '\'' +
                ", governmentForm='" + governmentForm + '\'' +
                ", headOfState='" + headOfState + '\'' +
                ", capital=" + capital +
                ", code2='" + code2 + '\'' +
                '}';
    }
}


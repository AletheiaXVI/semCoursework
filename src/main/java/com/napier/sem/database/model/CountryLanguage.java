package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryLanguage {
    private final String countryCode;
    private final String language;
    private final boolean isOfficial;
    private final double percentage;

    public CountryLanguage(ResultSet rs) throws SQLException {
        this.countryCode = rs.getString("CountryCode");
        this.language = rs.getString("Language");
        this.isOfficial = rs.getString("IsOfficial").equals("T");
        this.percentage = rs.getDouble("Percentage");
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isOfficial() {
        return isOfficial;
    }

    public double getPercentage() {
        return percentage;
    }
}

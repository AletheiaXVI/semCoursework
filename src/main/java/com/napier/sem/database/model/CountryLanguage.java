package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryLanguage {
    private final String countryCode;
    private final String language;
    private final boolean isOfficial;
    private final double percentage;

    public CountryLanguage(ResultSet rs)  {
        try {
            this.countryCode = rs.getString("CountryCode");
            this.language = rs.getString("Language");
            this.isOfficial = rs.getString("IsOfficial").equals("T");
            this.percentage = rs.getDouble("Percentage");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public String toString() {
        return "CountryLanguage{" +
                "countryCode='" + countryCode + '\'' +
                ", language='" + language + '\'' +
                ", isOfficial=" + isOfficial +
                ", percentage=" + percentage +
                '}';
    }
}

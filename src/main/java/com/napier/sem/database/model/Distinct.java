package com.napier.sem.database.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * a list to store all distinct fields for query use
 */

public class Distinct {
    private final String name;

    public Distinct(ResultSet rs, String columnName) {
        try {
            this.name = rs.getString(columnName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public String getName() {
        return name;
    }

}
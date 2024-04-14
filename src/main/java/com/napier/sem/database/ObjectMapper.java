package com.napier.sem.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Class that maps the result of a query to an object or a list of objects.
 * The connection to the database is provided in the constructor.
 * @author Oskar Budzisz
 */
public class ObjectMapper {

    private final Connection connection;

    /**
     * Creates a new ObjectMapper with the provided connection.
     *
     * @param connection The connection to the database.
     */
    public ObjectMapper(Connection connection) {
        assert connection != null : "Connection cannot be null";
        this.connection = connection;
    }

    /**
     * Executes a query, maps the first row of the result set to an object using the provided mapper function,
     * and returns the mapped object.
     *
     * @param query      The SQL query to execute.
     * @param mapper     A function that maps a ResultSet to an object of type T.
     * @param parameters A list of parameters to bind to the query.
     * @param <T>        The type of the object to map the result to.
     * @return The mapped object, or null if no rows were found or an error occurred.
     */
    public <T> T getObjectFromDatabase(String query, Function<ResultSet, T> mapper, Object... parameters) {
        assert parameters.length == 0 || query.contains("?") : "Number of parameters does not match number of placeholders";
        assert query != null : "Query cannot be null";
        assert mapper != null : "Mapper function cannot be null";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred whilst executing query: " + query);
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Executes a query, maps the result set to a list of objects using the provided mapper function,
     * and returns the list of mapped objects.
     *
     * @param query      The SQL query to execute.
     * @param mapper     A function that maps a ResultSet to an object of type T.
     * @param parameters A list of parameters to bind to the query.
     * @param <T>        The type of the objects to map the result to.
     * @return The list of mapped objects, or an empty list if no rows were found or an error occurred.
     */
    public <T> List<T> getObjectsFromDatabase(String query, Function<ResultSet, T> mapper, Object... parameters) {
        assert parameters.length == 0 || query.contains("?") : "Number of parameters does not match number of placeholders";
        assert query != null : "Query cannot be null";
        assert mapper != null : "Mapper function cannot be null";

        List<T> objects = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    objects.add(mapper.apply(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error occurred whilst executing query: " + query);
            System.out.println(e.getMessage());
        }
        return objects;
    }
}

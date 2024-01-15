package com.jfxbase.oopjfxbase.utils;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CountyBoundaries {

    // Hardcoded boundaries for each county
    public static class Boundary {
        public double minX;
        public double maxX;
        public double minY;
        public double maxY;

        public Boundary(double minX, double maxX, double minY, double maxY) {
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
    }

    // Map to store county boundaries
    private static final Map<String, Boundary> countyBoundaries = loadCountyBoundariesFromDatabase();

    // Method to load county boundaries from the database
    private static Map<String, Boundary> loadCountyBoundariesFromDatabase() {
        Map<String, Boundary> boundaries = new HashMap<>();

        String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            String sql = "SELECT county_name, min_x, max_x, min_y, max_y FROM country_boundaries";

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String countyName = resultSet.getString("county_name");
                    double minX = resultSet.getDouble("min_x");
                    double maxX = resultSet.getDouble("max_x");
                    double minY = resultSet.getDouble("min_y");
                    double maxY = resultSet.getDouble("max_y");

                    Boundary boundary = new Boundary(minX, maxX, minY, maxY);
                    boundaries.put(countyName, boundary);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return boundaries;
    }

    // Method to get the boundary for a specific county
    public static Boundary getBoundary(String countyName) {
        return countyBoundaries.get(countyName);
    }

}

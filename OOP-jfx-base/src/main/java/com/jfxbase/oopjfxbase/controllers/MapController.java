/*
 * MapController class controls the interaction and functionality of the map view
 * in the OOP-jfx-base application. It handles map clicks, updates to county data,
 * and UI initialization based on the data retrieved from the database.
 * This class extends SceneController, providing methods for scene management.
 */

package com.jfxbase.oopjfxbase.controllers;

import javafx.scene.control.*;
import com.jfxbase.oopjfxbase.models.County;
import com.jfxbase.oopjfxbase.utils.SceneController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import com.jfxbase.oopjfxbase.JFXBaseApplication;
import com.jfxbase.oopjfxbase.utils.CountyBoundaries;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.ProgressBar;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

/*
 * The MapController class is responsible for handling user interactions with the
 * map view, updating county data in the database, and initializing UI elements
 * based on the retrieved county information. It extends SceneController for
 * scene management capabilities.
 */
public class MapController extends SceneController {

    @FXML
    private ImageView romaniaMap;

    @FXML
    private Label countyLabel;

    @FXML
    private Button resetButton;

    @FXML
    private ComboBox<String> countyDropdown;

    @FXML
    private ComboBox<String> ethnicityDropdown;

    @FXML
    private TextField populationTextField;

    private Map<String, County> countyData;
    private Map<String, County> initialCountyData;

    /*
     * Initialize method is called when the FXML file is loaded.
     * It sets up the initial data, event handlers, and UI elements.
     */
    public void initialize() {
        initialCountyData = loadCountyDataFromDatabase();
        countyData = new HashMap<>(initialCountyData);

        romaniaMap.setOnMouseClicked(this::handleMapClick);

        resetButton.setOnAction(event -> resetToDefault());

        initializeDropdowns();

        printCountyNames();
    }

    /*
     * Initializes the dropdown menus with county and ethnicity options.
     */
    private void initializeDropdowns() {
        countyDropdown.getItems().addAll(initialCountyData.keySet());
        ethnicityDropdown.getItems().addAll("ro", "ma", "romi", "ucr", "ger", "tu", "ru", "sb", "slo", "bu", "gr", "ev", "ce", "pol", "arm", "other", "unavailable");
    }

    /*
     * Prints the names of counties loaded from the database to the console.
     */
    private void printCountyNames() {
        System.out.println("County Names from Database:");
        for (String countyName : countyData.keySet()) {
            System.out.println(countyName);
        }
    }

    /*
     * Handles map clicks by determining the clicked county and updating the UI.
     */
    private void handleMapClick(MouseEvent event) {
        double clickedX = event.getX();
        double clickedY = event.getY();

        System.out.println("Clicked at: X = " + clickedX + ", Y = " + clickedY);

        String clickedCounty = determineClickedCounty(clickedX, clickedY);
        System.out.println("Clicked County: " + clickedCounty);

        displayEthnicData(clickedCounty);
    }

    @FXML
    private HBox ethnicitiesBox;

    /*
     * Displays ethnicity data for the selected county.
     */
    private void displayEthnicData(String countyName) {
        if (countyData.containsKey(countyName)) {
            County county = countyData.get(countyName);
            int[] ethnicityPopulation = county.getEthnicityPopulation();

            if (countyLabel != null) {
                countyLabel.setText("Ethnic Makeup for " + county.getName() + ":\n" +
                        "Total Population: " + county.getTotalPopulation() + "\n");
            }

            GridPane ethnicitiesGrid = (GridPane) romaniaMap.getParent().lookup("#ethnicitiesGrid");
            ethnicitiesGrid.getChildren().clear();

            for (int i = 0; i < ethnicityPopulation.length; i++) {
                double percentage = ethnicityPopulation[i] * 100.0 / county.getTotalPopulation();

                Label ethnicityLabel = new Label(getEthnicityName(i) + " " + String.format("%.2f%%", percentage));

                // Add the ethnicity label to the GridPane
                ethnicitiesGrid.add(ethnicityLabel, i % 2, i / 2);
            }
        }
    }

    /*
     * Updates population data in the database based on user input.
     */
    private Map<String, County> updatePopulationInDatabase(String countyName, String ethnicityName, int newPopulation) {
        // Database connection parameters
        String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            // SQL queries for updating population data
            String ethnicityUpdateSql = "UPDATE counties SET " + ethnicityName + " = ? WHERE name = ?";
            String totalPopulationUpdateSql = "UPDATE counties SET total_population = ? WHERE name = ?";

            try (PreparedStatement totalPopulationStatement = connection.prepareStatement(totalPopulationUpdateSql)) {
                // Calculate the new total population based on the changes to the specific ethnicity
                int newTotalPopulation = calculateNewTotalPopulation(countyName, ethnicityName, newPopulation);
                totalPopulationStatement.setInt(1, newTotalPopulation);
                totalPopulationStatement.setString(2, countyName);
                totalPopulationStatement.executeUpdate();
            }

            try (PreparedStatement ethnicityStatement = connection.prepareStatement(ethnicityUpdateSql)) {
                ethnicityStatement.setInt(1, newPopulation);
                ethnicityStatement.setString(2, countyName);
                ethnicityStatement.executeUpdate();
            }

            // Reload the data from the database and return it
            return loadCountyDataFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * Calculates the new total population based on the changes to the specific ethnicity.
     */
    private int calculateNewTotalPopulation(String countyName, String ethnicityName, int newPopulation) {
        // Fetch the current population data from the database
        Map<String, County> countyData = loadCountyDataFromDatabase();

        if (countyData.containsKey(countyName)) {
            County county = countyData.get(countyName);
            int[] ethnicityPopulation = county.getEthnicityPopulation();

            // Find the index of the ethnicity in the array
            int ethnicityIndex = getEthnicityIndex(ethnicityName);

            // Get the old value of the ethnicity population
            int oldEthnicityPopulation = ethnicityPopulation[ethnicityIndex];

            // Calculate the difference between the old and new values
            int populationDifference = newPopulation - oldEthnicityPopulation;

            // Calculate the new total population based on the difference
            int newTotalPopulation = county.getTotalPopulation() + populationDifference;

            return newTotalPopulation;
        }

        return 0; // Default value if county not found
    }

    /*
     * Gets the index of the given ethnicity name in the array.
     */
    private int getEthnicityIndex(String ethnicityName) {
        String[] ethnicityNames = {"ro", "ma", "romi", "ucr", "ger", "tu", "ru", "sb", "slo", "bu", "gr", "ev", "ce", "pol", "arm", "other", "unavailable"};

        for (int i = 0; i < ethnicityNames.length; i++) {
            if (ethnicityNames[i].equals(ethnicityName)) {
                return i;
            }
        }

        return -1; // Default value if ethnicity not found
    }

    /*
     * Gets the ethnicity name based on the given index in the array.
     */
    private String getEthnicityName(int index) {
        String[] ethnicityNames = {"ro", "ma", "romi", "ucr", "ger", "tu", "ru", "sb", "slo", "bu", "gr", "ev", "ce", "pol", "arm", "other", "unavailable"};

        if (index >= 0 && index < ethnicityNames.length) {
            return ethnicityNames[index];
        } else {
            return "Unknown Ethnicity";
        }
    }

    /*
     * Resets the database values to their initial values.
     */
    @FXML
    private void resetToDefault() {
        // Reset the database values to initial values
        resetDatabaseToInitialValues();

        // Update the UI with the new database values
        countyData = loadCountyDataFromDatabase();

        // Display ethnic data for each county
        for (String countyName : countyData.keySet()) {
            displayEthnicData(countyName);
        }
    }

    /*
     * Resets the database values to their initial values.
     */
    private void resetDatabaseToInitialValues() {
        // Database connection parameters
        String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            for (Map.Entry<String, County> entry : initialCountyData.entrySet()) {
                String countyName = entry.getKey();
                County initialCounty = entry.getValue();
                int[] initialEthnicityPopulation = initialCounty.getEthnicityPopulation();

                // Calculate the initial total population
                int initialTotalPopulation = initialCounty.getTotalPopulation();

                // SQL query for updating all ethnicity and total population values for a county
                String sql = "UPDATE counties SET ro = ?, ma = ?, romi = ?, ucr = ?, ger = ?, tu = ?, ru = ?, sb = ?, " +
                        "slo = ?, bu = ?, gr = ?, ev = ?, ce = ?, pol = ?, arm = ?, other = ?, unavailable = ?, total_population = ? " +
                        "WHERE name = ?";

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    // Set parameters for each ethnicity
                    for (int i = 0; i < initialEthnicityPopulation.length; i++) {
                        statement.setInt(i + 1, initialEthnicityPopulation[i]);
                    }
                    // Set the total population parameter
                    statement.setInt(initialEthnicityPopulation.length + 1, initialTotalPopulation);
                    // Set the county name parameter
                    statement.setString(initialEthnicityPopulation.length + 2, countyName);

                    // Execute the update
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Loads county data from the database and returns a map of counties.
     */
    private Map<String, County> loadCountyDataFromDatabase() {
        // Initialize a map to store county data
        Map<String, County> countyData = new HashMap<>();

        // Database connection parameters
        String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "password";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            // SQL query to select county data
            String sql = "SELECT name, total_population, ro, ma, romi, ucr, ger, tu, ru, sb, slo, bu, gr, ev, ce, pol, arm, other, unavailable FROM counties";

            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {

                // Iterate over the result set and create County objects
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    int totalPopulation = resultSet.getInt("total_population");
                    int[] ethnicityPopulation = {
                            resultSet.getInt("ro"),
                            resultSet.getInt("ma"),
                            resultSet.getInt("romi"),
                            resultSet.getInt("ucr"),
                            resultSet.getInt("ger"),
                            resultSet.getInt("tu"),
                            resultSet.getInt("ru"),
                            resultSet.getInt("sb"),
                            resultSet.getInt("slo"),
                            resultSet.getInt("bu"),
                            resultSet.getInt("gr"),
                            resultSet.getInt("ev"),
                            resultSet.getInt("ce"),
                            resultSet.getInt("pol"),
                            resultSet.getInt("arm"),
                            resultSet.getInt("other"),
                            resultSet.getInt("unavailable")
                    };

                    // Create a County object and add it to the map
                    County county = new County(name, totalPopulation, ethnicityPopulation);
                    countyData.put(name, county);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return countyData;
    }

    /*
     * Determines the county that was clicked based on the provided coordinates.
     * Uses the county boundaries and checks if the click is within those boundaries.
     */
    private String determineClickedCounty(double x, double y) {
        // Iterate over the loaded county data
        for (Map.Entry<String, County> entry : loadCountyDataFromDatabase().entrySet()) {
            String countyName = entry.getKey();
            CountyBoundaries.Boundary boundary = CountyBoundaries.getBoundary(countyName);

            // Check if the click is within the boundaries of the current county
            if (boundary != null && isWithinBounds(x, y, boundary)) {
                return countyName;
            }
        }

        // Default case if no county is clicked
        return "Unknown County";
    }

    /*
     * Checks if the provided coordinates are within the specified county boundaries.
     */
// Modify the isWithinBounds method to accept Boundary object
    private boolean isWithinBounds(double x, double y, CountyBoundaries.Boundary boundary) {
        return x >= boundary.minX && x <= boundary.maxX && y >= boundary.minY && y <= boundary.maxY;
    }

    /*
     * Handles the button click event for updating population data in the UI and database.
     * Validates user input and displays error messages for invalid input.
     */
    @FXML
    private void handleUpdateButtonClick() {
        // Get user input
        String selectedCounty = countyDropdown.getValue();
        String selectedEthnicity = ethnicityDropdown.getValue();
        String populationText = populationTextField.getText();

        // Validate input
        if (selectedCounty == null || selectedEthnicity == null || populationText.isEmpty()) {
            // Handle invalid input
            return;
        }

        try {
            // Parse population input as an integer
            int newPopulation = Integer.parseInt(populationText);

            // Validate numeric input range
            if (newPopulation <= 0 || newPopulation >= 1000000) {
                // Display error message for improper input
                showErrorDialog("Improper input", "Population must be a positive integer under 1 million.");
                return;
            }

            // Call a method to update the database
            updatePopulationInDatabase(selectedCounty, selectedEthnicity, newPopulation);

            // Reload the data and update the UI
            countyData = loadCountyDataFromDatabase();
            displayEthnicData(selectedCounty);
        } catch (NumberFormatException e) {
            // Handle invalid number format
            showErrorDialog("Invalid input", "Please enter a valid integer for population.");
            e.printStackTrace();
        }
    }

    /*
     * Displays an error dialog with the given title and content.
     */
    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

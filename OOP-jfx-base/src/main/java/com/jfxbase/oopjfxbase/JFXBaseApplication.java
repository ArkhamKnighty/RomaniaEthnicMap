// com.jfxbase.oopjfxbase.JFXBaseApplication

package com.jfxbase.oopjfxbase;

import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import com.jfxbase.oopjfxbase.utils.ApplicationHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class JFXBaseApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ApplicationHandler.getInstance().setPrimaryStage(primaryStage);
        ApplicationHandler.getInstance().loadScene(SCENE_IDENTIFIER.MAP_VIEW);

        //importDataFromExcelToDatabase();

        primaryStage.setTitle("JFXBase Application");
        primaryStage.show();
    }


    /*private void importDataFromExcelToDatabase() {
        String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "password";

        String excelFilePath = "Tabel-2.01.xls";
        File file = new File(excelFilePath);

        int batchSize = 20;

        Connection connection = null;

        try {
            long start = System.currentTimeMillis();

            FileInputStream inputStream = new FileInputStream(excelFilePath);
            Workbook workbook = new HSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);
            Row nextRow, countyRow, popRow;
            String countyName;
            int totalPopulation;
            int[] ethnicityPopulation;

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            int currentCountyLine = 16; // Start from row 16 (one row before the current one)
            int currentColumn = 0; // Read the name from column 0 (one column before the current one)

            // Prepare the SQL statement for inserting data into the database
            String sql = "INSERT INTO counties (name, total_population, ro, ma, romi, ucr, ger, tu, ru, sb, slo, bu, gr, ev, ce, pol, arm, other, unavailable) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            // Iterate until the end of the file
            while (currentCountyLine < 436) {
                nextRow = sheet.getRow(currentCountyLine);
                countyRow = sheet.getRow(currentCountyLine);

                // Read the county name
                countyName = countyRow.getCell(currentColumn).getStringCellValue();
                popRow = sheet.getRow(currentCountyLine + 8);

                // Read the total population
                Cell totalPopulationCell = popRow.getCell(2);
                totalPopulation = getNumericValue(totalPopulationCell);

                // Read the ethnicity population data
                ethnicityPopulation = new int[22];
                for (int i = 3; i <= 20; i++) {
                    Cell ethnicityCell = popRow.getCell(i);
                    ethnicityPopulation[i - 3] = getNumericValue(ethnicityCell);
                }

                // Set parameters for the PreparedStatement
                statement.setString(1, countyName);
                statement.setInt(2, totalPopulation);

                // Set parameters for ethnicityPopulation array (assuming 23 ethnicity columns)
                for (int i = 0; i < 17; i++) {
                    statement.setInt(3 + i, ethnicityPopulation[i]);
                }

                // Execute the insert statement
                statement.executeUpdate();

                // Advance to the next county
                currentCountyLine += 10;
                currentColumn = 0;
            }

            workbook.close();

            // Commit the transaction and close resources
            connection.commit();
            statement.close();
            connection.close();

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static int getNumericValue(Cell cell) {
        if (cell == null) {
            return 0;
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                String stringValue = cell.getStringCellValue();
                if ("*".equals(stringValue) || "-".equals(stringValue)) {
                    return 0;
                }
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return 0;
                }
            default:
                return 0;
        }
    }*/
}

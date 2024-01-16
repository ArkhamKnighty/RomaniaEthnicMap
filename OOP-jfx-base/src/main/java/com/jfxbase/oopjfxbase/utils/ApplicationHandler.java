/*
 * ApplicationHandler class manages the initialization, loading, and changing of scenes in a JavaFX application.
 * It is responsible for handling the main stage, loading views, and managing scenes based on scene identifiers.
 * The class follows the Singleton pattern to ensure only one instance is created.
 */

package com.jfxbase.oopjfxbase.utils;

import com.jfxbase.oopjfxbase.JFXBaseApplication;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class ApplicationHandler {
    // HashMap to store loaded views based on scene identifiers
    private final HashMap<SCENE_IDENTIFIER, Pane> views = new HashMap<>();
    private Stage stage;

    // Private constructor to enforce Singleton pattern
    private ApplicationHandler() {}

    // Method to start the application and initialize views
    public void startApplication(Stage stage){
        this.initializeViews();

        this.stage = stage;
        this.stage.setTitle(Environment.APP_TITLE);
        this.stage.setFullScreen(Environment.IS_FULLSCREEN);
        this.stage.setScene(new Scene(this.views.get(SCENE_IDENTIFIER.HELLO), 800, 800));
        this.stage.show();

        Logger.info("Application started..");
    }

    // Method to set the primary stage
    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    // Method to load a view based on the provided scene identifier
    private void loadView(SCENE_IDENTIFIER sceneIdentifier) {
        try {
            String resourcePath = sceneIdentifier.label;
            System.out.println("Resource path: " + resourcePath); // Print the resource path
            URL resourceUrl = Objects.requireNonNull(JFXBaseApplication.class.getResource(resourcePath));
            Logger.info("Loading view from resource: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Pane root = loader.load();
            views.put(sceneIdentifier, root);
        } catch (IOException | NullPointerException exception) {
            exception.printStackTrace(); // Print the stack trace
            Logger.error("Could not initialize views. Please check the resources folder.", exception);
            closeApplication();
        }
    }

    // Method to load a scene based on the provided scene identifier
    public void loadScene(SCENE_IDENTIFIER sceneIdentifier) {
        if (!views.containsKey(sceneIdentifier)) {
            loadView(sceneIdentifier);
        }

        Pane root = views.get(sceneIdentifier);

        if (root != null && stage != null) {
            Scene scene = stage.getScene();

            if (scene == null) {
                scene = new Scene(root, 800, 800);
                stage.setScene(scene);
            } else if (scene.getRoot() != root) {
                scene.setRoot(root);
            }
        } else {
            // Handle the case where either root or stage is null
            System.err.println("Error: Root or Stage is null");
        }
    }

    // Method to change the scene based on the provided scene identifier
    public void changeScene(SCENE_IDENTIFIER newScene) {
        this.stage.getScene().setRoot(views.get(newScene));
    }

    // Method to close the application gracefully
    public void closeApplication(){
        Platform.exit();
        System.exit(0);
    }

    // Method to initialize views by loading FXML files for all scene identifiers
    private void initializeViews() {
        try {
            for (SCENE_IDENTIFIER value : SCENE_IDENTIFIER.values()) {
                this.views.put(value, FXMLLoader.load(Objects.requireNonNull(JFXBaseApplication.class.getResource("/" + value.label))));
            }
        } catch (IOException | NullPointerException exception) {
            Logger.error("Could not initialize views. Please check the resources folder.");
            this.closeApplication();
        }
    }

    // Singleton pattern implementation
    public static ApplicationHandler _instance = null;

    public static ApplicationHandler getInstance() {
        if(ApplicationHandler._instance == null){
            ApplicationHandler._instance = new ApplicationHandler();
        }

        return ApplicationHandler._instance;
    }
}

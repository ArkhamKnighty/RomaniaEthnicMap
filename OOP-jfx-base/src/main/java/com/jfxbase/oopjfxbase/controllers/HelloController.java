package com.jfxbase.oopjfxbase.controllers;

import com.jfxbase.oopjfxbase.utils.SceneController;
import com.jfxbase.oopjfxbase.utils.enums.SCENE_IDENTIFIER;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class HelloController extends SceneController {
    @FXML
    private TextField nameField;

    @FXML
    private Text helloPrompt;

    @FXML
    protected void onHelloButtonClick() {
        helloPrompt.setText(String.format("Hello %s", nameField.getText()));
    }

    @FXML
    protected void onGoToAnotherSceneClick(){
        this.changeScene(SCENE_IDENTIFIER.GOOD_BYE);
    }


    @FXML
    public void onGoToMapViewClick(){
        this.changeScene(SCENE_IDENTIFIER.MAP_VIEW);
    }
}
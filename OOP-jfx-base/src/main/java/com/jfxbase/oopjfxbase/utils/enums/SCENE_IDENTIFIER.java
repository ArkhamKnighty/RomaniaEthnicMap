package com.jfxbase.oopjfxbase.utils.enums;

public enum SCENE_IDENTIFIER {
    HELLO("/com/jfxbase/oopjfxbase/hello-view.fxml"),
    GOOD_BYE("/com/jfxbase/oopjfxbase/good-bye-view.fxml"),
    MAP_VIEW("/com/jfxbase/oopjfxbase/map-view.fxml");

    public final String label;

    SCENE_IDENTIFIER(String label) {
        this.label = label;
    }
}

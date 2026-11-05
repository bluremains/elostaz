package com.elostaz.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class Header {

    private Header() {
    }

    public static HBox create(String welcome) {
        Label logo = Ui.label("ELOSTAZ", "header-logo");
        Label text = Ui.label(welcome, "header-text");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button logout = Ui.button("Log out", "header-button");
        logout.setOnAction(e -> Navigator.toLogin());
        HBox bar = new HBox(16, logo, spacer, text, logout);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("header-bar");
        return bar;
    }
}

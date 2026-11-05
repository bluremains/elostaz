package com.elostaz.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Navigator.init(stage);
        Navigator.toLogin();
    }
}

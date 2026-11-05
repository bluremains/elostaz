package com.elostaz.ui;

import com.elostaz.model.Role;
import com.elostaz.util.Session;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigator {

    private static Stage stage;

    private Navigator() {
    }

    public static void init(Stage s) {
        stage = s;
        stage.setTitle("ELOSTAZ - Private Tutor Platform");
        stage.setMinWidth(1000);
        stage.setMinHeight(660);
    }

    public static void show(Parent root) {
        if (stage.getScene() == null) {
            Scene scene = new Scene(root, 1140, 740);
            scene.getStylesheets().add(Ui.css());
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
        if (!stage.isShowing()) {
            stage.show();
        }
    }

    public static void toLogin() {
        Session.end();
        show(new LoginView().build());
    }

    public static void toRegister() {
        show(new RegisterView().build());
    }

    public static void toHome() {
        if (Session.getUser().getRole() == Role.TUTOR) {
            show(new TutorView().build());
        } else {
            show(new ParentView().build());
        }
    }
}

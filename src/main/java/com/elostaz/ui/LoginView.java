package com.elostaz.ui;

import com.elostaz.service.AuthService;
import com.elostaz.service.ServiceException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {

    private final AuthService authService = new AuthService();

    public Parent build() {
        VBox brand = new VBox(18);
        brand.getStyleClass().add("brand-panel");
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPrefWidth(460);
        brand.setMinWidth(380);
        brand.getChildren().addAll(
                Ui.label("ELOSTAZ", "brand-logo"),
                Ui.label("Find the right private tutor and book a lesson in minutes.", "brand-tagline"),
                point("Search by subject, grade, area and price"),
                point("See each tutor's free times"),
                point("Book and track your lessons"));
        ((Label) brand.getChildren().get(1)).setWrapText(true);

        TextField username = new TextField();
        username.setPromptText("Your username");
        PasswordField password = new PasswordField();
        password.setPromptText("Your password");
        Label error = Ui.label("", "error-text");
        error.setWrapText(true);

        Button login = Ui.button("Log in", "primary-button");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setDefaultButton(true);
        login.setOnAction(e -> {
            try {
                authService.login(username.getText(), password.getText());
                Navigator.toHome();
            } catch (ServiceException ex) {
                error.setText(ex.getMessage());
            }
        });

        Button register = Ui.button("New here? Create an account", "link-button");
        register.setOnAction(e -> Navigator.toRegister());

        VBox card = new VBox(16,
                Ui.label("Welcome back", "title"),
                Ui.label("Log in to continue", "subtitle"),
                Ui.field("USERNAME", username),
                Ui.field("PASSWORD", password),
                error,
                login,
                register);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(420);
        card.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane right = new StackPane(card);
        right.setPadding(new Insets(30));
        HBox.setHgrow(right, Priority.ALWAYS);

        return new HBox(brand, right);
    }

    private HBox point(String text) {
        Label dot = new Label();
        dot.getStyleClass().add("brand-dot");
        HBox row = new HBox(12, dot, Ui.label(text, "brand-point"));
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }
}

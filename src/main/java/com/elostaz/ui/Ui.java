package com.elostaz.ui;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class Ui {

    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    public static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH);

    private Ui() {
    }

    public static String css() {
        return Ui.class.getResource("/styles.css").toExternalForm();
    }

    public static void info(String message) {
        show(Alert.AlertType.INFORMATION, "Done", message);
    }

    public static void error(String message) {
        show(Alert.AlertType.ERROR, "Something went wrong", message);
    }

    public static boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle("Please confirm");
        alert.setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(css());
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(css());
        alert.showAndWait();
    }

    public static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    public static Button button(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    public static Label chip(String text, String extraClass) {
        Label label = new Label(text);
        label.getStyleClass().addAll("chip", extraClass);
        return label;
    }

    public static VBox field(String caption, Node input) {
        VBox box = new VBox(5, label(caption, "field-caption"), input);
        return box;
    }

    public static <T> ComboBox<T> combo(String prompt, List<T> items, Function<T, String> text) {
        ComboBox<T> box = new ComboBox<>(FXCollections.observableArrayList(items));
        box.setPromptText(prompt);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setConverter(new StringConverter<T>() {
            @Override
            public String toString(T item) {
                return item == null ? "" : text.apply(item);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
        return box;
    }

    public static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }
}

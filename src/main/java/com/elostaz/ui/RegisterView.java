package com.elostaz.ui;

import com.elostaz.model.LessonType;
import com.elostaz.model.Teacher;
import com.elostaz.service.AuthService;
import com.elostaz.service.ServiceException;
import com.elostaz.util.Validator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class RegisterView {

    private final AuthService authService = new AuthService();

    public Parent build() {
        ToggleGroup group = new ToggleGroup();
        ToggleButton parentToggle = new ToggleButton("I am a parent");
        parentToggle.getStyleClass().add("toggle-left");
        ToggleButton tutorToggle = new ToggleButton("I am a tutor");
        tutorToggle.getStyleClass().add("toggle-right");
        parentToggle.setToggleGroup(group);
        tutorToggle.setToggleGroup(group);
        parentToggle.setSelected(true);
        group.selectedToggleProperty().addListener((obs, old, now) -> {
            if (now == null) {
                old.setSelected(true);
            }
        });
        HBox toggles = new HBox(parentToggle, tutorToggle);

        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        TextField phone = new TextField();
        phone.setPromptText("01012345678");
        TextField username = new TextField();
        username.setPromptText("Choose a username");
        PasswordField password = new PasswordField();
        password.setPromptText("At least 6 characters");

        TextField email = new TextField();
        email.setPromptText("name@example.com (optional)");
        TextField price = new TextField();
        price.setPromptText("Price per lesson (EGP)");
        ComboBox<LessonType> lessonType = Ui.combo("Choose lesson type", Arrays.asList(LessonType.values()), t -> Ui.capitalize(t.name()));
        TextField experience = new TextField();
        experience.setPromptText("Years of experience");
        TextArea bio = new TextArea();
        bio.setPromptText("Tell parents a little about yourself");
        bio.setPrefRowCount(3);
        bio.setWrapText(true);

        VBox tutorFields = new VBox(14,
                Ui.field("EMAIL", email),
                Ui.field("PRICE PER LESSON (EGP)", price),
                Ui.field("LESSON TYPE", lessonType),
                Ui.field("YEARS OF EXPERIENCE", experience),
                Ui.field("ABOUT YOU", bio));
        tutorFields.visibleProperty().bind(tutorToggle.selectedProperty());
        tutorFields.managedProperty().bind(tutorToggle.selectedProperty());

        Label error = Ui.label("", "error-text");
        error.setWrapText(true);

        Button create = Ui.button("Create account", "primary-button");
        create.setMaxWidth(Double.MAX_VALUE);
        create.setOnAction(e -> {
            error.setText("");
            try {
                if (tutorToggle.isSelected()) {
                    if (!Validator.isNonNegativeNumber(price.getText())) {
                        throw new ServiceException("Please enter a valid price.");
                    }
                    if (!Validator.isNonNegativeInteger(experience.getText())) {
                        throw new ServiceException("Please enter experience as a whole number of years.");
                    }
                    Teacher teacher = new Teacher();
                    teacher.setFullName(fullName.getText().trim());
                    teacher.setPhone(phone.getText().trim());
                    teacher.setEmail(email.getText().trim().isEmpty() ? null : email.getText().trim());
                    teacher.setPricePerLesson(Double.parseDouble(price.getText().trim()));
                    teacher.setLessonType(lessonType.getValue());
                    teacher.setExperienceYears(Integer.parseInt(experience.getText().trim()));
                    teacher.setBio(bio.getText().trim());
                    authService.registerTutor(username.getText(), password.getText(), teacher);
                } else {
                    authService.registerParent(username.getText(), password.getText(), fullName.getText(), phone.getText());
                }
                Ui.info("Your account is ready. You can log in now.");
                Navigator.toLogin();
            } catch (ServiceException ex) {
                error.setText(ex.getMessage());
            }
        });

        Button back = Ui.button("Back to log in", "link-button");
        back.setOnAction(e -> Navigator.toLogin());

        VBox card = new VBox(14,
                Ui.label("Create your account", "title"),
                Ui.label("Join ELOSTAZ as a parent or a tutor", "subtitle"),
                toggles,
                Ui.field("FULL NAME", fullName),
                Ui.field("PHONE", phone),
                Ui.field("USERNAME", username),
                Ui.field("PASSWORD", password),
                tutorFields,
                error,
                create,
                back);
        card.getStyleClass().add("form-card");
        card.setMaxWidth(480);
        card.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
        card.setAlignment(Pos.CENTER_LEFT);

        StackPane holder = new StackPane(card);
        holder.setPadding(new Insets(30));
        ScrollPane scroll = new ScrollPane(holder);
        scroll.setFitToWidth(true);
        return scroll;
    }
}

package com.elostaz.ui;

import com.elostaz.model.Subject;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherSubject;
import com.elostaz.service.BookingService;
import com.elostaz.service.ServiceException;
import com.elostaz.service.TeacherService;
import com.elostaz.util.Session;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookDialog {

    private final BookingService bookingService = new BookingService();
    private final TeacherService teacherService = new TeacherService();

    public boolean show(Teacher teacher) {
        List<Subject> subjects = new ArrayList<>();
        Set<DayOfWeek> days;
        try {
            Set<Integer> seen = new HashSet<>();
            for (TeacherSubject ts : teacherService.subjects(teacher.getTeacherId())) {
                if (seen.add(ts.getSubjectId())) {
                    subjects.add(new Subject(ts.getSubjectId(), ts.getSubjectName()));
                }
            }
            days = teacherService.availability(teacher.getTeacherId()).stream()
                    .map(a -> a.getDayOfWeek()).collect(Collectors.toSet());
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
            return false;
        }
        if (subjects.isEmpty() || days.isEmpty()) {
            Ui.info("This tutor has not added subjects or free times yet. Please try another tutor.");
            return false;
        }

        ComboBox<Subject> subject = Ui.combo("Choose a subject", subjects, Subject::getSubjectName);
        DatePicker date = new DatePicker();
        date.setPromptText("Choose a date");
        date.setEditable(false);
        date.setMaxWidth(Double.MAX_VALUE);
        Set<DayOfWeek> freeDays = days;
        date.setDayCellFactory(p -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(empty || item.isBefore(LocalDate.now()) || !freeDays.contains(item.getDayOfWeek()));
            }
        });
        ComboBox<LocalTime> time = Ui.combo("Choose a time", new ArrayList<>(), t -> t.format(Ui.TIME) + " - " + t.plusHours(1).format(Ui.TIME));
        Label error = Ui.label("", "error-text");
        error.setWrapText(true);

        date.valueProperty().addListener((obs, old, now) -> {
            time.getItems().clear();
            if (now != null) {
                try {
                    time.getItems().addAll(bookingService.freeSlots(teacher.getTeacherId(), now));
                    if (time.getItems().isEmpty()) {
                        error.setText("No free times on this day. Please choose another date.");
                    } else {
                        error.setText("");
                    }
                } catch (ServiceException e) {
                    error.setText(e.getMessage());
                }
            }
        });

        VBox content = new VBox(14,
                Ui.label(teacher.getFullName(), "section-title"),
                Ui.label(String.format("%.0f EGP per lesson (1 hour)", teacher.getPricePerLesson()), "subtitle"),
                Ui.field("SUBJECT", subject),
                Ui.field("DATE", date),
                Ui.field("TIME", time),
                error);
        content.setPadding(new Insets(10, 10, 0, 10));
        content.setPrefWidth(380);

        ButtonType bookType = new ButtonType("Book lesson", ButtonBar.ButtonData.OK_DONE);
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Book a lesson");
        dialog.getDialogPane().getStylesheets().add(Ui.css());
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(bookType, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(bookType).getStyleClass().add("primary-button");
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).getStyleClass().add("secondary-button");

        boolean[] booked = {false};
        dialog.getDialogPane().lookupButton(bookType).addEventFilter(ActionEvent.ACTION, event -> {
            Subject chosen = subject.getValue();
            try {
                bookingService.book(Session.getParent().getParentId(), teacher.getTeacherId(),
                        chosen == null ? 0 : chosen.getSubjectId(), date.getValue(), time.getValue());
                booked[0] = true;
            } catch (ServiceException e) {
                error.setText(e.getMessage());
                event.consume();
            }
        });

        dialog.showAndWait();
        return booked[0];
    }
}

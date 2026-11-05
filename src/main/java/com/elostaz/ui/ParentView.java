package com.elostaz.ui;

import com.elostaz.model.Area;
import com.elostaz.model.BookingStatus;
import com.elostaz.model.BookingView;
import com.elostaz.model.GradeLevel;
import com.elostaz.model.LessonType;
import com.elostaz.model.Subject;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherResult;
import com.elostaz.service.BookingService;
import com.elostaz.service.CatalogService;
import com.elostaz.service.ServiceException;
import com.elostaz.util.Session;
import com.elostaz.util.Validator;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class ParentView {

    private final CatalogService catalogService = new CatalogService();
    private final BookingService bookingService = new BookingService();

    private ComboBox<Subject> subjectBox;
    private ComboBox<GradeLevel> gradeBox;
    private ComboBox<Area> areaBox;
    private ComboBox<LessonType> typeBox;
    private TextField priceField;
    private VBox results;
    private Label countLabel;
    private TableView<BookingView> bookingTable;

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setTop(Header.create("Hello, " + Session.getParent().getFullName()));

        Tab find = new Tab("Find a tutor", buildFindTab());
        Tab mine = new Tab("My bookings", buildBookingsTab());
        find.setClosable(false);
        mine.setClosable(false);
        TabPane tabs = new TabPane(find, mine);
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
            if (now == mine) {
                loadBookings();
            }
        });
        root.setCenter(tabs);

        search();
        return root;
    }

    private Parent buildFindTab() {
        try {
            subjectBox = Ui.combo("Any subject", catalogService.subjects(), Subject::getSubjectName);
            gradeBox = Ui.combo("Any grade", catalogService.grades(), GradeLevel::getGradeName);
            areaBox = Ui.combo("Any area", catalogService.areas(), Area::getAreaName);
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
            subjectBox = Ui.combo("Any subject", List.<Subject>of(), Subject::getSubjectName);
            gradeBox = Ui.combo("Any grade", List.<GradeLevel>of(), GradeLevel::getGradeName);
            areaBox = Ui.combo("Any area", List.<Area>of(), Area::getAreaName);
        }
        typeBox = Ui.combo("Any type", Arrays.asList(LessonType.values()), t -> Ui.capitalize(t.name()));
        priceField = new TextField();
        priceField.setPromptText("e.g. 200");

        Button searchButton = Ui.button("Search", "primary-button");
        searchButton.setOnAction(e -> search());
        Button resetButton = Ui.button("Reset", "secondary-button");
        resetButton.setOnAction(e -> {
            subjectBox.setValue(null);
            gradeBox.setValue(null);
            areaBox.setValue(null);
            typeBox.setValue(null);
            priceField.clear();
            search();
        });

        HBox filters = new HBox(12,
                grow(Ui.field("SUBJECT", subjectBox)),
                grow(Ui.field("GRADE", gradeBox)),
                grow(Ui.field("AREA", areaBox)),
                grow(Ui.field("LESSON TYPE", typeBox)),
                grow(Ui.field("MAX PRICE (EGP)", priceField)),
                searchButton,
                resetButton);
        filters.setAlignment(Pos.BOTTOM_LEFT);
        filters.getStyleClass().add("card");

        countLabel = Ui.label("", "section-title");
        results = new VBox(14);
        ScrollPane scroll = new ScrollPane(results);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox box = new VBox(16, filters, countLabel, scroll);
        box.setPadding(new Insets(18, 28, 24, 28));
        return box;
    }

    private VBox grow(VBox box) {
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMinWidth(110);
        return box;
    }

    private void search() {
        results.getChildren().clear();
        Double maxPrice = null;
        if (Validator.isNotBlank(priceField.getText())) {
            if (!Validator.isNonNegativeNumber(priceField.getText())) {
                Ui.error("Max price must be a number.");
                return;
            }
            maxPrice = Double.parseDouble(priceField.getText().trim());
        }
        Integer subjectId = subjectBox.getValue() == null ? null : subjectBox.getValue().getSubjectId();
        Integer gradeId = gradeBox.getValue() == null ? null : gradeBox.getValue().getGradeId();
        Integer areaId = areaBox.getValue() == null ? null : areaBox.getValue().getAreaId();
        try {
            List<TeacherResult> found = catalogService.search(subjectId, gradeId, areaId, maxPrice, typeBox.getValue());
            countLabel.setText(found.size() + (found.size() == 1 ? " tutor found" : " tutors found"));
            for (TeacherResult r : found) {
                results.getChildren().add(card(r));
            }
            if (found.isEmpty()) {
                Label empty = Ui.label("No tutors match your search. Try changing the filters.", "subtitle");
                results.getChildren().add(empty);
            }
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }

    private HBox card(TeacherResult r) {
        Teacher t = r.getTeacher();

        Label initial = new Label(t.getFullName().substring(0, 1).toUpperCase());
        StackPane avatar = new StackPane(initial);
        avatar.getStyleClass().add("avatar");

        HBox chips = new HBox(8,
                Ui.chip(Ui.capitalize(t.getLessonType().name()), "chip-" + t.getLessonType().name().toLowerCase()),
                Ui.chip(t.getExperienceYears() + " yrs experience", "chip"));

        Label subjects = Ui.label("Subjects: " + (r.getSubjects() == null ? "not added yet" : r.getSubjects()), "muted");
        Label areas = Ui.label("Areas: " + (r.getAreas() == null ? "not added yet" : r.getAreas()), "muted");
        subjects.setWrapText(true);
        areas.setWrapText(true);

        VBox info = new VBox(6, Ui.label(t.getFullName(), "tutor-name"), chips, subjects, areas);
        if (t.getBio() != null && !t.getBio().isBlank()) {
            Label bio = Ui.label(t.getBio(), "subtitle");
            bio.setWrapText(true);
            info.getChildren().add(bio);
        }
        HBox.setHgrow(info, Priority.ALWAYS);

        Label price = Ui.label(String.format("%.0f EGP", t.getPricePerLesson()), "price");
        Button book = Ui.button("Book a lesson", "primary-button");
        book.setOnAction(e -> {
            if (new BookDialog().show(t)) {
                Ui.info("Your request was sent to " + t.getFullName() + ". You will see its status in My bookings.");
            }
        });
        VBox side = new VBox(8, price, Ui.label("per lesson", "muted"), book);
        side.setAlignment(Pos.CENTER_RIGHT);

        HBox card = new HBox(18, avatar, info, side);
        card.getStyleClass().add("card");
        card.setAlignment(Pos.CENTER_LEFT);
        return card;
    }

    private Parent buildBookingsTab() {
        bookingTable = BookingTable.create("Tutor");
        VBox.setVgrow(bookingTable, Priority.ALWAYS);

        Button refresh = Ui.button("Refresh", "secondary-button");
        refresh.setOnAction(e -> loadBookings());
        Button cancel = Ui.button("Cancel selected booking", "danger-button");
        cancel.setOnAction(e -> {
            BookingView selected = bookingTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Ui.info("Please select a booking first.");
                return;
            }
            if (selected.getStatus() != BookingStatus.PENDING && selected.getStatus() != BookingStatus.CONFIRMED) {
                Ui.info("Only pending or confirmed bookings can be cancelled.");
                return;
            }
            if (Ui.confirm("Cancel your lesson with " + selected.getPersonName() + "?")) {
                try {
                    bookingService.cancel(selected.getBookingId());
                    loadBookings();
                } catch (ServiceException ex) {
                    Ui.error(ex.getMessage());
                }
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(12, Ui.label("Your lessons", "section-title"), spacer, refresh, cancel);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(16, top, bookingTable);
        box.setPadding(new Insets(18, 28, 24, 28));
        return box;
    }

    private void loadBookings() {
        try {
            bookingTable.setItems(FXCollections.observableArrayList(
                    bookingService.forParent(Session.getParent().getParentId())));
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }
}

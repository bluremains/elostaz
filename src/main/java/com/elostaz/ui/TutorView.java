package com.elostaz.ui;

import com.elostaz.model.Area;
import com.elostaz.model.Availability;
import com.elostaz.model.BookingStatus;
import com.elostaz.model.BookingView;
import com.elostaz.model.GradeLevel;
import com.elostaz.model.LessonType;
import com.elostaz.model.Subject;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherSubject;
import com.elostaz.service.BookingService;
import com.elostaz.service.CatalogService;
import com.elostaz.service.ServiceException;
import com.elostaz.service.TeacherService;
import com.elostaz.util.Session;
import com.elostaz.util.Validator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TutorView {

    private final TeacherService teacherService = new TeacherService();
    private final BookingService bookingService = new BookingService();
    private final CatalogService catalogService = new CatalogService();
    private final Teacher teacher = Session.getTeacher();

    private TableView<BookingView> requestTable;
    private Label pendingNumber;
    private Label confirmedNumber;
    private Label totalNumber;
    private ListView<TeacherSubject> subjectList;
    private ListView<Area> areaList;
    private TableView<Availability> availabilityTable;

    public Parent build() {
        BorderPane root = new BorderPane();
        root.setTop(Header.create("Hello, " + teacher.getFullName()));

        Tab requests = new Tab("Booking requests", buildRequestsTab());
        Tab profile = new Tab("My profile", buildProfileTab());
        Tab offer = new Tab("Subjects and areas", buildOfferTab());
        Tab times = new Tab("Free times", buildTimesTab());
        TabPane tabs = new TabPane(requests, profile, offer, times);
        tabs.getTabs().forEach(t -> t.setClosable(false));
        tabs.getSelectionModel().selectedItemProperty().addListener((obs, old, now) -> {
            if (now == requests) {
                loadRequests();
            }
        });
        root.setCenter(tabs);

        loadRequests();
        loadSubjects();
        loadAreas();
        loadTimes();
        return root;
    }

    private VBox stat(Label number, String caption) {
        VBox box = new VBox(2, number, Ui.label(caption, "stat-caption"));
        box.getStyleClass().add("card");
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setPrefWidth(200);
        return box;
    }

    private Parent buildRequestsTab() {
        pendingNumber = Ui.label("0", "stat-number");
        confirmedNumber = Ui.label("0", "stat-number");
        totalNumber = Ui.label("0", "stat-number");
        HBox stats = new HBox(16, stat(pendingNumber, "WAITING FOR YOU"), stat(confirmedNumber, "CONFIRMED LESSONS"), stat(totalNumber, "ALL REQUESTS"));

        requestTable = BookingTable.create("Parent");
        VBox.setVgrow(requestTable, Priority.ALWAYS);

        Button confirm = Ui.button("Confirm", "success-button");
        confirm.setOnAction(e -> decide(true));
        Button reject = Ui.button("Reject", "danger-button");
        reject.setOnAction(e -> decide(false));
        Button refresh = Ui.button("Refresh", "secondary-button");
        refresh.setOnAction(e -> loadRequests());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(12, Ui.label("Requests from parents", "section-title"), spacer, refresh, confirm, reject);
        top.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(16, stats, top, requestTable);
        box.setPadding(new Insets(18, 28, 24, 28));
        return box;
    }

    private void decide(boolean accept) {
        BookingView selected = requestTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Ui.info("Please select a request first.");
            return;
        }
        if (selected.getStatus() != BookingStatus.PENDING) {
            Ui.info("Only pending requests can be confirmed or rejected.");
            return;
        }
        try {
            if (accept) {
                bookingService.confirm(selected.getBookingId());
            } else {
                bookingService.reject(selected.getBookingId());
            }
            loadRequests();
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }

    private void loadRequests() {
        try {
            List<BookingView> list = bookingService.forTeacher(teacher.getTeacherId());
            requestTable.setItems(FXCollections.observableArrayList(list));
            pendingNumber.setText(String.valueOf(list.stream().filter(b -> b.getStatus() == BookingStatus.PENDING).count()));
            confirmedNumber.setText(String.valueOf(list.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count()));
            totalNumber.setText(String.valueOf(list.size()));
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }

    private Parent buildProfileTab() {
        TextField fullName = new TextField(teacher.getFullName());
        TextField phone = new TextField(teacher.getPhone());
        TextField email = new TextField(teacher.getEmail() == null ? "" : teacher.getEmail());
        TextField price = new TextField(String.format("%.0f", teacher.getPricePerLesson()));
        ComboBox<LessonType> type = Ui.combo("Lesson type", Arrays.asList(LessonType.values()), t -> Ui.capitalize(t.name()));
        type.setValue(teacher.getLessonType());
        TextField experience = new TextField(String.valueOf(teacher.getExperienceYears()));
        TextArea bio = new TextArea(teacher.getBio() == null ? "" : teacher.getBio());
        bio.setWrapText(true);
        bio.setPrefRowCount(4);

        Button save = Ui.button("Save changes", "primary-button");
        save.setOnAction(e -> {
            try {
                if (!Validator.isNonNegativeNumber(price.getText())) {
                    throw new ServiceException("Please enter a valid price.");
                }
                if (!Validator.isNonNegativeInteger(experience.getText())) {
                    throw new ServiceException("Please enter experience as a whole number of years.");
                }
                Teacher updated = new Teacher(teacher.getTeacherId(), teacher.getUserId(), fullName.getText().trim(),
                        phone.getText().trim(), email.getText().trim().isEmpty() ? null : email.getText().trim(),
                        Double.parseDouble(price.getText().trim()), type.getValue(),
                        Integer.parseInt(experience.getText().trim()), bio.getText().trim());
                teacherService.updateProfile(updated);
                Session.setTeacher(updated);
                Ui.info("Your profile was saved.");
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });

        HBox row1 = new HBox(14, half(Ui.field("FULL NAME", fullName)), half(Ui.field("PHONE", phone)));
        HBox row2 = new HBox(14, half(Ui.field("EMAIL", email)), half(Ui.field("PRICE PER LESSON (EGP)", price)));
        HBox row3 = new HBox(14, half(Ui.field("LESSON TYPE", type)), half(Ui.field("YEARS OF EXPERIENCE", experience)));

        VBox card = new VBox(16, Ui.label("Your profile", "section-title"),
                Ui.label("Parents see this information when they search.", "subtitle"),
                row1, row2, row3, Ui.field("ABOUT YOU", bio), save);
        card.getStyleClass().add("card");
        card.setMaxWidth(760);

        VBox box = new VBox(card);
        box.setPadding(new Insets(18, 28, 24, 28));
        ScrollPane scroll = new ScrollPane(box);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private <T extends Region> T half(T box) {
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private Parent buildOfferTab() {
        List<Subject> subjects = new ArrayList<>();
        List<GradeLevel> grades = new ArrayList<>();
        List<Area> areas = new ArrayList<>();
        try {
            subjects = catalogService.subjects();
            grades = catalogService.grades();
            areas = catalogService.areas();
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }

        subjectList = new ListView<>();
        subjectList.setCellFactory(lv -> new javafx.scene.control.ListCell<TeacherSubject>() {
            @Override
            protected void updateItem(TeacherSubject item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getSubjectName() + "  -  " + item.getGradeName());
            }
        });
        VBox.setVgrow(subjectList, Priority.ALWAYS);
        ComboBox<Subject> subjectBox = Ui.combo("Subject", subjects, Subject::getSubjectName);
        ComboBox<GradeLevel> gradeBox = Ui.combo("Grade", grades, GradeLevel::getGradeName);
        Button addSubject = Ui.button("Add", "primary-button");
        addSubject.setOnAction(e -> {
            if (subjectBox.getValue() == null || gradeBox.getValue() == null) {
                Ui.info("Please choose a subject and a grade.");
                return;
            }
            try {
                teacherService.addSubject(teacher.getTeacherId(), subjectBox.getValue().getSubjectId(), gradeBox.getValue().getGradeId());
                loadSubjects();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });
        Button removeSubject = Ui.button("Remove selected", "danger-button");
        removeSubject.setOnAction(e -> {
            TeacherSubject s = subjectList.getSelectionModel().getSelectedItem();
            if (s == null) {
                Ui.info("Please select an item first.");
                return;
            }
            try {
                teacherService.removeSubject(teacher.getTeacherId(), s.getSubjectId(), s.getGradeId());
                loadSubjects();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });
        HBox subjectRow = new HBox(10, half(subjectBox), half(gradeBox), addSubject);
        VBox subjectCard = new VBox(14, Ui.label("What you teach", "section-title"),
                Ui.label("Add each subject with the grade level.", "subtitle"),
                subjectRow, subjectList, removeSubject);
        subjectCard.getStyleClass().add("card");
        HBox.setHgrow(subjectCard, Priority.ALWAYS);
        subjectCard.setMaxWidth(Double.MAX_VALUE);

        areaList = new ListView<>();
        areaList.setCellFactory(lv -> new javafx.scene.control.ListCell<Area>() {
            @Override
            protected void updateItem(Area item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getAreaName());
            }
        });
        VBox.setVgrow(areaList, Priority.ALWAYS);
        ComboBox<Area> areaBox = Ui.combo("Area", areas, Area::getAreaName);
        Button addArea = Ui.button("Add", "primary-button");
        addArea.setOnAction(e -> {
            if (areaBox.getValue() == null) {
                Ui.info("Please choose an area.");
                return;
            }
            try {
                teacherService.addArea(teacher.getTeacherId(), areaBox.getValue().getAreaId());
                loadAreas();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });
        Button removeArea = Ui.button("Remove selected", "danger-button");
        removeArea.setOnAction(e -> {
            Area a = areaList.getSelectionModel().getSelectedItem();
            if (a == null) {
                Ui.info("Please select an area first.");
                return;
            }
            try {
                teacherService.removeArea(teacher.getTeacherId(), a.getAreaId());
                loadAreas();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });
        HBox areaRow = new HBox(10, half(areaBox), addArea);
        VBox areaCard = new VBox(14, Ui.label("Where you teach", "section-title"),
                Ui.label("Choose the areas you can reach.", "subtitle"),
                areaRow, areaList, removeArea);
        areaCard.getStyleClass().add("card");
        HBox.setHgrow(areaCard, Priority.ALWAYS);
        areaCard.setMaxWidth(Double.MAX_VALUE);

        HBox box = new HBox(18, subjectCard, areaCard);
        box.setPadding(new Insets(18, 28, 24, 28));
        return box;
    }

    private void loadSubjects() {
        try {
            subjectList.setItems(FXCollections.observableArrayList(teacherService.subjects(teacher.getTeacherId())));
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }

    private void loadAreas() {
        try {
            areaList.setItems(FXCollections.observableArrayList(teacherService.areas(teacher.getTeacherId())));
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }

    private Parent buildTimesTab() {
        List<DayOfWeek> days = Arrays.asList(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
        List<LocalTime> hours = new ArrayList<>();
        for (int h = 6; h <= 23; h++) {
            hours.add(LocalTime.of(h, 0));
        }
        ComboBox<DayOfWeek> dayBox = Ui.combo("Day", days, d -> Ui.capitalize(d.name()));
        ComboBox<LocalTime> fromBox = Ui.combo("From", hours, t -> t.format(Ui.TIME));
        ComboBox<LocalTime> toBox = Ui.combo("To", hours, t -> t.format(Ui.TIME));

        Button add = Ui.button("Add free time", "primary-button");
        add.setMaxWidth(Double.MAX_VALUE);
        add.setOnAction(e -> {
            try {
                teacherService.addAvailability(teacher.getTeacherId(), dayBox.getValue(), fromBox.getValue(), toBox.getValue());
                loadTimes();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });

        VBox form = new VBox(14, Ui.label("Add a free time", "section-title"),
                Ui.label("Parents can book one-hour lessons inside these times.", "subtitle"),
                Ui.field("DAY", dayBox), Ui.field("FROM", fromBox), Ui.field("TO", toBox), add);
        form.getStyleClass().add("card");
        form.setPrefWidth(330);
        form.setMinWidth(300);
        form.setMaxHeight(Region.USE_PREF_SIZE);

        availabilityTable = new TableView<>();
        availabilityTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        availabilityTable.setPlaceholder(new Label("No free times yet."));
        TableColumn<Availability, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(c -> new SimpleStringProperty(Ui.capitalize(c.getValue().getDayOfWeek().name())));
        TableColumn<Availability, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStartTime().format(Ui.TIME)));
        TableColumn<Availability, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEndTime().format(Ui.TIME)));
        availabilityTable.getColumns().add(dayCol);
        availabilityTable.getColumns().add(fromCol);
        availabilityTable.getColumns().add(toCol);
        VBox.setVgrow(availabilityTable, Priority.ALWAYS);

        Button remove = Ui.button("Remove selected", "danger-button");
        remove.setOnAction(e -> {
            Availability a = availabilityTable.getSelectionModel().getSelectedItem();
            if (a == null) {
                Ui.info("Please select a time first.");
                return;
            }
            try {
                teacherService.removeAvailability(a.getAvailabilityId());
                loadTimes();
            } catch (ServiceException ex) {
                Ui.error(ex.getMessage());
            }
        });

        VBox right = new VBox(14, Ui.label("Your weekly schedule", "section-title"), availabilityTable, remove);
        HBox.setHgrow(right, Priority.ALWAYS);

        HBox box = new HBox(18, form, right);
        box.setPadding(new Insets(18, 28, 24, 28));
        return box;
    }

    private void loadTimes() {
        try {
            availabilityTable.setItems(FXCollections.observableArrayList(teacherService.availability(teacher.getTeacherId())));
        } catch (ServiceException e) {
            Ui.error(e.getMessage());
        }
    }
}

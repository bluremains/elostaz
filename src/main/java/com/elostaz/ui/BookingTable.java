package com.elostaz.ui;

import com.elostaz.model.BookingStatus;
import com.elostaz.model.BookingView;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BookingTable {

    private BookingTable() {
    }

    public static TableView<BookingView> create(String personColumn) {
        TableView<BookingView> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No bookings yet."));

        TableColumn<BookingView, String> person = new TableColumn<>(personColumn);
        person.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersonName()));

        TableColumn<BookingView, String> phone = new TableColumn<>("Phone");
        phone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPersonPhone()));

        TableColumn<BookingView, String> subject = new TableColumn<>("Subject");
        subject.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubjectName()));

        TableColumn<BookingView, String> date = new TableColumn<>("Date");
        date.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLessonDate().format(Ui.DATE)));

        TableColumn<BookingView, String> time = new TableColumn<>("Time");
        time.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getStartTime().format(Ui.TIME) + " - " + c.getValue().getEndTime().format(Ui.TIME)));

        TableColumn<BookingView, BookingStatus> status = new TableColumn<>("Status");
        status.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        status.setCellFactory(col -> new TableCell<BookingView, BookingStatus>() {
            @Override
            protected void updateItem(BookingStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(Ui.chip(Ui.capitalize(item.name()), "status-" + item.name().toLowerCase()));
                }
            }
        });

        table.getColumns().add(person);
        table.getColumns().add(phone);
        table.getColumns().add(subject);
        table.getColumns().add(date);
        table.getColumns().add(time);
        table.getColumns().add(status);
        return table;
    }
}

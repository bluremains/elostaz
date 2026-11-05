package com.elostaz.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingView {

    private int bookingId;
    private String personName;
    private String personPhone;
    private String subjectName;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;

    public BookingView(int bookingId, String personName, String personPhone, String subjectName, LocalDate lessonDate, LocalTime startTime, LocalTime endTime, BookingStatus status) {
        this.bookingId = bookingId;
        this.personName = personName;
        this.personPhone = personPhone;
        this.subjectName = subjectName;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }
}

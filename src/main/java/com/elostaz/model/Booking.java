package com.elostaz.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {

    private int bookingId;
    private int parentId;
    private int teacherId;
    private int subjectId;
    private LocalDate lessonDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatus status;

    public Booking() {
    }

    public Booking(int bookingId, int parentId, int teacherId, int subjectId, LocalDate lessonDate, LocalTime startTime, LocalTime endTime, BookingStatus status) {
        this.bookingId = bookingId;
        this.parentId = parentId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.lessonDate = lessonDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getLessonDate() {
        return lessonDate;
    }

    public void setLessonDate(LocalDate lessonDate) {
        this.lessonDate = lessonDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}

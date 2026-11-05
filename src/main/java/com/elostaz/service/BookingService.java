package com.elostaz.service;

import com.elostaz.dao.AvailabilityDAO;
import com.elostaz.dao.BookingDAO;
import com.elostaz.model.Availability;
import com.elostaz.model.Booking;
import com.elostaz.model.BookingStatus;
import com.elostaz.model.BookingView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final AvailabilityDAO availabilityDAO = new AvailabilityDAO();

    public List<LocalTime> freeSlots(int teacherId, LocalDate date) throws ServiceException {
        List<LocalTime> slots = new ArrayList<>();
        try {
            List<LocalTime> taken = bookingDAO.findTakenStarts(teacherId, date);
            for (Availability a : availabilityDAO.findByTeacher(teacherId)) {
                if (a.getDayOfWeek() != date.getDayOfWeek()) {
                    continue;
                }
                LocalTime start = a.getStartTime();
                while (!start.plusHours(1).isAfter(a.getEndTime())) {
                    if (!taken.contains(start) && !(date.equals(LocalDate.now()) && start.isBefore(LocalTime.now()))) {
                        slots.add(start);
                    }
                    start = start.plusHours(1);
                }
            }
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
        slots.sort(null);
        return slots;
    }

    public void book(int parentId, int teacherId, int subjectId, LocalDate date, LocalTime start) throws ServiceException {
        if (date == null || start == null || subjectId <= 0) {
            throw new ServiceException("Please choose the subject, date and time.");
        }
        if (date.isBefore(LocalDate.now())) {
            throw new ServiceException("You cannot book a date in the past.");
        }
        if (!freeSlots(teacherId, date).contains(start)) {
            throw new ServiceException("This time is no longer available. Please choose another one.");
        }
        try {
            bookingDAO.deleteInactiveSlot(teacherId, date, start);
            bookingDAO.insert(new Booking(0, parentId, teacherId, subjectId, date, start, start.plusHours(1), BookingStatus.PENDING));
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ServiceException("Someone just booked this time. Please choose another one.", e);
            }
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<BookingView> forParent(int parentId) throws ServiceException {
        try {
            return bookingDAO.findForParent(parentId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<BookingView> forTeacher(int teacherId) throws ServiceException {
        try {
            return bookingDAO.findForTeacher(teacherId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void confirm(int bookingId) throws ServiceException {
        changeStatus(bookingId, BookingStatus.CONFIRMED);
    }

    public void reject(int bookingId) throws ServiceException {
        changeStatus(bookingId, BookingStatus.REJECTED);
    }

    public void cancel(int bookingId) throws ServiceException {
        changeStatus(bookingId, BookingStatus.CANCELLED);
    }

    private void changeStatus(int bookingId, BookingStatus status) throws ServiceException {
        try {
            bookingDAO.updateStatus(bookingId, status);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }
}

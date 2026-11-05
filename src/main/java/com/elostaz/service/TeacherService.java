package com.elostaz.service;

import com.elostaz.dao.AvailabilityDAO;
import com.elostaz.dao.TeacherDAO;
import com.elostaz.model.Area;
import com.elostaz.model.Availability;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherSubject;
import com.elostaz.util.Validator;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class TeacherService {

    private final TeacherDAO teacherDAO = new TeacherDAO();
    private final AvailabilityDAO availabilityDAO = new AvailabilityDAO();

    public static void checkProfile(Teacher t) throws ServiceException {
        if (!Validator.isNotBlank(t.getFullName())) {
            throw new ServiceException("Please enter your full name.");
        }
        if (!Validator.isValidPhone(t.getPhone())) {
            throw new ServiceException("Please enter a valid Egyptian mobile number, e.g. 01012345678.");
        }
        if (Validator.isNotBlank(t.getEmail()) && !Validator.isValidEmail(t.getEmail())) {
            throw new ServiceException("The email address is not valid.");
        }
        if (t.getPricePerLesson() < 0) {
            throw new ServiceException("Price cannot be negative.");
        }
        if (t.getExperienceYears() < 0) {
            throw new ServiceException("Experience years cannot be negative.");
        }
        if (t.getLessonType() == null) {
            throw new ServiceException("Please choose the lesson type.");
        }
    }

    public void updateProfile(Teacher t) throws ServiceException {
        checkProfile(t);
        try {
            teacherDAO.update(t);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<TeacherSubject> subjects(int teacherId) throws ServiceException {
        try {
            return teacherDAO.findSubjects(teacherId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void addSubject(int teacherId, int subjectId, int gradeId) throws ServiceException {
        try {
            teacherDAO.addSubject(teacherId, subjectId, gradeId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void removeSubject(int teacherId, int subjectId, int gradeId) throws ServiceException {
        try {
            teacherDAO.removeSubject(teacherId, subjectId, gradeId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<Area> areas(int teacherId) throws ServiceException {
        try {
            return teacherDAO.findAreas(teacherId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void addArea(int teacherId, int areaId) throws ServiceException {
        try {
            teacherDAO.addArea(teacherId, areaId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void removeArea(int teacherId, int areaId) throws ServiceException {
        try {
            teacherDAO.removeArea(teacherId, areaId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<Availability> availability(int teacherId) throws ServiceException {
        try {
            return availabilityDAO.findByTeacher(teacherId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void addAvailability(int teacherId, DayOfWeek day, LocalTime start, LocalTime end) throws ServiceException {
        if (day == null || start == null || end == null) {
            throw new ServiceException("Please choose the day, start time and end time.");
        }
        if (!end.isAfter(start)) {
            throw new ServiceException("End time must be after start time.");
        }
        try {
            for (Availability a : availabilityDAO.findByTeacher(teacherId)) {
                if (a.getDayOfWeek() == day && start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime())) {
                    throw new ServiceException("This time overlaps with a slot you already added.");
                }
            }
            availabilityDAO.insert(new Availability(0, teacherId, day, start, end));
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void removeAvailability(int availabilityId) throws ServiceException {
        try {
            availabilityDAO.delete(availabilityId);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }
}

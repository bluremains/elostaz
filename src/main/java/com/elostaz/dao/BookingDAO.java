package com.elostaz.dao;

import com.elostaz.model.Booking;
import com.elostaz.model.BookingStatus;
import com.elostaz.model.BookingView;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public void insert(Booking b) throws SQLException {
        String sql = "INSERT INTO bookings (parent_id, teacher_id, subject_id, lesson_date, start_time, end_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, b.getParentId());
            ps.setInt(2, b.getTeacherId());
            ps.setInt(3, b.getSubjectId());
            ps.setDate(4, Date.valueOf(b.getLessonDate()));
            ps.setTime(5, Time.valueOf(b.getStartTime()));
            ps.setTime(6, Time.valueOf(b.getEndTime()));
            ps.setString(7, b.getStatus().name());
            ps.executeUpdate();
        }
    }

    public void deleteInactiveSlot(int teacherId, LocalDate date, LocalTime start) throws SQLException {
        String sql = "DELETE FROM bookings WHERE teacher_id = ? AND lesson_date = ? AND start_time = ? AND status IN ('REJECTED', 'CANCELLED')";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(start));
            ps.executeUpdate();
        }
    }

    public List<LocalTime> findTakenStarts(int teacherId, LocalDate date) throws SQLException {
        String sql = "SELECT start_time FROM bookings WHERE teacher_id = ? AND lesson_date = ? AND status IN ('PENDING', 'CONFIRMED')";
        List<LocalTime> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getTime("start_time").toLocalTime());
                }
            }
        }
        return list;
    }

    public List<BookingView> findForParent(int parentId) throws SQLException {
        String sql = "SELECT b.booking_id, t.full_name AS person_name, t.phone AS person_phone, s.subject_name, b.lesson_date, b.start_time, b.end_time, b.status "
                + "FROM bookings b "
                + "JOIN teachers t ON t.teacher_id = b.teacher_id "
                + "JOIN subjects s ON s.subject_id = b.subject_id "
                + "WHERE b.parent_id = ? ORDER BY b.lesson_date DESC, b.start_time DESC";
        return query(sql, parentId);
    }

    public List<BookingView> findForTeacher(int teacherId) throws SQLException {
        String sql = "SELECT b.booking_id, p.full_name AS person_name, p.phone AS person_phone, s.subject_name, b.lesson_date, b.start_time, b.end_time, b.status "
                + "FROM bookings b "
                + "JOIN parents p ON p.parent_id = b.parent_id "
                + "JOIN subjects s ON s.subject_id = b.subject_id "
                + "WHERE b.teacher_id = ? ORDER BY b.lesson_date DESC, b.start_time DESC";
        return query(sql, teacherId);
    }

    public void updateStatus(int bookingId, BookingStatus status) throws SQLException {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, bookingId);
            ps.executeUpdate();
        }
    }

    private List<BookingView> query(String sql, int id) throws SQLException {
        List<BookingView> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new BookingView(rs.getInt("booking_id"), rs.getString("person_name"),
                            rs.getString("person_phone"), rs.getString("subject_name"),
                            rs.getDate("lesson_date").toLocalDate(), rs.getTime("start_time").toLocalTime(),
                            rs.getTime("end_time").toLocalTime(), BookingStatus.valueOf(rs.getString("status"))));
                }
            }
        }
        return list;
    }
}

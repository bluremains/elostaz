package com.elostaz.dao;

import com.elostaz.model.Availability;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityDAO {

    public void insert(Availability a) throws SQLException {
        String sql = "INSERT INTO availability (teacher_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getTeacherId());
            ps.setString(2, a.getDayOfWeek().name());
            ps.setTime(3, Time.valueOf(a.getStartTime()));
            ps.setTime(4, Time.valueOf(a.getEndTime()));
            ps.executeUpdate();
        }
    }

    public void delete(int availabilityId) throws SQLException {
        String sql = "DELETE FROM availability WHERE availability_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, availabilityId);
            ps.executeUpdate();
        }
    }

    public List<Availability> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT availability_id, teacher_id, day_of_week, start_time, end_time FROM availability WHERE teacher_id = ? ORDER BY FIELD(day_of_week, 'SATURDAY','SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY'), start_time";
        List<Availability> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Availability(rs.getInt("availability_id"), rs.getInt("teacher_id"),
                            DayOfWeek.valueOf(rs.getString("day_of_week")),
                            rs.getTime("start_time").toLocalTime(), rs.getTime("end_time").toLocalTime()));
                }
            }
        }
        return list;
    }
}

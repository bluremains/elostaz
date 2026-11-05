package com.elostaz.dao;

import com.elostaz.model.Area;
import com.elostaz.model.LessonType;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherSubject;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public void insert(Connection con, Teacher t) throws SQLException {
        String sql = "INSERT INTO teachers (user_id, full_name, phone, email, price_per_lesson, lesson_type, experience_years, bio) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getUserId());
            ps.setString(2, t.getFullName());
            ps.setString(3, t.getPhone());
            ps.setString(4, t.getEmail());
            ps.setDouble(5, t.getPricePerLesson());
            ps.setString(6, t.getLessonType().name());
            ps.setInt(7, t.getExperienceYears());
            ps.setString(8, t.getBio());
            ps.executeUpdate();
        }
    }

    public Teacher findByUserId(int userId) throws SQLException {
        String sql = "SELECT teacher_id, user_id, full_name, phone, email, price_per_lesson, lesson_type, experience_years, bio FROM teachers WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void update(Teacher t) throws SQLException {
        String sql = "UPDATE teachers SET full_name = ?, phone = ?, email = ?, price_per_lesson = ?, lesson_type = ?, experience_years = ?, bio = ? WHERE teacher_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getFullName());
            ps.setString(2, t.getPhone());
            ps.setString(3, t.getEmail());
            ps.setDouble(4, t.getPricePerLesson());
            ps.setString(5, t.getLessonType().name());
            ps.setInt(6, t.getExperienceYears());
            ps.setString(7, t.getBio());
            ps.setInt(8, t.getTeacherId());
            ps.executeUpdate();
        }
    }

    public List<TeacherSubject> findSubjects(int teacherId) throws SQLException {
        String sql = "SELECT s.subject_id, s.subject_name, g.grade_id, g.grade_name "
                + "FROM teacher_subjects ts "
                + "JOIN subjects s ON s.subject_id = ts.subject_id "
                + "JOIN grade_levels g ON g.grade_id = ts.grade_id "
                + "WHERE ts.teacher_id = ? ORDER BY s.subject_name, g.grade_id";
        List<TeacherSubject> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TeacherSubject(rs.getInt("subject_id"), rs.getString("subject_name"),
                            rs.getInt("grade_id"), rs.getString("grade_name")));
                }
            }
        }
        return list;
    }

    public void addSubject(int teacherId, int subjectId, int gradeId) throws SQLException {
        String sql = "INSERT IGNORE INTO teacher_subjects (teacher_id, subject_id, grade_id) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            ps.setInt(3, gradeId);
            ps.executeUpdate();
        }
    }

    public void removeSubject(int teacherId, int subjectId, int gradeId) throws SQLException {
        String sql = "DELETE FROM teacher_subjects WHERE teacher_id = ? AND subject_id = ? AND grade_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, subjectId);
            ps.setInt(3, gradeId);
            ps.executeUpdate();
        }
    }

    public List<Area> findAreas(int teacherId) throws SQLException {
        String sql = "SELECT a.area_id, a.area_name FROM teacher_areas ta JOIN areas a ON a.area_id = ta.area_id WHERE ta.teacher_id = ? ORDER BY a.area_name";
        List<Area> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Area(rs.getInt("area_id"), rs.getString("area_name")));
                }
            }
        }
        return list;
    }

    public void addArea(int teacherId, int areaId) throws SQLException {
        String sql = "INSERT IGNORE INTO teacher_areas (teacher_id, area_id) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, areaId);
            ps.executeUpdate();
        }
    }

    public void removeArea(int teacherId, int areaId) throws SQLException {
        String sql = "DELETE FROM teacher_areas WHERE teacher_id = ? AND area_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, areaId);
            ps.executeUpdate();
        }
    }

    public static Teacher map(ResultSet rs) throws SQLException {
        return new Teacher(rs.getInt("teacher_id"), rs.getInt("user_id"), rs.getString("full_name"),
                rs.getString("phone"), rs.getString("email"), rs.getDouble("price_per_lesson"),
                LessonType.valueOf(rs.getString("lesson_type")), rs.getInt("experience_years"), rs.getString("bio"));
    }
}

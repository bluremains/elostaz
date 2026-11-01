package com.elostaz.dao;

import com.elostaz.model.Subject;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public List<Subject> findAll() throws SQLException {
        String sql = "SELECT subject_id, subject_name FROM subjects ORDER BY subject_name";
        List<Subject> subjects = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                subjects.add(new Subject(rs.getInt("subject_id"), rs.getString("subject_name")));
            }
        }
        return subjects;
    }

    public void insert(String subjectName) throws SQLException {
        String sql = "INSERT INTO subjects (subject_name) VALUES (?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            ps.executeUpdate();
        }
    }
}

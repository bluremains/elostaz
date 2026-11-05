package com.elostaz.dao;

import com.elostaz.model.GradeLevel;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradeLevelDAO {

    public List<GradeLevel> findAll() throws SQLException {
        String sql = "SELECT grade_id, grade_name FROM grade_levels ORDER BY grade_id";
        List<GradeLevel> grades = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                grades.add(new GradeLevel(rs.getInt("grade_id"), rs.getString("grade_name")));
            }
        }
        return grades;
    }
}

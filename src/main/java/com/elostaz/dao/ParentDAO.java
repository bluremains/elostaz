package com.elostaz.dao;

import com.elostaz.model.Parent;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParentDAO {

    public void insert(Connection con, Parent parent) throws SQLException {
        String sql = "INSERT INTO parents (user_id, full_name, phone) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, parent.getUserId());
            ps.setString(2, parent.getFullName());
            ps.setString(3, parent.getPhone());
            ps.executeUpdate();
        }
    }

    public Parent findByUserId(int userId) throws SQLException {
        String sql = "SELECT parent_id, user_id, full_name, phone FROM parents WHERE user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Parent(rs.getInt("parent_id"), rs.getInt("user_id"),
                            rs.getString("full_name"), rs.getString("phone"));
                }
            }
        }
        return null;
    }
}

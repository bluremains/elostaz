package com.elostaz.dao;

import com.elostaz.model.Area;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> findAll() throws SQLException {
        String sql = "SELECT area_id, area_name FROM areas ORDER BY area_name";
        List<Area> areas = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                areas.add(new Area(rs.getInt("area_id"), rs.getString("area_name")));
            }
        }
        return areas;
    }
}

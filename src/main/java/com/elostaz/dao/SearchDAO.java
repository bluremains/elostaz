package com.elostaz.dao;

import com.elostaz.model.LessonType;
import com.elostaz.model.Teacher;
import com.elostaz.model.TeacherResult;
import com.elostaz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchDAO {

    public List<TeacherResult> search(Integer subjectId, Integer gradeId, Integer areaId, Double maxPrice, LessonType lessonType) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT t.teacher_id, t.user_id, t.full_name, t.phone, t.email, t.price_per_lesson, t.lesson_type, t.experience_years, t.bio, ");
        sql.append("(SELECT GROUP_CONCAT(DISTINCT s.subject_name ORDER BY s.subject_name SEPARATOR ', ') FROM teacher_subjects ts JOIN subjects s ON s.subject_id = ts.subject_id WHERE ts.teacher_id = t.teacher_id) AS subjects, ");
        sql.append("(SELECT GROUP_CONCAT(a.area_name ORDER BY a.area_name SEPARATOR ', ') FROM teacher_areas ta JOIN areas a ON a.area_id = ta.area_id WHERE ta.teacher_id = t.teacher_id) AS areas ");
        sql.append("FROM teachers t WHERE 1 = 1 ");

        List<Object> params = new ArrayList<>();

        if (subjectId != null || gradeId != null) {
            sql.append("AND EXISTS (SELECT 1 FROM teacher_subjects x WHERE x.teacher_id = t.teacher_id ");
            if (subjectId != null) {
                sql.append("AND x.subject_id = ? ");
                params.add(subjectId);
            }
            if (gradeId != null) {
                sql.append("AND x.grade_id = ? ");
                params.add(gradeId);
            }
            sql.append(") ");
        }
        if (areaId != null) {
            sql.append("AND EXISTS (SELECT 1 FROM teacher_areas y WHERE y.teacher_id = t.teacher_id AND y.area_id = ?) ");
            params.add(areaId);
        }
        if (maxPrice != null) {
            sql.append("AND t.price_per_lesson <= ? ");
            params.add(maxPrice);
        }
        if (lessonType != null) {
            sql.append("AND t.lesson_type = ? ");
            params.add(lessonType.name());
        }
        sql.append("ORDER BY t.price_per_lesson, t.full_name");

        List<TeacherResult> results = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Teacher t = TeacherDAO.map(rs);
                    results.add(new TeacherResult(t, rs.getString("subjects"), rs.getString("areas")));
                }
            }
        }
        return results;
    }
}

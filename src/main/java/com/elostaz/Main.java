package com.elostaz;

import com.elostaz.dao.SubjectDAO;
import com.elostaz.model.Subject;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        try {
            for (Subject s : new SubjectDAO().findAll()) {
                System.out.println(s.getSubjectId() + " - " + s.getSubjectName());
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}

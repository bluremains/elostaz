package com.elostaz.service;

import com.elostaz.dao.AreaDAO;
import com.elostaz.dao.GradeLevelDAO;
import com.elostaz.dao.SearchDAO;
import com.elostaz.dao.SubjectDAO;
import com.elostaz.model.Area;
import com.elostaz.model.GradeLevel;
import com.elostaz.model.LessonType;
import com.elostaz.model.Subject;
import com.elostaz.model.TeacherResult;

import java.sql.SQLException;
import java.util.List;

public class CatalogService {

    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final GradeLevelDAO gradeLevelDAO = new GradeLevelDAO();
    private final AreaDAO areaDAO = new AreaDAO();
    private final SearchDAO searchDAO = new SearchDAO();

    public List<Subject> subjects() throws ServiceException {
        try {
            return subjectDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<GradeLevel> grades() throws ServiceException {
        try {
            return gradeLevelDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<Area> areas() throws ServiceException {
        try {
            return areaDAO.findAll();
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public List<TeacherResult> search(Integer subjectId, Integer gradeId, Integer areaId, Double maxPrice, LessonType lessonType) throws ServiceException {
        try {
            return searchDAO.search(subjectId, gradeId, areaId, maxPrice, lessonType);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }
}

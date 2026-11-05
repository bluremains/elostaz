package com.elostaz.service;

import com.elostaz.dao.ParentDAO;
import com.elostaz.dao.TeacherDAO;
import com.elostaz.dao.UserDAO;
import com.elostaz.model.Parent;
import com.elostaz.model.Role;
import com.elostaz.model.Teacher;
import com.elostaz.model.User;
import com.elostaz.util.DBConnection;
import com.elostaz.util.PasswordHasher;
import com.elostaz.util.Session;
import com.elostaz.util.Validator;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final ParentDAO parentDAO = new ParentDAO();
    private final TeacherDAO teacherDAO = new TeacherDAO();

    public void login(String username, String password) throws ServiceException {
        if (!Validator.isNotBlank(username) || !Validator.isNotBlank(password)) {
            throw new ServiceException("Please enter your username and password.");
        }
        try {
            User user = userDAO.findByUsername(username.trim());
            if (user == null || !PasswordHasher.verify(password, user.getPasswordHash())) {
                throw new ServiceException("Wrong username or password.");
            }
            Parent parent = null;
            Teacher teacher = null;
            if (user.getRole() == Role.PARENT) {
                parent = parentDAO.findByUserId(user.getUserId());
            } else if (user.getRole() == Role.TUTOR) {
                teacher = teacherDAO.findByUserId(user.getUserId());
            }
            Session.start(user, parent, teacher);
        } catch (SQLException e) {
            throw new ServiceException("Database error: " + e.getMessage(), e);
        }
    }

    public void registerParent(String username, String password, String fullName, String phone) throws ServiceException {
        checkAccount(username, password, fullName, phone);
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId = userDAO.insert(con, new User(0, username.trim(), PasswordHasher.hash(password), Role.PARENT));
                parentDAO.insert(con, new Parent(0, userId, fullName.trim(), phone.trim()));
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    public void registerTutor(String username, String password, Teacher teacher) throws ServiceException {
        checkAccount(username, password, teacher.getFullName(), teacher.getPhone());
        TeacherService.checkProfile(teacher);
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int userId = userDAO.insert(con, new User(0, username.trim(), PasswordHasher.hash(password), Role.TUTOR));
                teacher.setUserId(userId);
                teacherDAO.insert(con, teacher);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw translate(e);
        }
    }

    private void checkAccount(String username, String password, String fullName, String phone) throws ServiceException {
        if (!Validator.isValidUsername(username)) {
            throw new ServiceException("Username must be 3-30 characters: letters, numbers or underscore.");
        }
        if (!Validator.isValidPassword(password)) {
            throw new ServiceException("Password must be at least 6 characters.");
        }
        if (!Validator.isNotBlank(fullName)) {
            throw new ServiceException("Please enter your full name.");
        }
        if (!Validator.isValidPhone(phone)) {
            throw new ServiceException("Please enter a valid Egyptian mobile number, e.g. 01012345678.");
        }
    }

    private ServiceException translate(SQLException e) {
        if (e.getErrorCode() == 1062 || "23000".equals(e.getSQLState())) {
            return new ServiceException("This username is already taken.", e);
        }
        return new ServiceException("Database error: " + e.getMessage(), e);
    }
}

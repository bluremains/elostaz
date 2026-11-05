package com.elostaz.util;

import com.elostaz.model.Parent;
import com.elostaz.model.Teacher;
import com.elostaz.model.User;

public class Session {

    private static User user;
    private static Parent parent;
    private static Teacher teacher;

    private Session() {
    }

    public static User getUser() {
        return user;
    }

    public static Parent getParent() {
        return parent;
    }

    public static Teacher getTeacher() {
        return teacher;
    }

    public static void start(User u, Parent p, Teacher t) {
        user = u;
        parent = p;
        teacher = t;
    }

    public static void setTeacher(Teacher t) {
        teacher = t;
    }

    public static void end() {
        user = null;
        parent = null;
        teacher = null;
    }
}

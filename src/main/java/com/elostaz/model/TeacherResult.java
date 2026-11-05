package com.elostaz.model;

public class TeacherResult {

    private Teacher teacher;
    private String subjects;
    private String areas;

    public TeacherResult(Teacher teacher, String subjects, String areas) {
        this.teacher = teacher;
        this.subjects = subjects;
        this.areas = areas;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getAreas() {
        return areas;
    }
}

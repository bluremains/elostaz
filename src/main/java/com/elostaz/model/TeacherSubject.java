package com.elostaz.model;

public class TeacherSubject {

    private int subjectId;
    private String subjectName;
    private int gradeId;
    private String gradeName;

    public TeacherSubject(int subjectId, String subjectName, int gradeId, String gradeName) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.gradeId = gradeId;
        this.gradeName = gradeName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getGradeId() {
        return gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }
}

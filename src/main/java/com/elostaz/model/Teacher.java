package com.elostaz.model;

public class Teacher {

    private int teacherId;
    private int userId;
    private String fullName;
    private String phone;
    private String email;
    private double pricePerLesson;
    private LessonType lessonType;
    private int experienceYears;
    private String bio;

    public Teacher() {
    }

    public Teacher(int teacherId, int userId, String fullName, String phone, String email, double pricePerLesson, LessonType lessonType, int experienceYears, String bio) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.pricePerLesson = pricePerLesson;
        this.lessonType = lessonType;
        this.experienceYears = experienceYears;
        this.bio = bio;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPricePerLesson() {
        return pricePerLesson;
    }

    public void setPricePerLesson(double pricePerLesson) {
        this.pricePerLesson = pricePerLesson;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}

package com.elostaz.model;

public class Parent {

    private int parentId;
    private int userId;
    private String fullName;
    private String phone;

    public Parent() {
    }

    public Parent(int parentId, int userId, String fullName, String phone) {
        this.parentId = parentId;
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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
}

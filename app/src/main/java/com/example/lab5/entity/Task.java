package com.example.lab5.entity;

import java.io.Serializable;
import java.util.Date;

public class Task implements Serializable {
    public static final int IMPORTANCE_HIGH = 1;
    public static final int IMPORTANCE_DEFAULT = 2;
    public static final int IMPORTANCE_LOW = 3;

    private String title;
    private String description;
    private Date dueDate;
    private int importance;

    public Task(String title, String description, Date dueDate, int importance) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.importance = importance;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}

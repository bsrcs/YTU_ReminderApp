package com.busra.reminder.model;

public class Task {

    private String titleTask;
    private String descTask;
    private String dateTask;
    private String keyTask;
    private String keyFirebase;

    public Task() {
    }

    public Task(String titleTask, String descTask, String dateTask, String keyTask, String keyFirebase) {
        this.titleTask = titleTask;
        this.descTask = descTask;
        this.dateTask = dateTask;
        this.keyTask = keyTask;
        this.keyFirebase = keyFirebase;
    }

    public String getTitleTask() {
        return titleTask;
    }

    public void setTitleTask(String titleTask) {
        this.titleTask = titleTask;
    }

    public String getDescTask() {
        return descTask;
    }

    public void setDescTask(String descTask) {
        this.descTask = descTask;
    }

    public String getDateTask() {
        return dateTask;
    }

    public void setDateTask(String dateTask) {
        this.dateTask = dateTask;
    }

    public String getKeyTask() {
        return keyTask;
    }

    public void setKeyTask(String keyTask) {
        this.keyTask = keyTask;
    }

    public String getKeyFirebase() {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase) {
        this.keyFirebase = keyFirebase;
    }
}
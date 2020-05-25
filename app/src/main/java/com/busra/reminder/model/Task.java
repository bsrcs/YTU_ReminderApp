package com.busra.reminder.model;

public class Task {

    private String title;
    private String desc;
    private String date;
    private String key;
    private String category;
    private String frequency;
    private String keyFirebase;

    public Task() {
    }

    public Task(String title, String desc, String date, String key, String keyFirebase) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.key = key;
        this.keyFirebase = keyFirebase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyFirebase() {
        return keyFirebase;
    }

    public void setKeyFirebase(String keyFirebase) {
        this.keyFirebase = keyFirebase;
    }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public String getFrequency() { return frequency; }

    public void setFrequency(String frequency) { this.frequency = frequency; }
}
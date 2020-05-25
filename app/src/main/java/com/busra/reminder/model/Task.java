package com.busra.reminder.model;

public class Task {

    private String title;
    private String desc;
    private String date;
    private String id;
    private String category;
    private String frequency;

    public Task() {
    }

    public Task(String title, String desc, String date, String id, String category, String frequency) {
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.id = id;
        this.category = category;
        this.frequency = frequency;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", frequency='" + frequency + '\'' +
                '}';
    }
}
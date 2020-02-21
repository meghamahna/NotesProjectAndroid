package com.example.notesproject;

import java.io.Serializable;

public class NoteClass implements Serializable {

    int id;
    String title;
    String description;
    String category;
    String date;
    Double latitude;
    Double longitude;

    public NoteClass(int id, String title, String description, String category, String date, Double latitude, Double longitude) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}

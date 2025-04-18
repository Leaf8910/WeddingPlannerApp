package com;

/**
 * Model class representing planning details for a wedding
 */
public class Planning {
    private int id;
    private int userId;
    private String venue;
    private String hall;
    private String catering;
    private String month;
    private String day;

    // Constructors
    public Planning() {}

    public Planning(int userId, String venue, String hall, String catering, String month, String day) {
        this.userId = userId;
        this.venue = venue;
        this.hall = hall;
        this.catering = catering;
        this.month = month;
        this.day = day;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getCatering() {
        return catering;
    }

    public void setCatering(String catering) {
        this.catering = catering;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "Planning{" +
                "id=" + id +
                ", userId=" + userId +
                ", venue='" + venue + '\'' +
                ", hall='" + hall + '\'' +
                ", catering='" + catering + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                '}';
    }
}
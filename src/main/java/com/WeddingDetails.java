package com;

import java.time.LocalDate;

public class WeddingDetails {
    private int id;
    private String groomName;
    private String brideName;
    private LocalDate weddingDateFrom;
    private LocalDate weddingDateTo;
    private int attendantsCount;
    private String budgetRange;

    // Constructors, getters, and setters
    public WeddingDetails() {}

    public WeddingDetails(String groomName, String brideName, LocalDate weddingDateFrom,
                          LocalDate weddingDateTo, int attendantsCount, String budgetRange) {
        this.groomName = groomName;
        this.brideName = brideName;
        this.weddingDateFrom = weddingDateFrom;
        this.weddingDateTo = weddingDateTo;
        this.attendantsCount = attendantsCount;
        this.budgetRange = budgetRange;
    }

    // Getters and setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getGroomName() { return groomName; }
    public void setGroomName(String groomName) { this.groomName = groomName; }
    public String getBrideName() { return brideName; }
    public void setBrideName(String brideName) { this.brideName = brideName; }
    public LocalDate getWeddingDateFrom() { return weddingDateFrom; }
    public void setWeddingDateFrom(LocalDate weddingDateFrom) { this.weddingDateFrom = weddingDateFrom; }
    public LocalDate getWeddingDateTo() { return weddingDateTo; }
    public void setWeddingDateTo(LocalDate weddingDateTo) { this.weddingDateTo = weddingDateTo; }
    public int getAttendantsCount() { return attendantsCount; }
    public void setAttendantsCount(int attendantsCount) { this.attendantsCount = attendantsCount; }
    public String getBudgetRange() { return budgetRange; }
    public void setBudgetRange(String budgetRange) { this.budgetRange = budgetRange; }
}
package com.example.iconic_raffleevent.model;

/**
 * Represents an event created by an organizer.
 * Stores event details such as title, date, capacity, and participants.
 */
public class Event {
    private String eventId;
    private String title;
    private String date;
    private String description;
    private int capacity;
    private int registeredCount; // Number of people registered so far

    // Constructor
    public Event(String eventId, String title, String date, String description, int capacity) {
        this.eventId = eventId;
        this.title = title;
        this.date = date;
        this.description = description;
        this.capacity = capacity;
        this.registeredCount = 0;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void incrementRegisteredCount() {
        this.registeredCount++;
    }
}
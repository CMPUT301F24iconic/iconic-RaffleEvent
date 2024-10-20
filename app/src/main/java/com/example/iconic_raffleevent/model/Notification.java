package com.example.iconic_raffleevent.model;

/**
 * Represents a notification sent to a user about their lottery results or updates.
 */
public class Notification {
    private String userId;
    private String message;
    private boolean isRead;

    // Constructor
    public Notification(String userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
package com.example.iconic_raffleevent.model;

/**
 * Represents a notification sent to a user about their lottery results or updates.
 */

public class Notification {

    private String notificationId;
    private String userId;
    private String eventId;
    private String message;
    private boolean read;

    public Notification() {
    }

    // Getters and setters

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
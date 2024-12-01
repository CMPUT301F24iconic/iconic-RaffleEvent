package com.example.iconic_raffleevent.model;

/**
 * Represents a notification sent to a user about their lottery results or updates.
 */
public class Notification {

    private String notificationId;
    private String userId;
    private String eventId;
    private String eventTitle;
    private String message;
    private boolean read;
    private String notificationType;

    /**
     * Default constructor for creating an empty Notification object.
     */
    public Notification() {
    }

    /**
     * Gets the unique identifier of the notification.
     * @return
     * Return the notification ID as a String.
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the unique identifier for the notification.
     * @param notificationId
     * This is the unique ID to assign to the notification.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Gets the user ID associated with the notification.
     * @return
     * Return the user ID as a String.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID for the notification.
     * @param userId
     * This is the user ID to assign to the notification.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the event ID associated with the notification.
     * @return
     * Return the event ID as a String.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID for the notification.
     * @param eventId
     * This is the event ID to assign to the notification.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the message content of the notification.
     * @return
     * Return the notification message as a String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message content of the notification.
     * @param message
     * This is the message to assign to the notification.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Checks if the notification has been read.
     * @return
     * Return true if the notification is read, false if not.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status of the notification.
     * @param read
     * This is the read status to assign to the notification.
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Sets the eventTitle for the notification
     * @param eventTitle Title of the event the notification relates to
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the title of the event that the notification relates to
     * @return String title of event
     */
    public String getEventTitle() {
        return this.eventTitle;
    }

    /**
     * Gets the type of notification
     * @return String type of the notification
     */
    public String getNotificationType() {
        return notificationType;
    }

    /**
     * Sets the notification type
     * @param notificationType type of notification
     */
    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }
}
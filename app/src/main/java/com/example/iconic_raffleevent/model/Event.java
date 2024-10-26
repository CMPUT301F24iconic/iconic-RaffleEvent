package com.example.iconic_raffleevent.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an event created by an organizer.
 * Each event is associated with a facility and an organizer, with options such as geolocation requirement,
 * start and end times (including both date and time), maximum attendees, and a poster.
 */
public class Event {
    private String eventId;            // Unique identifier for the event (e.g., UUID)
    private String eventTitle;         // Title of the event
    private boolean geolocationRequired; // Whether geolocation is required for attendance
    private LocalDateTime startTime;   // Event start date and time
    private LocalDateTime endTime;     // Event end date and time
    private int maxAttendees;          // Maximum number of attendees allowed
    private String eventDescription;   // Optional additional description of the event
    private Facility facility;         // Facility where the event is held (must be set)
    private String posterUrl;          // Optional URL or file path for the event poster
    private User organizer;            // Organizer of the event (must be a user with roles USER and ORGANIZER)
    private String qrCode;             // Optional QR code linking to the event details

    /**
     * Constructor that initializes the event with the required fields.
     *
     * @param eventTitle Title of the event.
     * @param geolocationRequired Whether geolocation is required.
     * @param startTime The start date and time of the event.
     * @param endTime The end date and time of the event.
     * @param maxAttendees Maximum number of attendees for the event.
     * @param facility The facility where the event is held.
     * @param organizer The organizer of the event (must have roles USER and ORGANIZER).
     */
    public Event(String eventTitle, boolean geolocationRequired, LocalDateTime startTime, LocalDateTime endTime,
                 int maxAttendees, Facility facility, User organizer) {
        if (eventTitle == null || facility == null || organizer == null || startTime == null || endTime == null) {
            throw new IllegalArgumentException("Event title, facility, organizer, start time, and end time are required.");
        }
        this.eventId = generateEventId();  // Generate a unique ID for the event
        this.eventTitle = eventTitle;
        this.geolocationRequired = geolocationRequired;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxAttendees = maxAttendees;
        this.facility = facility;
        this.organizer = organizer;
        this.qrCode = generateQRCode();  // Automatically generate a QR code for the event
    }

    /**
     * Generates a unique ID for the event. This can be implemented as a UUID or similar.
     *
     * @return The generated unique ID string.
     */
    private String generateEventId() {
        return java.util.UUID.randomUUID().toString();  // Generates a random unique ID
    }

    /**
     * Generates a QR code for the event based on its details.
     *
     * @return The generated QR code string (can be an encoded string or link).
     */
    // Todo: Changes needed (placeholder)
    private String generateQRCode() {
        // Implementation for generating a QR code based on the event details.
        // For simplicity, we'll just return a placeholder string.
        return "QR_CODE_" + eventId;
    }

    // Getters and setters

    public String getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        if (!organizer.hasRole(User.Role.ORGANIZER)) {
            throw new IllegalArgumentException("Organizer must have the ORGANIZER role.");
        }
        this.organizer = organizer;
    }

    // Todo: Changes required (placeholder)
    public String getQrCode() {
        return qrCode;
    }

    // Equals and hashCode for comparing Event objects

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId.equals(event.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }
}
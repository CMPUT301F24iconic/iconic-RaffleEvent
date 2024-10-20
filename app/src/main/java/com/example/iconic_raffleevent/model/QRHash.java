package com.example.iconic_raffleevent.model;

/**
 * Represents the hash data of the generated QR code for an event.
 * This helps in storing and verifying the QR code.
 */
public class QRHash {
    private String eventId;
    private String qrHash;

    // Constructor
    public QRHash(String eventId, String qrHash) {
        this.eventId = eventId;
        this.qrHash = qrHash;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getQrHash() {
        return qrHash;
    }

    public void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }
}
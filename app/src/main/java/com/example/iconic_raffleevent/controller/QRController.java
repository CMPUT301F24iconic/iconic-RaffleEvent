package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.QRHash;

/**
 * QRController handles QR code generation and scanning logic.
 * It interacts with the view for displaying event details after scanning a QR code.
 */
public class QRController {

    private QRHash qrHash;

    public QRController(QRHash qrHash) {
        this.qrHash = qrHash;
    }

    /**
     * Generates a QR code hash for a specific event.
     *
     * @param eventId The event ID for which the QR code is generated.
     * @return The generated QR hash as a string.
     */
    public String generateQRHash(String eventId) {
        // Example: Generate a simple hash using event ID (this can be more complex)
        String generatedHash = eventId.hashCode() + "";
        qrHash.setQrHash(generatedHash);
        return generatedHash;
    }

    /**
     * Verifies the scanned QR code by matching it against the stored QR hash.
     *
     * @param scannedHash The QR hash from the scanned code.
     * @return True if the scanned hash matches the stored QR hash, false otherwise.
     */
    public boolean verifyQRHash(String scannedHash) {
        return qrHash.getQrHash().equals(scannedHash);
    }

    /**
     * Displays event details after scanning a QR code.
     *
     * @param scannedHash The QR hash obtained from scanning.
     * @param listener Listener to handle updating the view with event details.
     */
    public void displayEventDetails(String scannedHash, EventController.EventDetailsListener listener) {
        // Verify the QR hash and fetch event details from the database if valid
        if (verifyQRHash(scannedHash)) {
            // Logic to load event details using EventController and update the view
            listener.onEventDetailsFetched();
        } else {
            listener.onError("Invalid QR Code");
        }
    }
}

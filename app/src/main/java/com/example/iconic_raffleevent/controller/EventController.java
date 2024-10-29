package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;

/**
 * EventController handles the logic related to event creation, modification, and management.
 * It communicates between the event model and the view components.
 */
public class EventController {

    private FirebaseAttendee firebaseAttendee;

    public EventController() {
        this.firebaseAttendee = new FirebaseAttendee();
    }

    public void getEventDetails(String eventId, EventDetailsCallback callback) {
        firebaseAttendee.getEventDetails(eventId, callback);
    }

    public void joinWaitingList(String eventId, String userId, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingList(eventId, userId, callback);
    }

    public void leaveWaitingList(String eventId, String userId, LeaveWaitingListCallback callback) {
        firebaseAttendee.leaveWaitingList(eventId, userId, callback);
    }

    public void acceptEventInvitation(String eventId, String userId, AcceptInvitationCallback callback) {
        firebaseAttendee.acceptEventInvitation(eventId, userId, callback);
    }

    public void declineEventInvitation(String eventId, String userId, DeclineInvitationCallback callback) {
        firebaseAttendee.declineEventInvitation(eventId, userId, callback);
    }

    public void scanQRCode(String qrCodeData, ScanQRCodeCallback callback) {
        // Implement the logic to handle QR code scanning
        // Example:
        if (isValidQRCode(qrCodeData)) {
            String eventId = extractEventIdFromQRCode(qrCodeData);
            callback.onEventFound(eventId);
        } else {
            callback.onError("Invalid QR code");
        }
    }

    private boolean isValidQRCode(String qrCodeData) {
        // Implement the logic to validate the QR code data
        // Example: Check if the QR code data contains a valid event ID
        return qrCodeData != null && qrCodeData.startsWith("event_");
    }

    private String extractEventIdFromQRCode(String qrCodeData) {
        // Implement the logic to extract the event ID from the QR code data
        // Example: Remove the "event_" prefix from the QR code data
        return qrCodeData.substring(6);
    }

    public interface EventDetailsCallback {
        void onEventDetailsFetched(Event event);
        void onError(String message);
    }

    public interface JoinWaitingListCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface LeaveWaitingListCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface AcceptInvitationCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface DeclineInvitationCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface ScanQRCodeCallback {
        void onEventFound(String eventId);
        void onError(String message);
    }
}
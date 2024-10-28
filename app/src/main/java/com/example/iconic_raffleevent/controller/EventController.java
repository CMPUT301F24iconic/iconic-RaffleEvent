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
        firebaseAttendee.scanQRCode(qrCodeData, callback);
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
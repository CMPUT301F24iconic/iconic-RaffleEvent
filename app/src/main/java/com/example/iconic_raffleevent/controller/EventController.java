package com.example.iconic_raffleevent.controller;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

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

    public void getEventMap(String eventId, EventMapCallback callback) {
        firebaseAttendee.getEventMap(eventId, callback);
    }

    public void getAllEvents(EventListCallback callback) {
        firebaseAttendee.getAllEvents(callback);
    }

    public void joinWaitingListWithLocation(String eventId, String userId, GeoPoint userLocation, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingListWithLocation(eventId, userId, userLocation, callback);
    }

    public void joinWaitingListWithoutLocation(String eventId, String userId, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingListWithoutLocation(eventId, userId, callback);
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

    // Aiden Teal
    // Add event to database
    public void saveEventToDatabase(Event event, User user) {
        firebaseAttendee.addEvent(event, user);
    }

    public void uploadEventPoster(Uri imageUri, Event eventObj, UploadEventPosterCallback callback) {
        firebaseAttendee.addEventPoster(imageUri, eventObj, callback);
    }

    public void uploadEventQRCode(Event eventObj, UploadEventQRCodeCallback callback) {
        firebaseAttendee.addEventQRCode(eventObj, callback);
    }

    public interface EventMapCallback {
        void onEventMapFetched(ArrayList<GeoPoint> locations);

        void onError(String message);
    }

    public interface EventDetailsCallback {
        void onEventDetailsFetched(Event event);

        void onError(String message);
    }

    public interface EventListCallback {
        void onEventsFetched(ArrayList<Event> events);

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

    public void scanQRCode(String qrCodeData, String userId, GeoPoint location, ScanQRCodeCallback callback) {
        firebaseAttendee.scanQRCode(qrCodeData, userId, location, callback);
    }

    public void getUserEvents(String userId, EventListCallback callback) {
        firebaseAttendee.getUserEvents(userId, callback);
    }

    public interface UploadEventPosterCallback {
        void onSuccessfulUpload(String posterUrl);

        void onError(String message);
    }

    public interface UploadEventQRCodeCallback {
        void onSuccessfulQRUpload(String qrUrl);

        void onError(String message);
    }

    // Zhiyuan LI - Event management for Admin
    public void deleteEvent(String eventId, DeleteEventCallback callback) {
        firebaseAttendee.deleteEvent(eventId, new FirebaseAttendee.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public interface DeleteEventCallback {
        void onSuccess();

        void onError(String message);
    }

    public interface GetWaitlistCallback {
        void onSuccess();

        void onError(String message);
    }
}
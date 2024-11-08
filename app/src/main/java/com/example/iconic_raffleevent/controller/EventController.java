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

    /**
     * Constructs a new EventController instance and initializes FirebaseAttendee.
     */
    public EventController() {
        this.firebaseAttendee = new FirebaseAttendee();
    }

    /**
     * Fetches event details for a given event ID.
     *
     * @param eventId   The ID of the event.
     * @param callback  The callback to handle the fetched event details or error.
     */
    public void getEventDetails(String eventId, EventDetailsCallback callback) {
        firebaseAttendee.getEventDetails(eventId, callback);
    }

    /**
     * Fetches the map locations associated with a given event.
     *
     * @param eventId   The ID of the event.
     * @param callback  The callback to handle the fetched locations or error.
     */
    public void getEventMap(String eventId, EventMapCallback callback) {
        firebaseAttendee.getEventMap(eventId, callback);
    }

    /**
     * Fetches all available events.
     *
     * @param callback  The callback to handle the fetched events or error.
     */
    public void getAllEvents(EventListCallback callback) {
        firebaseAttendee.getAllEvents(callback);
    }

    /**
     * Adds a user to the event's waiting list with their location.
     *
     * @param eventId        The ID of the event.
     * @param userId         The ID of the user.
     * @param userLocation   The location of the user.
     * @param callback       The callback to handle success or error.
     */
    public void joinWaitingListWithLocation(String eventId, String userId, GeoPoint userLocation, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingListWithLocation(eventId, userId, userLocation, callback);
    }

    /**
     * Adds a user to the event's waiting list without their location.
     *
     * @param eventId   The ID of the event.
     * @param userId    The ID of the user.
     * @param callback  The callback to handle success or error.
     */
    public void joinWaitingListWithoutLocation(String eventId, String userId, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingListWithoutLocation(eventId, userId, callback);
    }

    /**
     * Removes a user from the event's waiting list.
     *
     * @param eventId   The ID of the event.
     * @param userId    The ID of the user.
     * @param callback  The callback to handle success or error.
     */
    public void leaveWaitingList(String eventId, String userId, LeaveWaitingListCallback callback) {
        firebaseAttendee.leaveWaitingList(eventId, userId, callback);
    }

    /**
     * Accepts an event invitation for the user.
     *
     * @param eventId   The ID of the event.
     * @param userId    The ID of the user.
     * @param callback  The callback to handle success or error.
     */
    public void acceptEventInvitation(String eventId, String userId, AcceptInvitationCallback callback) {
        firebaseAttendee.acceptEventInvitation(eventId, userId, callback);
    }

    /**
     * Declines an event invitation for the user.
     *
     * @param eventId   The ID of the event.
     * @param userId    The ID of the user.
     * @param callback  The callback to handle success or error.
     */
    public void declineEventInvitation(String eventId, String userId, DeclineInvitationCallback callback) {
        firebaseAttendee.declineEventInvitation(eventId, userId, callback);
    }

    /**
     * Scans a QR code and processes its data to find the associated event.
     *
     * @param qrCodeData The data encoded in the QR code.
     * @param callback   The callback to handle success or error.
     */
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

    /**
     * Validates whether the QR code data is in a correct format.
     *
     * @param qrCodeData The data from the QR code.
     * @return true if the QR code is valid, false otherwise.
     */
    private boolean isValidQRCode(String qrCodeData) {
        // Implement the logic to validate the QR code data
        // Example: Check if the QR code data contains a valid event ID
        return qrCodeData != null && qrCodeData.startsWith("event_");
    }

    /**
     * Extracts the event ID from the QR code data.
     *
     * @param qrCodeData The data from the QR code.
     * @return The event ID extracted from the QR code data.
     */
    private String extractEventIdFromQRCode(String qrCodeData) {
        // Implement the logic to extract the event ID from the QR code data
        // Example: Remove the "event_" prefix from the QR code data
        return qrCodeData.substring(6);
    }

    /**
     * Saves an event to the database.
     *
     * @param event The event object to save.
     * @param user  The user associated with the event.
     */
    public void saveEventToDatabase(Event event, User user) {
        firebaseAttendee.addEvent(event, user);
    }

    /**
     * Uploads an event poster to Firebase Storage.
     *
     * @param imageUri   The URI of the image to upload.
     * @param eventObj   The event object associated with the poster.
     * @param callback   The callback to handle success or error.
     */
    public void uploadEventPoster(Uri imageUri, Event eventObj, UploadEventPosterCallback callback) {
        firebaseAttendee.addEventPoster(imageUri, eventObj, callback);
    }

    /**
     * Uploads an event QR code to Firebase Storage.
     *
     * @param eventObj   The event object associated with the QR code.
     * @param callback   The callback to handle success or error.
     */
    public void uploadEventQRCode(Event eventObj, UploadEventQRCodeCallback callback) {
        firebaseAttendee.addEventQRCode(eventObj, callback);
    }

    /**
     * Scan a qrcode from the users camera and add them to event
     *
     * @param userId   The ID of the user.
     * @param callback The callback to handle the fetched events or error.
     * @param qrCodeData The qr code data embedded in the qrcode image
     * @param location Entrant location when scanning the QR code
     */
    public void scanQRCode(String qrCodeData, String userId, GeoPoint location, ScanQRCodeCallback callback) {
        firebaseAttendee.scanQRCode(qrCodeData, userId, location, callback);
    }

    /**
     * Fetches the list of events a user is waiting for.
     *
     * @param userId   The ID of the user.
     * @param callback The callback to handle the fetched events or error.
     */
    public void getUserEvents(String userId, EventListCallback callback) {
        firebaseAttendee.getUserWaitingListEvents(userId, callback);
    }

    /**
     * Deletes an event from the system.
     *
     * @param eventId   The ID of the event to delete.
     * @param callback  The callback to handle success or error.
     */
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

    /**
     * Callback interface for event map fetch operations.
     */
    public interface EventMapCallback {
        void onEventMapFetched(ArrayList<GeoPoint> locations);

        void onError(String message);
    }

    /**
     * Callback interface for event details fetch operations.
     */
    public interface EventDetailsCallback {
        void onEventDetailsFetched(Event event);

        void onError(String message);
    }

    /**
     * Callback interface for fetching all events.
     */
    public interface EventListCallback {
        void onEventsFetched(ArrayList<Event> events);

        void onError(String message);
    }

    /**
     * Callback interface for joining a waiting list.
     */
    public interface JoinWaitingListCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for leaving a waiting list.
     */
    public interface LeaveWaitingListCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for accepting an event invitation.
     */
    public interface AcceptInvitationCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for declining an event invitation.
     */

    public interface DeclineInvitationCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for scanning a QR code.
     */
    public interface ScanQRCodeCallback {
        void onEventFound(String eventId);

        void onError(String message);
    }

    /**
     * Callback interface for uploading an event poster.
     */
    public interface UploadEventPosterCallback {
        void onSuccessfulUpload(String posterUrl);

        void onError(String message);
    }

    /**
     * Callback interface for uploading an event QR code.
     */
    public interface UploadEventQRCodeCallback {
        void onSuccessfulQRUpload(String qrUrl);

        void onError(String message);
    }

    /**
     * Callback interface for deleting an event.
     */
    public interface DeleteEventCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for retrieving a waitlist
     */
    public interface GetWaitlistCallback {
        void onSuccess();

        void onError(String message);
    }
}
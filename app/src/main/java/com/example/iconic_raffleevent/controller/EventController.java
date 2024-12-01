package com.example.iconic_raffleevent.controller;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;

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
     * Validates and sets the waiting list limit for an event.
     * Ensures that the limit is greater than or equal to the current waiting list size.
     *
     * @param eventId          The ID of the event.
     * @param waitingListLimit The new waiting list limit.
     * @param callback         Callback to handle success or error.
     */
    public void setWaitingListLimit(String eventId, int waitingListLimit, UpdateEventCallback callback) {
        firebaseAttendee.setWaitingListLimit(eventId, waitingListLimit, callback);
    }

    /**
     * Adds a user to the event's waiting list with their location.
     * Checks if the waiting list limit has been reached before adding the user.
     *
     * @param eventId        The ID of the event.
     * @param user           The user.
     * @param userLocation   The location of the user.
     * @param callback       The callback to handle success or error.
     */
    public void joinWaitingListWithLocation(String eventId, User user, GeoPoint userLocation, JoinWaitingListCallback callback) {
        firebaseAttendee.joinWaitingListWithLocation(eventId, user, userLocation, callback);
    }

    /**
     * Adds a user to the event's waiting list without their location.
     * Checks if the waiting list limit has been reached before adding the user.
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
     * @param event     The event we are removing the user from
     * @param user      The user.
     * @param callback  The callback to handle success or error.
     */
    public void leaveWaitingList(Event event, User user, LeaveWaitingListCallback callback) {
        firebaseAttendee.leaveWaitingList(event, user, callback);
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

    public void updateEventDetails(Event eventObj) {
        firebaseAttendee.updateEventDetails(eventObj);
    }

    /**
     * Scan a qrcode from the users camera and returns the event
     *
     * @param callback The callback to handle the fetched events or error.
     * @param qrCodeData The qr code data embedded in the qrcode image
     */
    public void scanQRCode(String qrCodeData, ScanQRCodeCallback callback) {
        firebaseAttendee.scanQRCode(qrCodeData, callback);
    }

    /**
     * Fetches the list of events a user is waiting for.
     *
     * @param userId   The ID of the user.
     * @param callback The callback to handle the fetched events or error.
     */
    public void getUserWaitingListEvents(String userId, EventListCallback callback) {
        firebaseAttendee.getUserWaitingListEvents(userId, callback);
    }

    /**
     * Fetches the events a user is in a waiting list for or an owner of
     *
     * @param userId The ID of the user
     * @param callback The callback to handle the fetched events or error
     */
    public void getAllUserEvents(String userId, EventListCallback callback) {
        firebaseAttendee.getAllUserEvents(userId, callback);
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

    public void sendNotification(Notification notification, SendDrawNotificationCallback callback) {
        firebaseAttendee.sendDrawNotification(notification, callback);
    }

    /**
     * Callback interface for event map fetch operations.
     */
    public interface EventMapCallback {
        void onEventMapFetched(Map<String, Object> locations);

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
     * Callback interface for updating event details.
     */
    public interface UpdateEventCallback {
        void onSuccess();
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

    public interface SendDrawNotificationCallback {
        void onSuccess();

        void onError(String message);
    }
}
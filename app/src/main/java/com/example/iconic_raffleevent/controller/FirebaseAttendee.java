package com.example.iconic_raffleevent.controller;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Zhiyuan Li - upload image error checking
import android.util.Log;

/**
 * FirebaseAttendee class interacts with Firebase Firestore and Firebase Storage to manage user profiles, events, and notifications.
 * This class provides methods for managing users, events, event registration, waiting lists, notifications, and event media.
 */
public class FirebaseAttendee {
    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;
    private CollectionReference notificationsCollection;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    /**
     * Constructs a FirebaseAttendee instance and initializes references to Firebase Firestore and Firebase Storage.
     */
    public FirebaseAttendee() {
        this.db = FirebaseFirestore.getInstance();
        this.usersCollection = db.collection("User");
        this.eventsCollection = db.collection("Event");
        this.notificationsCollection = db.collection("Notification");
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
    }

    // User-related methods

    /**
     * Updates a user's profile in Firebase Firestore.
     *
     * @param user The user whose profile is to be updated.
     */
    public void updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            return;
        }
        System.out.println("Here 3");
        //Zhiyuan - ensure that updateUser(User user) actually updates the profileImageUrl in Firestore.
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user)  // This will update the user document with all current fields
                .addOnSuccessListener(aVoid -> Log.d("FirebaseAttendee", "User profile updated."))
                .addOnFailureListener(e -> Log.e("FirebaseAttendee", "Error updating profile", e));
    }

    /**
     * Retrieves a user from Firebase Firestore.
     *
     * @param userID   The ID of the user to retrieve.
     * @param callback The callback to handle the result of the operation.
     */
    public void getUser(String userID, UserController.UserFetchCallback callback) {
        DocumentReference userRef = usersCollection.document(userID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                callback.onUserFetched(user);
            } else {
                callback.onUserFetched(null);
            }
        });
    }

    /**
     * Retrieves all users from the Firebase database.
     * This method fetches the list of all users and passes it to the provided callback.
     *
     * @param callback The callback to handle the response with the list of users.
     */
    public void getAllUsers(UserController.UserListCallback callback) {
        // Fetch all users from Firebase and pass to callback
    }

    /**
     * Deletes a user profile from Firebase Firestore.
     *
     * @param userId  The ID of the user to delete.
     * @param callback Callback to handle the result of the operation.
     */
    public void deleteUser(String userId, DeleteUserCallback callback) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onUserDeleted(true))
                .addOnFailureListener(e -> callback.onUserDeleted(false));
    }

    /**
     * Updates the waiting list for a user in Firebase Firestore.
     *
     * @param user The user whose waiting list is to be updated.
     */
    public void updateWaitingList(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("waitingListEventIds", user.getWaitingListEventIds());
    }

    /**
     * Updates the registered events for a user in Firebase Firestore.
     *
     * @param user The user whose registered events are to be updated.
     */
    public void updateRegisteredEvents(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("registeredEventIds", user.getRegisteredEventIds());
    }

    /**
     * Updates the notification preference for a user in Firebase Firestore.
     *
     * @param user The user whose notification preference is to be updated.
     */
    public void updateNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("notificationsEnabled", user.isNotificationsEnabled());
    }

    /**
     * Updates the user's win notification preference in the Firebase database.
     * The preference is stored in the user's document under the field "winNotificationPref".
     *
     * @param user The user whose win notification preference needs to be updated.
     */
    public void updateWinNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("winNotificationPref", user.isWinNotificationPref());
    }

    /**
     * Updates the user's lose notification preference in the Firebase database.
     * The preference is stored in the user's document under the field "loseNotificationPref".
     *
     * @param user The user whose lose notification preference needs to be updated.
     */
    public void updateLoseNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("loseNotificationPref", user.isLoseNotificationPref());
    }

    // Event-related methods

    /**
     * Retrieves the details of a specific event.
     *
     * @param eventId  The ID of the event to retrieve.
     * @param callback The callback to handle the result of the operation.
     */
    public void getEventDetails(String eventId, EventController.EventDetailsCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                callback.onEventDetailsFetched(event);
            } else {
                callback.onError("Failed to fetch event details");
            }
        });
    }

    /**
     * Retrieves all events from Firebase Firestore.
     *
     * @param callback The callback to handle the result of the operation.
     */
    public void getAllEvents(EventController.EventListCallback callback) {
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> events = new ArrayList<>(task.getResult().toObjects(Event.class));
                callback.onEventsFetched(events);
            } else {
                callback.onError("Failed to fetch events");
            }
        });
    }

    /**
     * Adds a new event and generates a QR code for the event.
     *
     * @param event The event to add.
     * @param user The user organizing the event.
     */
    public void addEvent(Event event, User user) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        event.setOrganizerID(user.getUserId());
        eventRef.set(event);
    }

    /**
     * Retrieves the map of entrant locations for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param callback The callback to handle the result of the operation.
     */
    public void getEventMap(String eventId, EventController.EventMapCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    ArrayList<GeoPoint> locations = event.getEntrantLocations();
                    callback.onEventMapFetched(locations);
                }
            } else {
                callback.onError("Failed to fetch event and entrant locations");
            }
        });
    }

    /**
     * Deletes an event with the specified event ID from Firestore.
     *
     * @param eventId  The ID of the event to delete.
     * @param callback Callback interface to notify the success or failure of the deletion.
     */
    public void deleteEvent(String eventId, DeleteEventCallback callback) {
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Updates the event details in the Firebase database.
     * This method updates the entire event document with the provided event object.
     *
     * @param event The event object containing the updated event details.
     */
    public void updateEventDetails(Event event) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        eventRef.set(event);
    }

    /**
     * Adds the user to the event's waiting list along with their location.
     * This method updates both the "waitingList" and "entrantLocations" fields of the event document.
     *
     * @param eventId      The ID of the event to join.
     * @param userId       The ID of the user joining the event.
     * @param userLocation The location of the user.
     * @param callback     The callback interface to notify the success or failure of the operation.
     */
    public void joinWaitingListWithLocation(String eventId, String userId, GeoPoint userLocation, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        WriteBatch writebatch = FirebaseFirestore.getInstance().batch();

        writebatch.update(eventRef, "waitingList", FieldValue.arrayUnion(userId));
        writebatch.update(eventRef, "entrantLocations", FieldValue.arrayUnion(userLocation));

        writebatch.commit().addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to join waiting list"));
    }

    /**
     * Adds a user to the event's waiting list without including their location.
     * This method updates the "waitingList" field of the event document by adding the user ID.
     *
     * @param eventId The ID of the event to which the user is joining the waiting list.
     * @param userId The ID of the user joining the waiting list.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void joinWaitingListWithoutLocation(String eventId, String userId, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to accept invitation"));
    }

    /**
     * Removes a user from the event's waiting list.
     * This method updates the "waitingList" field of the event document by removing the user ID.
     *
     * @param eventId The ID of the event from which the user is leaving the waiting list.
     * @param userId The ID of the user leaving the waiting list.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void leaveWaitingList(String eventId, String userId, EventController.LeaveWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
    }

    /**
     * Accepts an event invitation for a user, adding them to the registered attendees list.
     * This method updates the "registeredAttendees" field of the event document by adding the user ID.
     *
     * @param eventId The ID of the event to which the user is accepting the invitation.
     * @param userId The ID of the user accepting the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void acceptEventInvitation(String eventId, String userId, EventController.AcceptInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("registeredAttendees", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to accept invitation"));
    }

    /**
     * Declines an event invitation for a user, removing them from the waiting list.
     * This method updates the "waitingList" field of the event document by removing the user ID.
     *
     * @param eventId The ID of the event to which the user is declining the invitation.
     * @param userId The ID of the user declining the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void declineEventInvitation(String eventId, String userId, EventController.DeclineInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to decline invitation"));
    }

    /**
     * Scans a QR code to find an event and attempts to add the user to the waiting list.
     * This method retrieves the event associated with the QR code and joins the waiting list.
     *
     * @param qrCodeData The data encoded in the QR code (event identifier).
     * @param callback   The callback interface to notify the success or failure of the operation.
     */
    public void scanQRCode(String qrCodeData, EventController.ScanQRCodeCallback callback) {
        eventsCollection.whereEqualTo("qrCode", qrCodeData)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String eventId = task.getResult().getDocuments().get(0).getId();
                            callback.onEventFound(eventId);
                        } else {
                            callback.onError("Event not found");
                        }
                    } else {
                        if (task.getException() != null) {
                            callback.onError(task.getException().getMessage());
                        } else {
                            callback.onError("Failed to scan QR code");
                        }
                    }
                });
    }


    // Notification-related methods

    /**
     * Retrieves the notifications for a specific user.
     * Queries Firestore for notifications associated with the user.
     *
     * @param userId   The ID of the user whose notifications are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getNotifications(String userId, NotificationController.GetNotificationsCallback callback) {
        notificationsCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = task.getResult().toObjects(Notification.class);
                        callback.onNotificationsFetched(notifications);
                    } else {
                        callback.onError("Failed to fetch notifications");
                    }
                });
    }

    /**
     * Marks a specific notification as read for a user.
     * Updates the "read" field of the notification document in Firestore.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @param callback       The callback interface to notify the success or failure of the operation.
     */
    public void markNotificationAsRead(String notificationId, NotificationController.MarkNotificationAsReadCallback callback) {
        notificationsCollection.document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to mark notification as read"));
    }

    /**
     * Retrieves the list of events for a user's waiting list.
     * The method queries Firestore for all events where the user is present in the waiting list.
     *
     * @param userId   The ID of the user whose waiting list events are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getUserWaitingListEvents(String userId, EventController.EventListCallback callback) {
        eventsCollection.whereArrayContains("waitingList", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        callback.onEventsFetched(new ArrayList<>(events));
                    } else {
                        callback.onError("Failed to fetch events for user waiting list.");
                    }
                });
    }

    /**
     * Retrieves the list of events a user is in a waiting list for or owns
     * The method queries Firestore for all events where the user is present in the waiting list or owns.
     *
     * @param userId   The ID of the user whose waiting list events/owned events are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getAllUserEvents(String userId, EventController.EventListCallback callback) {
        Task<QuerySnapshot> waitingListQuery = eventsCollection.whereArrayContains("waitingList", userId).get();
        Task<QuerySnapshot> ownerQuery = eventsCollection.whereEqualTo("organizerID", userId).get();

        Tasks.whenAllSuccess(waitingListQuery, ownerQuery)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get results from the waiting list query
                        List<Event> waitingListEvents = waitingListQuery.getResult().toObjects(Event.class);

                        // Add them to event list we're returning
                        List<Event> events = new ArrayList<>(waitingListEvents);

                        // Get results from the organizer query
                        List<Event> organizerEvents = ownerQuery.getResult().toObjects(Event.class);

                        // Add organizer events, avoiding duplicates
                        for (Event event : organizerEvents) {
                            if (!events.contains(event)) {
                                events.add(event);
                            }
                        }
                        callback.onEventsFetched(new ArrayList<>(events));
                    } else {
                        callback.onError("Failed to fetch events for user waiting list.");
                    }
                });
    }

    /**
     * Updates the event's invited and declined lists in the Firebase database.
     * This method updates the event document with the provided lists of invited and declined users.
     *
     * @param eventId The ID of the event whose lists are being updated.
     * @param invitedList The list of users invited to the event.
     * @param declinedList The list of users who have declined the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void updateEventLists(String eventId, List<String> invitedList, List<String> declinedList, UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("invitedList", invitedList);
        updates.put("declinedList", declinedList);

        eventsCollection.document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update event lists: " + e.getMessage()));
    }

    /**
     * Uploads the event poster to Firebase Storage and retrieves the download URL.
     * The image is uploaded under the "event_posters" directory in Firebase Storage.
     *
     * @param eventUri URI of the event poster image.
     * @param eventObj The event object associated with the poster.
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void addEventPoster(Uri eventUri, Event eventObj, EventController.UploadEventPosterCallback callback) {
        String eventId = eventObj.getEventId();
        String filePath = "event_posters/" + eventId;
        StorageReference ref = storageReference.child(filePath);

        ref.putFile(eventUri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            callback.onSuccessfulUpload(downloadUrl);
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onError("Unable to upload event poster: " + e.getMessage());
            }
        });
    }

    /**
     * Adds the QR code for an event to Firebase Storage and retrieves the download URL.
     * The QR code is generated based on the event's `qrCode` field and uploaded as a JPEG image.
     *
     * @param eventObj The event object containing the QR code data.
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void addEventQRCode(Event eventObj, EventController.UploadEventQRCodeCallback callback) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap eventQrCode = barcodeEncoder.encodeBitmap(eventObj.getQrCode(), BarcodeFormat.QR_CODE, 400, 400);
            String filePath = "event_qrcodes/" + eventObj.getQrCode();
            StorageReference ref = storageReference.child(filePath);

            // Convert eventQrCode into bytes array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            eventQrCode.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] image = baos.toByteArray();

            ref.putBytes(image).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                callback.onSuccessfulQRUpload(downloadUrl);
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onError("Unable to upload event QR code: " + e.getMessage());
                }
            });

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Callback interface for handling the result of fetching event details.
     */
    public interface EventDetailsCallback {
        void onEventDetailsFetched(Event event);
        void onError(String message);
    }

    /**
     * Callback interface for handling the result of fetching user details.
     */
    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    /**
     * Callback interface for handling success or failure of an update operation.
     */
    public interface UpdateCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Callback interface for handling success or failure of an event deletion operation.
     */
    public interface DeleteEventCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Callback interface for handling the result of a user deletion operation.
     */
    public interface DeleteUserCallback {
        void onUserDeleted(boolean success);
    }
}

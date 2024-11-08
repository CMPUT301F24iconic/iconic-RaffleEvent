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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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

public class FirebaseAttendee {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;
    private CollectionReference notificationsCollection;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public FirebaseAttendee() {
        this.db = FirebaseFirestore.getInstance();
        this.usersCollection = db.collection("User");
        this.eventsCollection = db.collection("Event");
        this.notificationsCollection = db.collection("Notification");
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
    }

    // User-related methods
    public void updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            return;
        }
    //Zhiyuan - ensure that updateUser(User user) actually updates the profileImageUrl in Firestore.
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user)  // This will update the user document with all current fields
                .addOnSuccessListener(aVoid -> Log.d("FirebaseAttendee", "User profile updated."))
                .addOnFailureListener(e -> Log.e("FirebaseAttendee", "Error updating profile", e));
    }

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
    // Retrieve all users
    public void getAllUsers(UserController.UserListCallback callback) {
        // Fetch all users from Firebase and pass to callback
    }

    // Zhiyuan - Delete a user profile
    // Method to delete a user by userId
    public void deleteUser(String userId, DeleteUserCallback callback) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onUserDeleted(true))
                .addOnFailureListener(e -> callback.onUserDeleted(false));
    }

    // Callback for deletion success or failure
    public interface DeleteUserCallback {
        void onUserDeleted(boolean success);
    }

    public void updateWaitingList(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("waitingListEventIds", user.getWaitingListEventIds());
    }

    public void updateRegisteredEvents(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("registeredEventIds", user.getRegisteredEventIds());
    }

    public void updateNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("notificationsEnabled", user.isNotificationsEnabled());
    }

    // Manh Duong Hoang
    public void updateWinNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("winNotificationPref", user.isWinNotificationPref());
    }

    public void updateLoseNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("loseNotificationPref", user.isLoseNotificationPref());
    }
    //

    // Event-related methods
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

    // Add event and generate a new qrcode
    public void addEvent(Event event, User user) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        event.setOrganizerID(user.getUserId());
        eventRef.set(event);
    }

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

    public interface DeleteEventCallback {
        void onSuccess();

        void onError(String message);
    }
    public void updateEventDetails(Event event) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        eventRef.set(event);
    }

    public void joinWaitingListWithLocation(String eventId, String userId, GeoPoint userLocation, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        WriteBatch writebatch = FirebaseFirestore.getInstance().batch();

        writebatch.update(eventRef, "waitingList", FieldValue.arrayUnion(userId));
        writebatch.update(eventRef, "entrantLocations", FieldValue.arrayUnion(userLocation));

        writebatch.commit().addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to join waiting list"));
    }

    public void joinWaitingListWithoutLocation(String eventId, String userId, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to accept invitation"));
    }

    public void leaveWaitingList(String eventId, String userId, EventController.LeaveWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
    }

    public void acceptEventInvitation(String eventId, String userId, EventController.AcceptInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("registeredAttendees", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to accept invitation"));
    }

    public void declineEventInvitation(String eventId, String userId, EventController.DeclineInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to decline invitation"));
    }

    public void scanQRCode(String qrCodeData, String userId, GeoPoint userLocation, EventController.ScanQRCodeCallback callback) {
        eventsCollection.whereEqualTo("qrCode", qrCodeData)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String eventId = task.getResult().getDocuments().get(0).getId();
                            joinWaitingListWithLocation(eventId, userId, userLocation, new EventController.JoinWaitingListCallback() {
                                @Override
                                public void onSuccess() {
                                    callback.onEventFound(eventId);
                                }

                                @Override
                                public void onError(String message) {
                                    callback.onError(message);
                                }
                            });
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

    public void markNotificationAsRead(String notificationId, NotificationController.MarkNotificationAsReadCallback callback) {
        notificationsCollection.document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to mark notification as read"));
    }

    public void getUserEvents(String userId, EventController.EventListCallback callback) {
        // Query for events where the user is in the waiting list or the organizer
        eventsCollection
                .whereArrayContains("waitingList", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = new ArrayList<>(task.getResult().toObjects(Event.class));

                        // Now fetch events where the user is the organizer and combine the results
                        eventsCollection
                                .whereEqualTo("organizerID", userId)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        List<Event> organizerEvents = task2.getResult().toObjects(Event.class);
                                        events.addAll(organizerEvents); // Combine the lists
                                        callback.onEventsFetched(new ArrayList<>(events));
                                    } else {
                                        callback.onError("Failed to fetch organizer events.");
                                    }
                                });
                    } else {
                        callback.onError("Failed to fetch events for user.");
                    }
                });
    }

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

    // EventDetailsCallback to fetch event details
    public interface EventDetailsCallback {
        void onEventDetailsFetched(Event event);
        void onError(String message);
    }

    // UserFetchCallback to fetch user details
    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    public void updateEventLists(String eventId, List<String> invitedList, List<String> declinedList, UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("invitedList", invitedList);
        updates.put("declinedList", declinedList);

        eventsCollection.document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update event lists: " + e.getMessage()));
    }

    public interface UpdateCallback {
        void onSuccess();
        void onError(String message);
    }
}

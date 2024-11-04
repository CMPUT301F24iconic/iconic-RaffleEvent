package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAttendee {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;
    private CollectionReference notificationsCollection;

    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("User");
        eventsCollection = db.collection("Event");
        notificationsCollection = db.collection("Notification");
    }

    // User-related methods
    public void updateUser(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user);
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
        String hashed_qr_data = "event_" + event.getEventId();
        // Code to generate a bitmap qr code
        event.setQrCode(hashed_qr_data);
        event.setBitmap();
        event.setOrganizerID(user.getUserId());
        eventRef.set(event);
    }

    public void updateEventDetails(Event event) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        eventRef.set(event);
    }

    public void joinWaitingList(String eventId, String userId, GeoPoint userLocation, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        WriteBatch writebatch = FirebaseFirestore.getInstance().batch();

        writebatch.update(eventRef, "waitingList", FieldValue.arrayUnion(userId));
        writebatch.update(eventRef, "entrantLocations", FieldValue.arrayUnion(userLocation));

        writebatch.commit().addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to join waiting list"));
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
                            joinWaitingList(eventId, userId, userLocation, new EventController.JoinWaitingListCallback() {
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
                        callback.onError("Failed to scan QR code");
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
}
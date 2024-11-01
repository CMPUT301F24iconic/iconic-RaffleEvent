package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirebaseAttendee {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;
    private CollectionReference notificationsCollection;

    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("User");
        eventsCollection = db.collection("events");
        notificationsCollection = db.collection("notifications");
    }

    // User-related methods
    public void updateUser(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user);
    }

    public void getUser(String userID, OnUserRetrievedListener callback) {
        DocumentReference userRef = usersCollection.document(userID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                callback.onUserRetrieved(user);
            } else {
                callback.onUserRetrieved(null);
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

    public void joinWaitingList(String eventId, String userId, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
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

    public void scanQRCode(String qrCodeData, EventController.ScanQRCodeCallback callback) {
        // Implement the logic to handle QR code scanning and retrieving the associated event from Firestore
        // Example:
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
package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationController {

    private FirebaseFirestore db;
    private CollectionReference notificationsCollection;
    private FirebaseAttendee firebaseAttendee;

    public NotificationController() {
        db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("notifications");
        firebaseAttendee = new FirebaseAttendee();
    }

    public void getNotifications(String userId, GetNotificationsCallback callback) {
        firebaseAttendee.getNotifications(userId, callback);
    }

    public void markNotificationAsRead(String notificationId, MarkNotificationAsReadCallback callback) {
        notificationsCollection.document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface GetNotificationsCallback {
        void onNotificationsFetched(List<Notification> notifications);
        void onError(String message);
    }

    public interface MarkNotificationAsReadCallback {
        void onSuccess();
        void onError(String message);
    }
}
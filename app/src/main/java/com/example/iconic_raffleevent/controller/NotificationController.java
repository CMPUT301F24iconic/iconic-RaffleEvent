package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Notification;
import java.util.List;

public class NotificationController {

    private FirebaseAttendee firebaseAttendee;

    public NotificationController() {
        this.firebaseAttendee = new FirebaseAttendee();
    }

    public void getNotifications(String userId, GetNotificationsCallback callback) {
        // Implement the logic to fetch notifications from Firebase
        // Example:
        firebaseAttendee.getNotificationsForUser(userId, new FirebaseAttendee.GetNotificationsCallback() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                callback.onNotificationsFetched(notifications);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void markNotificationAsRead(String notificationId, MarkNotificationAsReadCallback callback) {
        // Implement the logic to mark a notification as read in Firebase
        // Example:
        firebaseAttendee.markNotificationAsRead(notificationId, new FirebaseAttendee.MarkNotificationAsReadCallback() {
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

    public interface GetNotificationsCallback {
        void onNotificationsFetched(List<Notification> notifications);
        void onError(String message);
    }

    public interface MarkNotificationAsReadCallback {
        void onSuccess();
        void onError(String message);
    }
}
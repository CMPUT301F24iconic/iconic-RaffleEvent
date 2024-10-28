package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Notification;
import java.util.List;

public class NotificationController {

    private FirebaseAttendee firebaseAttendee;

    public NotificationController() {
        this.firebaseAttendee = new FirebaseAttendee();
    }

    public void getNotifications(String userId, GetNotificationsCallback callback) {
        firebaseAttendee.getNotifications(userId, callback);
    }

    public void markNotificationAsRead(String notificationId, MarkNotificationAsReadCallback callback) {
        firebaseAttendee.markNotificationAsRead(notificationId, callback);
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
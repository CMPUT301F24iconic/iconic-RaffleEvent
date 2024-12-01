package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Controller class responsible for managing notifications for users.
 * Provides methods to retrieve notifications and mark them as read.
 */
public class NotificationController {
    public FirebaseFirestore db;
    public CollectionReference notificationsCollection;
    public FirebaseAttendee firebaseAttendee;

    /**
     * Constructs a new NotificationController and initializes the Firebase Firestore instance.
     * Also sets up the reference to the "Notification" collection in Firestore.
     */
    public NotificationController() {
        db = FirebaseFirestore.getInstance();
        notificationsCollection = db.collection("Notification");
        firebaseAttendee = new FirebaseAttendee();
    }

    /**
     * Uploads a new notification to the Notification collection in firebase
     * @param notification Notification to be uploaded
     * @param callback The callback interface to handle the uploaded notifications or error
     */
    public void sendNotification(Notification notification, NotificationController.SendNotificationCallback callback) {
        firebaseAttendee.sendGeneralNotification(notification, callback);
    }

    /**
     * Retrieves the notifications for a specified user.
     *
     * @param userId   The ID of the user for whom to fetch the notifications.
     * @param callback The callback interface to handle the fetched notifications or error.
     */
    public void getNotifications(String userId, GetNotificationsCallback callback) {
        firebaseAttendee.getNotifications(userId, callback);
    }

    /**
     * Marks a notification as read by updating its "read" status in Firestore.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @param callback       The callback interface to handle the success or failure of the operation.
     */
    public void markNotificationAsRead(String notificationId, MarkNotificationAsReadCallback callback) {
        notificationsCollection.document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Callback interface for retrieving notifications for a user.
     */
    public interface GetNotificationsCallback {
        /**
         * Callback which contains a list of notifications
         * @param notifications List of notifications
         */
        void onNotificationsFetched(List<Notification> notifications);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for marking a notification as read.
     */
    public interface MarkNotificationAsReadCallback {
        /**
         * Callback which is called upon successful mark notification as read operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    public interface SendNotificationCallback {
        /**
         * Callback which is called upon successful send notification operation
         */
        void onSuccess(Boolean success);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }
}

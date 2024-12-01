package com.example.iconic_raffleevent.view;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.NotificationController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility class for creating and sending notifications. Can be called from multiple activities,
 * allowing organizers to send a custom notification to multiple users.
 */
public class NotificationUtils {

    /**
     * Generates a dialog allowing an organizer to type and send a custom notification
     * @param context context dialog is being opened in
     * @param users list of users to send the notification to
     * @param event event related to notification
     */
    public static void showNotificationDialog(
            Context context,
            ArrayList<User> users,
            Event event
    ) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_create_notification, null);

        TextInputLayout notificationMessagelayout = dialogView.findViewById(R.id.notification_input_layout);
        TextInputEditText notificationMessage = dialogView.findViewById(R.id.notification_input);
        ImageButton cancel = dialogView.findViewById(R.id.cancel_button);
        Button sendNoti = dialogView.findViewById(R.id.send_noti);

        // Build the dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set button listeners
        cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        sendNoti.setOnClickListener(v -> {
            // Send notification, when callback occurs, dismiss dialog
            Boolean validNotification = validateNotification(notificationMessagelayout, notificationMessage);
            if (validNotification) {
                // sendNotification
                sendNotification(context, users, event, notificationMessage.getText().toString(), dialog);
            }
        });

        dialog.show();
    }

    /**
     * Ensure notification message is not empty
     * @param notificationMessageLayout layout for the notification message
     * @param notificationMessage notification message
     * @return True if notification message is valid, False otherwise
     */
    public static Boolean validateNotification(TextInputLayout notificationMessageLayout, TextInputEditText notificationMessage) {
        // Check if notification is valid
        if (notificationMessage == null || notificationMessage.getText().length() == 0) {
            notificationMessageLayout.setError("Notification cannot be empty");

            // message not valid
            return Boolean.FALSE;
        }

        // message valid
        return Boolean.TRUE;
    }

    /**
     * Sends a notification to the specified list of users
     *
     * @param context context dialog exists in
     * @param users lists of users notification is being sent too
     * @param event event notification is related to
     * @param message notification message
     * @param dialog notification dialog
     */
    public static void sendNotification(
            Context context,
            ArrayList<User> users,
            Event event,
            String message,
            AlertDialog dialog) {

        // Create necessary controllers
        NotificationController notificationController = new NotificationController();

        AtomicBoolean allSentSuccessfully = new AtomicBoolean(true);
        AtomicInteger pendingNotifications = new AtomicInteger(users.size());

        // For each user in the list, generate a new notification and add it to the notifications database
        for (User user : users) {
            Notification selectedNotification = new Notification();
            selectedNotification.setNotificationType("General");
            selectedNotification.setEventTitle(event.getEventTitle());
            selectedNotification.setEventId(event.getEventId());
            selectedNotification.setUserId(user.getUserId());
            selectedNotification.setMessage(message);

            notificationController.sendNotification(selectedNotification, new NotificationController.SendNotificationCallback() {
                @Override
                public void onSuccess(Boolean success) {
                    // Successfully drew applicants
                    if (!success) {
                        allSentSuccessfully.set(false);
                    }
                    checkCompletion();
                }
                @Override
                public void onError(String message) {
                    allSentSuccessfully.set(false);
                    checkCompletion();
                }

                /**
                 * Check to see if all notifications were sent successfully
                 */
                private void checkCompletion() {
                    if (pendingNotifications.decrementAndGet() == 0) {
                        if (allSentSuccessfully.get()) {
                            Toast.makeText(context, "All notifications sent successfully.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "Some notifications failed to send.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }
}

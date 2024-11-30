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

public class NotificationUtils {

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

    public static Boolean validateNotification(TextInputLayout notificationMessageLayout, TextInputEditText notificationMessage) {
        // Check if notification is valid
        if (notificationMessage == null || notificationMessage.getText().length() == 0) {
            notificationMessageLayout.setError("Notification cannot be empty");

            // Return false to indicate notification is not valid
            return Boolean.FALSE;
        }

        // Return true to indicate notification is valid
        return Boolean.TRUE;
    }

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

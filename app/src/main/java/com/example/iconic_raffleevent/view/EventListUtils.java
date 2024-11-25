package com.example.iconic_raffleevent.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.AvatarGenerator;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

import java.util.List;

public class EventListUtils {

    /**
     * Shows the user details dialog with options to cancel or delete the user.
     *
     * @param context         The context of the calling activity.
     * @param user            The user whose details need to be shown.
     * @param event           The event object containing the lists.
     * @param firebaseAttendee The FirebaseAttendee controller for Firestore operations.
     * @param callback        A callback to execute after successful deletion (e.g., refresh the UI).
     */
    public static void showUserDetailsDialog(
            Context context,
            User user,
            Event event,
            FirebaseAttendee firebaseAttendee,
            Runnable callback
    ) {
        // Inflate the custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_user_details, null);

        // Initialize UI components
        ImageView profileImageView = dialogView.findViewById(R.id.userProfileImage);
        TextView userNameTextView = dialogView.findViewById(R.id.userNameTextView);
        TextView userEmailTextView = dialogView.findViewById(R.id.userEmailTextView);
        TextView userPhoneTextView = dialogView.findViewById(R.id.userPhoneTextView);
        TextView warningMessage = dialogView.findViewById(R.id.warningMessage);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button deleteButton = dialogView.findViewById(R.id.deleteButton);

        // Set user details
        userNameTextView.setText(user.getName());
        userEmailTextView.setText(user.getEmail());
        if (user.getPhoneNo() != null && !user.getPhoneNo().isEmpty()) {
            userPhoneTextView.setText(user.getPhoneNo());
            userPhoneTextView.setVisibility(View.VISIBLE);
        } else {
            userPhoneTextView.setVisibility(View.GONE);
        }

        // Load profile image or initials avatar
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .into(profileImageView);
        } else {
            Bitmap avatarBitmap = AvatarGenerator.generateAvatar(user.getName(), 200);
            profileImageView.setImageBitmap(avatarBitmap);
        }

        // Set warning message
        warningMessage.setText("Warning: Deleting this user will remove them from all lists. This action cannot be undone.");

        // Build the dialog
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Handle Cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Handle Delete button
        deleteButton.setOnClickListener(v -> {
            deleteUserFromLists(context, event, user.getUserId(), firebaseAttendee, () -> {
                callback.run();
                dialog.dismiss();
            });
        });

        // Show the dialog
        dialog.show();
    }

    /**
     * Deletes a user from all event lists: waitingList, invitedList, and registeredAttendees.
     *
     * @param context         The context of the calling activity.
     * @param event           The event object containing the lists.
     * @param userId          The ID of the user to delete.
     * @param firebaseAttendee The FirebaseAttendee controller for Firestore operations.
     * @param callback        A callback to execute after successful deletion (e.g., refresh the UI).
     */
    public static void deleteUserFromLists(
            Context context,
            Event event,
            String userId,
            FirebaseAttendee firebaseAttendee,
            Runnable callback
    ) {
        if (event == null) {
            Toast.makeText(context, "Event data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check and remove the user from each list
        List<String> waitingList = event.getWaitingList();
        List<String> invitedList = event.getInvitedList();
        List<String> registeredAttendees = event.getRegisteredAttendees();

        boolean removedFromWaiting = waitingList != null && waitingList.remove(userId);
        boolean removedFromInvited = invitedList != null && invitedList.remove(userId);
        boolean removedFromRegistered = registeredAttendees != null && registeredAttendees.remove(userId);

        // If the user was found in any list, update Firestore
        if (removedFromWaiting || removedFromInvited || removedFromRegistered) {
            firebaseAttendee.updateEventLists(event.getEventId(), invitedList, waitingList, registeredAttendees, new FirebaseAttendee.UpdateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "User successfully deleted from all lists.", Toast.LENGTH_SHORT).show();
                    callback.run(); // Refresh the UI
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(context, "Failed to update event lists: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "User not found in any list.", Toast.LENGTH_SHORT).show();
        }
    }
}

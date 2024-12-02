package com.example.iconic_raffleevent.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.AvatarGenerator;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * ConfirmedListActivity displays a list of users who have confirmed their attendance for an event.
 */
public class ConfirmedListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;
    private Event eventObj;

    private ArrayList<User> usersObj;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;
    private Button notificationButton;


    /**
     * Called when the activity is first created.
     * Initializes the UI elements and sets up navigation logic.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_list);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);
        notificationButton = findViewById(R.id.sendNotification);

        // Initialize UI elements
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FirebaseAttendee controller
        firebaseAttendee = new FirebaseAttendee();

        // Get the event ID passed from the previous activity
        eventId = getIntent().getStringExtra("eventId");
        usersObj = new ArrayList<>();

        loadEventDetails();

        // Initialize adapter and set it to RecyclerView
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setAdapter(userAdapter);

        // Set item click listener for the user list
        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(User user) {
                // Handle user details dialog
                com.example.iconic_raffleevent.view.EventListUtils.showUserDetailsDialog(
                        ConfirmedListActivity.this,
                        user,
                        eventObj,
                        firebaseAttendee,
                        ConfirmedListActivity.this::refreshConfirmedList
                );
            }

            @Override
            public void onProfileImageClick(User user) {
                // Show profile image dialog
                showProfileImageDialog(user);
            }
        });


        // Fetch and display waiting list
        loadConfirmedList();

        backButton.setOnClickListener(v -> finish());

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmedListActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmedListActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(ConfirmedListActivity.this, ProfileActivity.class));
        });

        notificationButton.setOnClickListener(v -> {
            if (usersObj == null || usersObj.isEmpty()) {
                // No users to send notification to
                Toast.makeText(ConfirmedListActivity.this, "No users to send notification to", Toast.LENGTH_SHORT).show();
            } else {
                com.example.iconic_raffleevent.view.NotificationUtils.showNotificationDialog(
                        ConfirmedListActivity.this,
                        usersObj,
                        eventObj
                );
            }
        });
    }

    /**
     * Refreshes the confirmed list by clearing the adapter and reloading the list.
     */
    private void refreshConfirmedList() {
        // Clear the user adapter to reset the displayed list
        userAdapter.clearUsers();
        // Reload the waiting list from Firestore or backend
        loadConfirmedList();
    }


    /**
     * Loads the list of users who have confirmed attendance for the event.
     * Fetches event details using the event ID and retrieves the confirmed list.
     */
    private void loadConfirmedList() {
        firebaseAttendee.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                List<String> confirmedListIds = event.getRegisteredAttendees();
                fetchUsersFromConfirmedList(confirmedListIds);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ConfirmedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches user details for each user ID in the confirmed list and updates the RecyclerView.
     *
     * @param userIds List of user IDs who confirmed attendance for the event.
     */
    private void fetchUsersFromConfirmedList(List<String> userIds) {
        if (!userIds.isEmpty()) {
            for (String userId : userIds) {
                firebaseAttendee.getUser(userId, new UserController.UserFetchCallback() {
                    @Override
                    public void onUserFetched(User user) {
                        if (user != null) {
                            userAdapter.addUser(user);
                            userAdapter.notifyDataSetChanged();
                            usersObj.add(user);
                        } else {
                            Toast.makeText(ConfirmedListActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(ConfirmedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    /**
     * Loads the event details, including waiting list information and capacity.
     * Stores the event object in the activity.
     */
    private void loadEventDetails() {
        firebaseAttendee.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                eventObj = event;
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ConfirmedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Create a dialog showing the profile image for a specific user
     * @param user User object of the profile image being displayed in dialog
     */
    private void showProfileImageDialog(User user) {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_zoomed_profile_photo, null);

        // Find ImageView in the dialog layout
        ImageView zoomedProfileImageView = dialogView.findViewById(R.id.zoomedProfileImageView);

        // Set the profile image
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .into(zoomedProfileImageView);
        } else {
            Bitmap avatarBitmap = AvatarGenerator.generateAvatar(user.getName(), 200);
            zoomedProfileImageView.setImageBitmap(avatarBitmap);
        }

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogTheme);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * DeclinedListActivity displays a list of users who have declined an event invitation.
 */
public class DeclinedListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    public FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;

    private ArrayList<User> usersObj;
    private Event eventObj;

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
        setContentView(R.layout.activity_declined_list);

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

        loadEventDetails();

        // Initialize lists
        usersObj = new ArrayList<>();

        // Initialize adapter and set it to RecyclerView
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setAdapter(userAdapter);

        // Fetch and display waiting list
        loadDeclinedList();

        backButton.setOnClickListener(v -> finish());

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(DeclinedListActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(DeclinedListActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(DeclinedListActivity.this, ProfileActivity.class));
        });

        notificationButton.setOnClickListener(v -> {
            if (usersObj == null || usersObj.isEmpty()) {
                // No users to send notification to
                Toast.makeText(DeclinedListActivity.this, "No users to send notification to", Toast.LENGTH_SHORT).show();

            } else {
                com.example.iconic_raffleevent.view.NotificationUtils.showNotificationDialog(
                        DeclinedListActivity.this,
                        usersObj,
                        eventObj
                );
            }
        });
    }

    /**
     * Loads the list of users who have declined the event.
     * Fetches event details using the event ID and retrieves the declined list.
     */
    public void loadDeclinedList() {
        firebaseAttendee.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                List<String> declinedListIds = event.getDeclinedList();
                fetchUsersFromDeclinedList(declinedListIds);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(DeclinedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches user details for each user ID in the declined list and updates the RecyclerView.
     *
     * @param userIds List of user IDs who declined the event.
     */
    public void fetchUsersFromDeclinedList(List<String> userIds) {
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
                            Toast.makeText(DeclinedListActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(DeclinedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(DeclinedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
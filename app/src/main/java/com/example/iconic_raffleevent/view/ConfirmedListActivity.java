package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
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
 * ConfirmedListActivity displays a list of users who have confirmed their attendance for an event.
 */
public class ConfirmedListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;
    private Event eventObj;

    // Navigation UI
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;

    // Top Nav bar
//    private ImageButton notificationButton;

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

        // Initialize DrawerLayout and NavigationView
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.navigation_view);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
//        notificationButton = findViewById(R.id.notification_icon);
        backButton = findViewById(R.id.back_button);

//        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        // Initialize UI elements
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FirebaseAttendee controller
        firebaseAttendee = new FirebaseAttendee();

        // Get the event ID passed from the previous activity
        eventId = getIntent().getStringExtra("eventId");

        loadEventDetails();

        // Initialize adapter and set it to RecyclerView
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setAdapter(userAdapter);

        // Set item click listener for the user list
        userAdapter.setOnItemClickListener(user -> {
            com.example.iconic_raffleevent.view.EventListUtils.showUserDetailsDialog(
                    ConfirmedListActivity.this,
                    user,
                    eventObj,
                    firebaseAttendee,
                    this::refreshConfirmedList
            );
        });

        // Fetch and display waiting list
        loadConfirmedList();

        // Top nav bar
//        notificationButton.setOnClickListener(v ->
//                startActivity(new Intent(ConfirmedListActivity.this, NotificationsActivity.class))
//        );
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
        for (String userId : userIds) {
            firebaseAttendee.getUser(userId, new UserController.UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    if (user != null) {
                        userAdapter.addUser(user);
                        userAdapter.notifyDataSetChanged();
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

    public void setFirebaseAttendee(FirebaseAttendee firebaseAttendee) {
        this.firebaseAttendee = firebaseAttendee;
    }

}
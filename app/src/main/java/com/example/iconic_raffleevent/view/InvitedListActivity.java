package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
 * InvitedListActivity displays a list of users who have been invited to an event.
 */
public class InvitedListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;

    // Navigation UI
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
//    private ImageButton menuButton;

    // Top Nav bar
    private ImageButton notificationButton;

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
        setContentView(R.layout.activity_invited_list);

        // Initialize DrawerLayout and NavigationView
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.navigation_view);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        notificationButton = findViewById(R.id.notification_icon);
//        menuButton = findViewById(R.id.menu_button);

//        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        // Initialize UI elements
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize FirebaseAttendee controller
        firebaseAttendee = new FirebaseAttendee();

        // Get the event ID passed from the previous activity
        eventId = getIntent().getStringExtra("eventId");

        // Initialize adapter and set it to RecyclerView
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setAdapter(userAdapter);

        // Fetch and display waiting list
        loadInvitedList();

        // Top nav bar
        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(InvitedListActivity.this, NotificationsActivity.class))
        );
//        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(InvitedListActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(InvitedListActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(InvitedListActivity.this, ProfileActivity.class));
        });
    }

    /**
     * Loads the list of users who have been invited to the event.
     * Fetches event details using the event ID and retrieves the invited list.
     */
    private void loadInvitedList() {
        firebaseAttendee.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                List<String> invitedListIds = event.getInvitedList();
                fetchUsersFromInvitedList(invitedListIds);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(InvitedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches user details for each user ID in the invited list and updates the RecyclerView.
     *
     * @param userIds List of user IDs who have been invited to the event.
     */
    private void fetchUsersFromInvitedList(List<String> userIds) {
        for (String userId : userIds) {
            firebaseAttendee.getUser(userId, new UserController.UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    if (user != null) {
                        userAdapter.addUser(user);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(InvitedListActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(InvitedListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
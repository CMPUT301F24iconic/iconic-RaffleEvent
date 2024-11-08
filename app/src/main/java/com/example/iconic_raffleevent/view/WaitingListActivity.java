package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import java.util.Collections;
import java.util.List;

/**
 * Activity that displays a waiting list of users for a specified event and allows the organizer
 * to randomly select attendees based on event capacity.
 */
public class WaitingListActivity extends AppCompatActivity {
    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;
    private Event eventObj;
    private Button sampleAttendeesButton;

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
     * Called when the activity is starting. Sets up the layout, initializes components, and
     * begins loading event details and the waiting list of users.
     *
     * @param savedInstanceState the saved state of the activity if it was previously terminated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

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

        sampleAttendeesButton = findViewById(R.id.sampleAttendeesButton);

        // Initialize FirebaseAttendee controller
        firebaseAttendee = new FirebaseAttendee();

        // Get the event ID passed from the previous activity
        eventId = getIntent().getStringExtra("eventId");

        loadEventDetails();

        // Initialize adapter and set it to RecyclerView
        userAdapter = new UserAdapter(new ArrayList<>());
        userRecyclerView.setAdapter(userAdapter);

        // Fetch and display waiting list
        loadWaitingList();

        // Set listener for sampling attendees
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());

        // Top nav bar
        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(WaitingListActivity.this, NotificationsActivity.class))
        );
//        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(WaitingListActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(WaitingListActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(WaitingListActivity.this, ProfileActivity.class));
        });
    }

    /**
     * Loads the waiting list for the event by fetching the event details.
     * Retrieves the list of user IDs on the waiting list and starts the process of fetching each user's details.
     */
    public void loadWaitingList() {
        firebaseAttendee.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                List<String> waitingListIds = event.getWaitingList();
                fetchUsersFromWaitingList(waitingListIds);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(WaitingListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches user details for each user ID in the waiting list and updates the RecyclerView adapter.
     * Displays a toast message if user data fails to load.
     *
     * @param userIds the list of user IDs on the event's waiting list.
     */
    private void fetchUsersFromWaitingList(List<String> userIds) {
        for (String userId : userIds) {
            firebaseAttendee.getUser(userId, new UserController.UserFetchCallback() {
                @Override
                public void onUserFetched(User user) {
                    if (user != null) {
                        userAdapter.addUser(user);
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(WaitingListActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(WaitingListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                // Now we have the event object with waiting list and max attendees
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WaitingListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Samples attendees from the waiting list based on event capacity, randomly selects users to invite,
     * and updates the event's invited and declined lists accordingly.
     * Provides feedback to the organizer on the number of invited and declined attendees.
     */
    private void sampleAttendees() {
        if (eventObj == null) {
            Toast.makeText(this, "Event data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> waitingList = eventObj.getWaitingList();
        int maxAttendees = eventObj.getMaxAttendees();

        if (waitingList == null || waitingList.isEmpty()) {
            Toast.makeText(this, "No users in waiting list.", Toast.LENGTH_SHORT).show();
            return;
        }

        int sampleSize = Math.min(maxAttendees, waitingList.size());
        List<String> invitedList = new ArrayList<>();
        List<String> declinedList = new ArrayList<>();

        // Shuffle the waiting list for randomness
        Collections.shuffle(waitingList);

        // Select sampleSize number of attendees as invited
        invitedList.addAll(waitingList.subList(0, sampleSize));

        // If there are more in waiting list than maxAttendees, add the rest to declinedList
        if (waitingList.size() > sampleSize) {
            declinedList.addAll(waitingList.subList(sampleSize, waitingList.size()));
        }

        // Update the event object
        eventObj.setInvitedList(new ArrayList<>(invitedList));
        eventObj.setDeclinedList(new ArrayList<>(declinedList));

        // Update Firestore with the invited and declined lists
        updateEventListsInFirestore(invitedList, declinedList);

        // Provide feedback to the organizer
        Toast.makeText(this, "Invited " + invitedList.size() + " attendees. Declined " + declinedList.size() + " attendees.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the event lists (invited and declined) in Firestore for the current event.
     * Provides a success or error message based on the update result.
     *
     * @param invitedList the list of invited user IDs.
     * @param declinedList the list of declined user IDs.
     */
    private void updateEventListsInFirestore(List<String> invitedList, List<String> declinedList) {
        firebaseAttendee.updateEventLists(eventObj.getEventId(), invitedList, declinedList, new FirebaseAttendee.UpdateCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(WaitingListActivity.this, "Event lists updated successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WaitingListActivity.this, "Failed to update event lists: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

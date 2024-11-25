package com.example.iconic_raffleevent.view;

import static java.lang.Math.min;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

        // Set item click listener for the user list
        userAdapter.setOnItemClickListener(user -> {
            com.example.iconic_raffleevent.view.EventListUtils.showUserDetailsDialog(
                    WaitingListActivity.this,
                    user,
                    eventObj,
                    firebaseAttendee,
                    this::refreshWaitingList
            );
        });

        // Fetch and display waiting list
        loadWaitingList();

        // Set listener for sampling attendees
        sampleAttendeesButton.setOnClickListener(v -> showSamplingDialog());

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
     * Refreshes the waiting list by clearing the adapter and reloading the list.
     */
    private void refreshWaitingList() {
        // Clear the user adapter to reset the displayed list
        userAdapter.clearUsers();
        // Reload the waiting list from Firestore or backend
        loadWaitingList();
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
            }

            @Override
            public void onError(String message) {
                Toast.makeText(WaitingListActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a dialog to specify the number of attendees to sample.
     */
    private void showSamplingDialog() {
        if (eventObj == null) {
            Toast.makeText(this, "Event data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> waitingList = eventObj.getWaitingList();
        List<String> invitedList = eventObj.getInvitedList();
        List<String> registeredAttendees = eventObj.getRegisteredAttendees();
        int maxAttendees = eventObj.getMaxAttendees();

        if (waitingList == null || waitingList.isEmpty()) {
            Toast.makeText(this, "No users in waiting list.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate remaining slots
        int alreadyRegistered = registeredAttendees != null ? registeredAttendees.size() : 0;
        int alreadyInvited = invitedList != null ? invitedList.size() : 0;
        int remainingSlots = maxAttendees - (alreadyRegistered + alreadyInvited);

        if (remainingSlots <= 0) {
            Toast.makeText(this, "All slots have been filled. Cannot sample more attendees.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_sample_attendees, null);

        // Get references to dialog elements
        TextView infoText = dialogView.findViewById(R.id.info_text);
        EditText attendeeCountInput = dialogView.findViewById(R.id.attendee_count_input);
        CheckBox sampleAllCheckbox = dialogView.findViewById(R.id.sample_all_checkbox);

        // Update info text dynamically
        infoText.setText(String.format("Waiting List: %d | Max Attendees: %d | Already Registered: %d | Remaining Slots: %d",
                waitingList.size(), maxAttendees, alreadyRegistered, remainingSlots));

        // Set default attendee count to min(remaining slots, waiting list size)
        int defaultSampleSize = Math.min(remainingSlots, waitingList.size());
        attendeeCountInput.setText(String.valueOf(defaultSampleSize));

        // Update checkbox text dynamically
        sampleAllCheckbox.setText(String.format("Sample all remaining %d attendees", defaultSampleSize));

        // Disable input field when "Sample all remaining" is checked
        sampleAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendeeCountInput.setEnabled(false);
                attendeeCountInput.setText(String.valueOf(defaultSampleSize));
            } else {
                attendeeCountInput.setEnabled(true);
            }
        });

        // Build and display the dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Set button listeners in the dialog
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            // Parse attendee count from input
            int sampleSize;
            try {
                sampleSize = Integer.parseInt(attendeeCountInput.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number entered.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate sample size
            if (sampleSize <= 0 || sampleSize > remainingSlots) {
                Toast.makeText(this, "Invalid number of attendees to sample.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform sampling and update lists
            sampleAttendees(sampleSize, dialog);
        });

        dialog.show();
    }

    private void sampleAttendees(int sampleSize, AlertDialog dialog) {
        if (eventObj == null) {
            Toast.makeText(this, "Event data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> waitingList = eventObj.getWaitingList();
        List<String> invitedList = eventObj.getInvitedList();
        List<String> registeredAttendees = eventObj.getRegisteredAttendees();

        if (invitedList == null) invitedList = new ArrayList<>();
        if (registeredAttendees == null) registeredAttendees = new ArrayList<>();

        if (waitingList == null || waitingList.isEmpty()) {
            Toast.makeText(this, "No users in waiting list.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Shuffle the waiting list for randomness
        Collections.shuffle(waitingList);

        // Select attendees and update lists
        List<String> selectedAttendees = waitingList.subList(0, sampleSize);
        invitedList.addAll(selectedAttendees);
        waitingList.removeAll(selectedAttendees);

        // Update Firestore with the new lists
        updateEventListsInFirestore(invitedList, waitingList, registeredAttendees, dialog);
    }

    private void updateEventListsInFirestore(List<String> invitedList, List<String> waitingList, List<String> registeredAttendees, AlertDialog dialog) {
        firebaseAttendee.updateEventLists(eventObj.getEventId(), invitedList, waitingList, registeredAttendees, new FirebaseAttendee.UpdateCallback() {
            @Override
            public void onSuccess() {
                // Update event object locally
                eventObj.setInvitedList(new ArrayList<>(invitedList));
                eventObj.setWaitingList(new ArrayList<>(waitingList));
                eventObj.setRegisteredAttendees(new ArrayList<>(registeredAttendees));

                // Provide feedback to the organizer
                Toast.makeText(WaitingListActivity.this, "Invited attendees successfully.", Toast.LENGTH_SHORT).show();

                // Dismiss dialog only after Firestore update is successful
                dialog.dismiss();

                recreate();
            }

            @Override
            public void onError(String message) {
                // If the update fails, show error message and do not close the dialog
                Toast.makeText(WaitingListActivity.this, "Failed to update event lists: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
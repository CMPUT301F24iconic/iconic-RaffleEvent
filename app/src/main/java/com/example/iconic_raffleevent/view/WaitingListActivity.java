package com.example.iconic_raffleevent.view;

import android.os.Bundle;
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
 * Activity that displays a waiting list of users for a specified event.
 * Uses a RecyclerView to show the list of users waiting to attend the event.
 */
public class WaitingListActivity extends AppCompatActivity {
    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;

    /**
     * Called when the activity is starting. Sets up the layout, initializes components,
     * and starts loading the waiting list of users for the event.
     *
     * @param savedInstanceState the saved state of the activity if it was previously terminated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

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
}

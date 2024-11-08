package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.Button;
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

public class WaitingListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;
    private Event eventObj;
    private Button sampleAttendeesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

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
    }

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

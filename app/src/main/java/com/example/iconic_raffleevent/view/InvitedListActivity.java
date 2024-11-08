package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;
import java.util.List;

public class InvitedListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private FirebaseAttendee firebaseAttendee;
    private UserAdapter userAdapter;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_list);

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
    }

    private void loadInvitedList() {
        firebaseAttendee.getEventDetailsForWaitingList(eventId, new FirebaseAttendee.EventDetailsCallback() {
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

    private void fetchUsersFromInvitedList(List<String> userIds) {
        for (String userId : userIds) {
            firebaseAttendee.getUserDetailsForWaitingList(userId, new FirebaseAttendee.UserFetchCallback() {
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
package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

public class EventDetailsActivity extends AppCompatActivity {

    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventDateTextView;
    private Button joinWaitingListButton;
    private Button leaveWaitingListButton;

    private EventController eventController;
    private User currentUser;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        eventController = new EventController();
        currentUser = getCurrentUser();
        eventId = getIntent().getStringExtra("eventId");

        fetchEventDetails();

        joinWaitingListButton.setOnClickListener(v -> joinWaitingList());
        leaveWaitingListButton.setOnClickListener(v -> leaveWaitingList());
    }

    private void fetchEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                updateUI(event);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(Event event) {
        Glide.with(this)
                .load(event.getEventImageUrl())
                .into(eventImageView);

        eventTitleTextView.setText(event.getEventTitle());
        eventDescriptionTextView.setText(event.getEventDescription());
        eventLocationTextView.setText(event.getEventLocation());
        eventDateTextView.setText(event.getEventStartDate());

        if (event.isGeolocationRequired()) {
            showGeolocationWarning();
        }
    }

    private void joinWaitingList() {
        eventController.joinWaitingList(eventId, currentUser.getUserId(), new EventController.JoinWaitingListCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Joined waiting list", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void leaveWaitingList() {
        eventController.leaveWaitingList(eventId, currentUser.getUserId(), new EventController.LeaveWaitingListCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Left waiting list", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showGeolocationWarning() {
        // Show a dialog or toast message to warn the user about geolocation requirement
        Toast.makeText(this, "This event requires geolocation", Toast.LENGTH_LONG).show();
    }

    private User getCurrentUser() {
        // Placeholder implementation. Replace with actual logic to get the current user.
        User user = new User();
        user.setUserId("user123");
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        return user;
    }
}
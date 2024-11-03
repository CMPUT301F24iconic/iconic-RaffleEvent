package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.AvatarGenerator;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
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

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;

    private EventController eventController;
    private String eventId;
    private UserController userController;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Link UI to views
        eventImageView = findViewById(R.id.eventImage);
        eventTitleTextView = findViewById(R.id.eventTitle);
        eventDescriptionTextView = findViewById(R.id.eventDescription);
        eventLocationTextView = findViewById(R.id.eventLocation);
        eventDateTextView = findViewById(R.id.eventDate);

        eventController = new EventController();
        userController = getUserController();
        loadUserProfile();
        eventId = getIntent().getStringExtra("eventId");

        fetchEventDetails();

        // Link to button views
        joinWaitingListButton = findViewById(R.id.joinWaitingListButton);
        leaveWaitingListButton = findViewById(R.id.leaveWaitingListButton);

        //Aiden Teal
        joinWaitingListButton.setEnabled(false);
        leaveWaitingListButton.setEnabled(false);

        joinWaitingListButton.setOnClickListener(v -> joinWaitingList());
        leaveWaitingListButton.setOnClickListener(v -> leaveWaitingList());

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);


        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, HubActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, ProfileActivity.class));
        });
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
        System.out.println(event);
        if (event != null) {
            Glide.with(this)
                    .load(event.getEventImageUrl())
                    .into(eventImageView);
        }

        eventTitleTextView.setText(event.getEventTitle());
        eventDescriptionTextView.setText(event.getEventDescription());
        eventLocationTextView.setText(event.getEventLocation());
        eventDateTextView.setText(event.getEventStartDate());

        if (event.isGeolocationRequired()) {
            showGeolocationWarning();
        }
    }

    private void joinWaitingList() {
        eventController.joinWaitingList(eventId, userObj.getUserId(), new EventController.JoinWaitingListCallback() {
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
        eventController.leaveWaitingList(eventId, userObj.getUserId(), new EventController.LeaveWaitingListCallback() {
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

    private void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    joinWaitingListButton.setEnabled(true);
                    leaveWaitingListButton.setEnabled(true);
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Cannot fetch user information");
            }
        });
    }

    /*
    Aiden Teal function to get userID
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }
}
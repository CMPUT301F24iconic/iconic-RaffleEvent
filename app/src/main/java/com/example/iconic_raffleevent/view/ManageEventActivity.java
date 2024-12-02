package com.example.iconic_raffleevent.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;

/**
 * Activity for managing various lists associated with an event, such as the waiting list,
 * attendee list, cancelled attendee list, and final attendee list.
 */
public class ManageEventActivity extends AppCompatActivity {
    private CardView waitingListTile;
    private CardView attendeeListTile;
    private CardView cancelledAttendeeListTile;
    private CardView finalAttendeeListTile;
    private String eventId;

    // Event details UI components
    private ImageView eventImage;
    private TextView eventTitle;
    private TextView hosterTitle;

    private EventController eventController;
    private UserController organizerController;
    private Event eventObj;
    private User eventOrganizer;

    private boolean isEventLoaded = false;
    private boolean isOrganizerLoaded = false;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;


    /**
     * Called when the activity is first created. Sets the layout, retrieves the event ID,
     * links UI elements, and sets click listeners for each button to navigate to the
     * appropriate list activity.
     *
     * @param savedInstanceState The previously saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        // Get the event ID passed from the previous screen
        eventId = getIntent().getStringExtra("eventId");

        // Initialize event controller
        eventController = new EventController();

        // Link event details UI components
        eventImage = findViewById(R.id.eventImage);
        eventTitle = findViewById(R.id.eventTitle);
        hosterTitle = findViewById(R.id.hosterTitle);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        // Link UI elements
        waitingListTile = findViewById(R.id.waitingListTile);
        attendeeListTile = findViewById(R.id.attendeeListTile);
        cancelledAttendeeListTile = findViewById(R.id.cancelledAttendeeListTile);
        finalAttendeeListTile = findViewById(R.id.finalAttendeeListTile);

        // Fetch and update event details
        fetchEventDetails();

        // Set onClickListeners for each button
        waitingListTile.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, WaitingListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        attendeeListTile.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, InvitedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Placeholder for cancelled attendee list functionality
        cancelledAttendeeListTile.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, DeclinedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Placeholder for final attendee list functionality
        finalAttendeeListTile.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, ConfirmedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, ProfileActivity.class));
        });
    }

    /**
     * Fetches the event details from the server.
     */
    private void fetchEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                eventObj = event;
                isEventLoaded = true;
                fetchOrganizerDetails(event.getOrganizerID());
                checkUIUpdate();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ManageEventActivity.this, "Error fetching event details: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fetches the organizer details for the event.
     *
     * @param organizerId The organizer ID to fetch details for.
     */
    private void fetchOrganizerDetails(String organizerId) {
        organizerController = new UserController(organizerId, this);
        organizerController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                eventOrganizer = user;
                isOrganizerLoaded = true;
                checkUIUpdate();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ManageEventActivity.this, "Error fetching organizer details: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the UI components with the fetched event and organizer details.
     */
    private void checkUIUpdate() {
        if (isEventLoaded && isOrganizerLoaded) {
            updateEventDetailsUI();
        }
    }

    /**
     * Updates the event details UI components.
     */
    private void updateEventDetailsUI() {
        if (eventObj != null) {
            // Load event image using Glide
            Glide.with(this)
                    .load(eventObj.getEventImageUrl())
                    .into(eventImage);

            // Set event title
            eventTitle.setText(eventObj.getEventTitle());
        }

        if (eventOrganizer != null) {
            // Set organizer name
            String organizerText = "Organized by: " + eventOrganizer.getName();
            hosterTitle.setText(organizerText);
        }
    }
}

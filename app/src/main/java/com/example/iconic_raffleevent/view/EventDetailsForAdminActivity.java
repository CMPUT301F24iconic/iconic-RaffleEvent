package com.example.iconic_raffleevent.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.GeoPoint;

/**
 * This Activity displays the details of a specific event. It allows users to view event information,
 * join or leave the waiting list, and access event location via a map. It also provides functionality
 * for event organizers to manage their events.
 *
 * The activity manages user interactions, navigation, geolocation services, and UI updates based on
 * event and user data. It handles dynamic UI changes based on whether the user is the event organizer
 * or a participant.
 */
public class EventDetailsForAdminActivity extends AppCompatActivity {

    private FirebaseOrganizer firebaseOrganizer;
    // View elements
    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventDateTextView;
    private TextView warningMessage;
    private Button deleteEventButton, cancelButton;

    // Controllers and data related to objects
    private EventController eventController;
    private String eventId;
    private UserController userController;
    private User userObj;
    private GeoPoint userLocation;
    private Event eventObj;
    private String orgFacility;

    private Boolean isEventLoaded;

    /**
     * Called when the activity is created. Initializes UI components,
     * sets up navigation, fetches event details, and handles user actions.
     *
     * @param savedInstanceState The saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_for_admin);

        // set async checks
        isEventLoaded = false;

        // Link UI to views
        eventImageView = findViewById(R.id.eventImage);
        eventTitleTextView = findViewById(R.id.eventTitle);
        eventDescriptionTextView = findViewById(R.id.eventDescription);
        eventLocationTextView = findViewById(R.id.eventLocation);
        eventDateTextView = findViewById(R.id.eventDate);
        warningMessage = findViewById(R.id.warning_message);
        cancelButton = findViewById(R.id.cancel_button);
        deleteEventButton = findViewById(R.id.delete_event_button);

        eventController = new EventController();
        eventId = getIntent().getStringExtra("eventId");

        firebaseOrganizer = new FirebaseOrganizer();

        fetchEventDetails();

        // Set up button listeners
        cancelButton.setOnClickListener(v -> {
            // Navigate back to the previous activity
            finish();
        });

        deleteEventButton.setOnClickListener(v -> {
            // Handle event deletion
            deleteEvent(eventObj);
        });
    }

    /**
     * Fetches the event details from the server.
     * Updates the UI with event information once the event details are successfully fetched.
     */
    private void fetchEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                eventObj = event;
                isEventLoaded = true;
                checkUIUpdate();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsForAdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the UI with the provided event details.
     * Adjusts button visibility based on the user's role (organizer or participant).
     *
     * @param event The event whose details will be displayed.
     */
    private void updateUI(Event event) {
        if (event != null) {
            Glide.with(this)
                    .load(event.getEventImageUrl())
                    .into(eventImageView);
        }

        eventTitleTextView.setText(event.getEventTitle());
        eventDescriptionTextView.setText(event.getEventDescription());
        eventLocationTextView.setText(event.getEventLocation());
        eventDateTextView.setText(event.getEventStartDate());
    }

    /**
     * Checks if both the event and user data have been loaded. If so, updates the UI with the event details.
     */
    private void checkUIUpdate() {
        if (isEventLoaded) {
            updateUI(eventObj);
        }
    }

    /**
     * Deletes the specified event from Firebase and reloads the event list to reflect the changes.
     *
     * @param event The event to be deleted.
     */
    private void deleteEvent(Event event) {
        firebaseOrganizer.deleteEvent(event.getEventId(), new FirebaseOrganizer.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsForAdminActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EventDetailsForAdminActivity.this, EventListForAdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsForAdminActivity.this, "Failed to delete event: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

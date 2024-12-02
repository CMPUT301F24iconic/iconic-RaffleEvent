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
    private TextView eventStartDateTextView;
    private TextView eventEndDateTextView;
    private TextView hosterTextView;
    private TextView warningMessage;
    private Button deleteEventButton, cancelButton, deleteEventPosterButton;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;

    // Controllers and data related to objects
    private EventController eventController;
    private String eventId;
    private UserController userController;
    private UserController organizerController;
    private User userObj;
    private User eventOrganizer;
    private GeoPoint userLocation;
    private Event eventObj;
    private String orgFacility;

    private Boolean isUserLoaded;
    private Boolean isEventLoaded;
    private Boolean isOrganizerLoaded;

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
        isUserLoaded = false;
        isEventLoaded = false;
        isOrganizerLoaded = false;

        // Link UI to views
        eventImageView = findViewById(R.id.eventImage);
        eventTitleTextView = findViewById(R.id.eventTitle);
        eventDescriptionTextView = findViewById(R.id.eventDescription);
        eventLocationTextView = findViewById(R.id.eventLocation);
        eventStartDateTextView = findViewById(R.id.eventDateStart);
        eventEndDateTextView = findViewById(R.id.eventDateEnd);
        hosterTextView = findViewById(R.id.hosterTitle);
        warningMessage = findViewById(R.id.warning_message);
        cancelButton = findViewById(R.id.cancel_button);
        deleteEventButton = findViewById(R.id.delete_event_button);
        deleteEventPosterButton = findViewById(R.id.delete_event_poster_button);

        eventController = new EventController();
        userController = getUserController();
        loadUserProfile();
        eventId = getIntent().getStringExtra("eventId");

        fetchEventDetails();

        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsForAdminActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsForAdminActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsForAdminActivity.this, ProfileActivity.class));
        });

        backButton.setOnClickListener(v -> finish());

        firebaseOrganizer = new FirebaseOrganizer();

        fetchEventDetails();

        // Set up button listeners
        cancelButton.setOnClickListener(v -> {
            // Navigate back to the previous activity
            finish();
        });

        deleteEventButton.setOnClickListener(v -> {
            if (eventObj != null) {
                confirmAndDeleteEvent();
            } else {
                Toast.makeText(EventDetailsForAdminActivity.this, "Event not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });

        deleteEventPosterButton = findViewById(R.id.delete_event_poster_button);

        deleteEventPosterButton.setOnClickListener(v -> {
            if (eventObj != null) {
                confirmAndDeletePoster();
            } else {
                Toast.makeText(EventDetailsForAdminActivity.this, "Event not loaded yet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Loads the user's profile information by fetching it from the UserController.
     * Enables the waiting list buttons and updates the UI once the user information is loaded.
     */
    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    isUserLoaded = true;
                    orgFacility = userObj.getFacilityId();
                    checkUIUpdate();
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
                runOnUiThread(() -> fetchOrganizerDetails(eventObj.getOrganizerID()));
                checkUIUpdate();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsForAdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * Generates dialog prompting the admin to delete an event
     * Deletes an event if confirmed, does nothing otherwise
     */
    private void confirmAndDeleteEvent() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> deleteEvent(eventObj))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Generates dialog prompting the admin to delete a poster
     * Deletes a poster if confirmed, does nothing otherwise
     */
    private void confirmAndDeletePoster() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Event Poster")
                .setMessage("Are you sure you want to delete the event poster? This action will set a default poster.")
                .setPositiveButton("Yes", (dialog, which) -> deletePoster())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Delete an event poster from the database and replace it with a generic one so event does not crash
     */
    private void deletePoster() {
        if (eventObj != null) {
            // Set the default poster URL
            String defaultPosterUrl = "android.resource://" + getPackageName() + "/" + R.drawable.default_image_poster;

            // Update the event object
            eventObj.setEventImageUrl(defaultPosterUrl);

            // Update event details in Firebase
            eventController.updateEventDetails(eventObj);

            // Notify the user and redirect to the updated Event Details page
            Toast.makeText(this, "Event poster deleted and set to default.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(EventDetailsForAdminActivity.this, EventDetailsForAdminActivity.class);
            intent.putExtra("eventId", eventObj.getEventId());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error: Event not found.", Toast.LENGTH_SHORT).show();
        }
    }

//    /**
//     * Fetches the event details from the server.
//     * Updates the UI with event information once the event details are successfully fetched.
//     */
//    private void fetchEventDetails() {
//        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
//            @Override
//            public void onEventDetailsFetched(Event event) {
//                eventObj = event;
//                isEventLoaded = true;
//                checkUIUpdate();
//            }
//
//            @Override
//            public void onError(String message) {
//                Toast.makeText(EventDetailsForAdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

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
                    .placeholder(R.drawable.placeholder_image)
                    .into(eventImageView);

            // Show "Delete Poster" button only if the current poster is not the default
            String defaultPosterUrl = "android.resource://" + getPackageName() + "/" + R.drawable.default_image_poster;
            if (event.getEventImageUrl().equals(defaultPosterUrl)) {
                deleteEventPosterButton.setVisibility(View.GONE);
            } else {
                deleteEventPosterButton.setVisibility(View.VISIBLE);
            }
        }

        eventTitleTextView.setText(event.getEventTitle());
        eventDescriptionTextView.setText(event.getEventDescription());
        String eventLocationText = event.getEventLocation();
        eventLocationTextView.setText(eventLocationText);

        // Format start date text
        String startDate = event.getEventStartDate() + ", " + event.getEventStartTime();
        String endDate = event.getEventEndDate() + ", " + event.getEventEndTime();

        eventStartDateTextView.setText(startDate);
        eventEndDateTextView.setText(endDate);

        String organizerText = "Organized by: " + eventOrganizer.getName();
        hosterTextView.setText(organizerText);
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

    /**
     * Checks if both the event and user data have been loaded. If so, updates the UI with the event details.
     */
    private void checkUIUpdate() {
        if (isEventLoaded && isUserLoaded && isOrganizerLoaded) {
            updateUI(eventObj);
        }
    }

    /**
     * Retrieves the unique user ID for the device. This ID is based on the device's Android Secure Settings.
     *
     * @return A unique string identifier for the device.
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Initializes and retrieves the UserController instance for managing user-related actions.
     * This controller is obtained from the UserControllerViewModel.
     *
     * @return The UserController instance configured for the current user.
     */
    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        return userController;
    }

    /**
     * Fetches the organizer details for the event so we can display who is hosting the event
     * @param organizerId The organizer we want to fetch
     */
    private void fetchOrganizerDetails(String organizerId) {
        organizerController = new UserController(organizerId, this);
        organizerController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                eventOrganizer = user;
                isOrganizerLoaded = Boolean.TRUE;
                checkUIUpdate();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsForAdminActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

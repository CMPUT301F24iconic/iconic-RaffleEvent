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

import androidx.annotation.NonNull;
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
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.GeoPoint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This Activity displays the details of a specific event. It allows users to view event information,
 * join or leave the waiting list, and access event location via a map. It also provides functionality
 * for event organizers to manage their events.
 *
 * The activity manages user interactions, navigation, geolocation services, and UI updates based on
 * event and user data. It handles dynamic UI changes based on whether the user is the event organizer
 * or a participant.
 */
public class EventDetailsActivity extends AppCompatActivity {

    // View elements
    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventStartDateTextView;
    private TextView eventEndDateTextView;
    private TextView hosterTextView;
    private Button joinWaitingListButton;
    private Button leaveWaitingListButton;
    private Button declineInvitationButton;
    private Button acceptInvitationButton;
    private Button mapButton;
    private Button editButton;
    private Button viewQRButton;
    private Button manageButton;
    private CardView congratsMessage;

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

    // Geolocation
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Called when the activity is created. Initializes UI components,
     * sets up navigation, fetches event details, and handles user actions.
     *
     * @param savedInstanceState The saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details_good);

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
        congratsMessage = findViewById(R.id.congratulations_card);

        // Link map button
        mapButton = findViewById(R.id.map_button);

        editButton = findViewById(R.id.edit_button);
        manageButton = findViewById(R.id.manage_button);
        viewQRButton = findViewById(R.id.share_button);

        eventController = new EventController();
        userController = getUserController();
        loadUserProfile();
        eventId = getIntent().getStringExtra("eventId");

        fetchEventDetails();

        // Link to button views
        joinWaitingListButton = findViewById(R.id.joinWaitingListButton);
        leaveWaitingListButton = findViewById(R.id.leaveWaitingListButton);
        declineInvitationButton = findViewById(R.id.decline_button);
        acceptInvitationButton = findViewById(R.id.accept_button);

        joinWaitingListButton.setEnabled(false);
        leaveWaitingListButton.setEnabled(false);

        joinWaitingListButton.setOnClickListener(v -> {
            joinWaitingList(eventObj);
        });
        leaveWaitingListButton.setOnClickListener(v -> leaveWaitingList());
        declineInvitationButton.setOnClickListener(v -> {
            declineInvitation();
        });
        acceptInvitationButton.setOnClickListener(v -> {
            acceptInvitation();
        });

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        /*
            Setup geolocation services to obtain location when joining waitlist
         */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(EventDetailsActivity.this, ProfileActivity.class));
        });

        manageButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, ManageEventActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        // Redirect user when clicking on mapButton
        mapButton.setOnClickListener(v -> {
            // Create an intent to start the EventDetailsActivity
            Intent intent = new Intent(EventDetailsActivity.this, MapActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventTitle", eventObj.getEventTitle());
            intent.putExtra("geolocation", eventObj.isGeolocationRequired());
            startActivity(intent);
        });

        editButton.setOnClickListener(v -> {
            // Create an intent to start the Create/Edit event activity
            Intent intent = new Intent(EventDetailsActivity.this, CreateEventActivity.class);
            intent.putExtra("facilityId", orgFacility); // Pass the facilityId
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        viewQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, EventQRViewActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
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
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
        String eventLocationText = event.getEventLocation();
        eventLocationTextView.setText(eventLocationText);

        // Format start date text
        String startDate = event.getEventStartDate() + ", " + event.getEventStartTime();
        String endDate = event.getEventEndDate() + ", " + event.getEventEndTime();

        eventStartDateTextView.setText(startDate);
        eventEndDateTextView.setText(endDate);

        String organizerText = "Organized by: " + eventOrganizer.getName();
        hosterTextView.setText(organizerText);

        // Check if the current user is the organizer
        if (event.getOrganizerID().equals(userObj.getUserId())) {
            // Show Edit and Manage buttons for the organizer
            editButton.setVisibility(View.VISIBLE);
            manageButton.setVisibility(View.VISIBLE);
            mapButton.setVisibility(View.VISIBLE);
            viewQRButton.setVisibility(View.VISIBLE);
        } else {
            // For non-organizers, handle Join/Leave button visibility
            if (event.getWaitingList().contains(userObj.getUserId())) {
                leaveWaitingListButton.setVisibility(View.VISIBLE);
            } else if (event.getInvitedList().contains(userObj.getUserId())) {
                declineInvitationButton.setVisibility(View.VISIBLE);
                acceptInvitationButton.setVisibility(View.VISIBLE);
            } else if (event.getRegisteredAttendees().contains(userObj.getUserId())) {
                congratsMessage.setVisibility(View.VISIBLE);
            } else {
                joinWaitingListButton.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * Joins the user to the event's waiting list.
     * Checks for valid email and phone number, and handles geolocation if required.
     */
    private void joinWaitingList(Event event) {
        // Format start date text
        String startDate = event.getEventStartDate() + ", " + event.getEventStartTime();
        LocalDateTime targetDateTime;
        LocalDateTime currentDateTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd, hh:mm a");
            try {
                targetDateTime = LocalDateTime.parse(startDate, formatter);
                // Get the current date and time
                currentDateTime = LocalDateTime.now();

                if (!currentDateTime.isBefore(targetDateTime)) {
                    Toast.makeText(EventDetailsActivity.this, "You cannot join a waitlist after the event has started", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date and time format: " + e.getMessage());
            }
        }

        // Ensure user has valid email and name
        if (userObj.getEmail().isEmpty() || userObj.getName().isEmpty()) {
            Toast.makeText(EventDetailsActivity.this, "You must have a valid name and email to join", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EventDetailsActivity.this, ProfileActivity.class));
            finish();
        } else if (eventObj.getDeclinedList().contains(userObj.getUserId())) {
            Toast.makeText(EventDetailsActivity.this, "You have been blacklisted from this event by the organizer", Toast.LENGTH_SHORT).show();
        }
        // Check if event requires geolocation
        else if (eventObj.isGeolocationRequired()) {
            // Bring up geolocation popup
            showGeolocationDialog();
        }
        else {
            // Add user to waiting list
            eventController.joinWaitingListWithoutLocation(eventId, userObj.getUserId(), new EventController.JoinWaitingListCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(EventDetailsActivity.this, "Joined waiting list", Toast.LENGTH_SHORT).show();
                    // Update Button UI
                    joinWaitingListButton.setVisibility(View.INVISIBLE);
                    leaveWaitingListButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Leaves the user's name from the waiting list for the event.
     */
    private void leaveWaitingList() {
        eventController.leaveWaitingList(eventObj, userObj, new EventController.LeaveWaitingListCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Left waiting list", Toast.LENGTH_SHORT).show();

                // Update Button UI
                joinWaitingListButton.setVisibility(View.VISIBLE);
                leaveWaitingListButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Decline invitation to register for an event if invited.
     */
    private void declineInvitation() {
        eventController.declineEventInvitation(eventId, userObj.getUserId(), new EventController.DeclineInvitationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Invitation declined to register for an event", Toast.LENGTH_SHORT).show();

                // Redirect the user to the home page (EventListActivity)
                Intent intent = new Intent(EventDetailsActivity.this, EventListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
                startActivity(intent);

                // Finish the current activity to remove it from the back stack
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Accept invitation to register for an event if invited (join the registeredAttendees list).
     */
    private void acceptInvitation() {
        eventController.acceptEventInvitation(eventId, userObj.getUserId(), new EventController.AcceptInvitationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Congratulations! You are successfully registered for an event", Toast.LENGTH_SHORT).show();

                declineInvitationButton.setVisibility(View.INVISIBLE);
                acceptInvitationButton.setVisibility(View.INVISIBLE);
                congratsMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a dialog requesting the user to allow access to their geolocation in order to join the waiting list.
     */
    private void showGeolocationDialog() {
        Dialog dialog = new Dialog(EventDetailsActivity.this);
        dialog.setContentView(R.layout.activity_warning_geolocation);
        dialog.show();

        Button allowGeolocation = dialog.findViewById(R.id.allowAccessButton);
        Button declineGeolocation = dialog.findViewById(R.id.declineButton);

        allowGeolocation.setOnClickListener(v -> {
            // Implement logic to change user settings to allow location grabbing
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            } else {
                // Permissions accepted, get user location
                // Add entrant to waitlist if location is valid
                getUserLocation();
            }
            // close dialog
            dialog.dismiss();
        });

        declineGeolocation.setOnClickListener(v -> {
            // Don't allow user to join event
            dialog.dismiss();
        });
    }

    /**
     * Retrieves the user's current location and proceeds to join the waiting list if successful.
     */
    private void getUserLocation() {
        userController.retrieveUserLocation(fusedLocationClient, new UserController.OnLocationReceivedCallback() {
            @Override
            public void onLocationReceived(GeoPoint location) {
                userLocation = location;
                joinWaitlist();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error retrieving location: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds the user to the waiting list using their location data.
     */
    private void joinWaitlist() {
        eventController.joinWaitingListWithLocation(eventId, userObj, userLocation, new EventController.JoinWaitingListCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventDetailsActivity.this, "Joined waiting list", Toast.LENGTH_SHORT).show();
                // Update Button UI
                joinWaitingListButton.setVisibility(View.INVISIBLE);
                leaveWaitingListButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                    joinWaitingListButton.setEnabled(true);
                    leaveWaitingListButton.setEnabled(true);
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
     * Checks if both the event and user data have been loaded. If so, updates the UI with the event details.
     */
    private void checkUIUpdate() {
        if (isEventLoaded && isUserLoaded && isOrganizerLoaded) {
            updateUI(eventObj);
        }
    }

    /**
     * Retrieves the unique user ID for the device. This ID is based on the device's Android Secure Settings.
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
                Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

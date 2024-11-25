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
public class EventDetailsActivity extends AppCompatActivity {
    // Navigation UI
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // View elements
    private ImageView eventImageView;
    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventLocationTextView;
    private TextView eventDateTextView;
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
    private ImageButton menuButton;

    // Top Nav bar
    private ImageButton notificationButton;

    // Controllers and data related to objects
    private EventController eventController;
    private String eventId;
    private UserController userController;
    private User userObj;
    private GeoPoint userLocation;
    private Event eventObj;
    private String orgFacility;

    private Boolean isUserLoaded;
    private Boolean isEventLoaded;

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
        setContentView(R.layout.activity_event_details);

        // set async checks
        isUserLoaded = false;
        isEventLoaded = false;

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Link UI to views
        eventImageView = findViewById(R.id.eventImage);
        eventTitleTextView = findViewById(R.id.eventTitle);
        eventDescriptionTextView = findViewById(R.id.eventDescription);
        eventLocationTextView = findViewById(R.id.eventLocation);
        eventDateTextView = findViewById(R.id.eventDate);
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
            joinWaitingList();
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
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView, userObj != null ? userObj.getUserId() : "");

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

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Redirect user when clicking on mapButton
        mapButton.setOnClickListener(v -> {
            // Create an intent to start the EventDetailsActivity
            Intent intent = new Intent(EventDetailsActivity.this, MapActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventTitle", eventObj.getEventTitle());
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

        // Top nav bar
        notificationButton = findViewById(R.id.notification_icon);
        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(EventDetailsActivity.this, NotificationsActivity.class))
        );

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
        eventLocationTextView.setText(event.getEventLocation());
        eventDateTextView.setText(event.getEventStartDate());

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

        if (event.isGeolocationRequired()) {
            showGeolocationWarning();
        }
    }

    /**
     * Joins the user to the event's waiting list.
     * Checks for valid email and phone number, and handles geolocation if required.
     */
    private void joinWaitingList() {
        // Ensure user has valid email and phone number
        if (userObj.getEmail().isEmpty() || userObj.getPhoneNo().isEmpty()) {
            Toast.makeText(EventDetailsActivity.this, "You must have a valid email and phone number to join", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EventDetailsActivity.this, ProfileActivity.class));
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
        // Need to implement functionality to remove geolocation from entrantLocations if they leave event
        eventController.leaveWaitingList(eventId, userObj.getUserId(), new EventController.LeaveWaitingListCallback() {
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
            }
            // Permissions accepted, get user location
            // Add entrant to waitlist if location is valid
            getUserLocation();
            // close dialog
            dialog.dismiss();
        });

        declineGeolocation.setOnClickListener(v -> {
            // Don't allow user to join event
            dialog.dismiss();
        });
    }

    /**
     * Shows a warning message if the event requires geolocation.
     */
    private void showGeolocationWarning() {
        // Show a dialog or toast message to warn the user about geolocation requirement
        Toast.makeText(this, "This event requires geolocation", Toast.LENGTH_LONG).show();
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
        eventController.joinWaitingListWithLocation(eventId, userObj.getUserId(), userLocation, new EventController.JoinWaitingListCallback() {
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
                    System.out.println(orgFacility);
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
        if (isEventLoaded && isUserLoaded) {
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
}

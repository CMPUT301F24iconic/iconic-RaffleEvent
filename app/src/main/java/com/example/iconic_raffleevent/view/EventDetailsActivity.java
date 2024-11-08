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
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.GeoPoint;

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
    private Button mapButton;
    private Button editButton;
    private Button manageButton;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;

    // Controllers and data related to objects
    private EventController eventController;
    private String eventId;
    private UserController userController;
    private User userObj;
    private GeoPoint userLocation;
    private Event eventObj;

    private Boolean isUserLoaded;
    private Boolean isEventLoaded;

    // Geolocation
    private FusedLocationProviderClient fusedLocationClient;

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

        // Link map button
        mapButton = findViewById(R.id.map_button);

        editButton = findViewById(R.id.edit_button);
        manageButton = findViewById(R.id.manage_button);

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

        joinWaitingListButton.setOnClickListener(v -> {
            joinWaitingList();
        });
        leaveWaitingListButton.setOnClickListener(v -> leaveWaitingList());

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

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

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Redirect user when clicking on mapButton
        mapButton.setOnClickListener(v -> {
            // Create an intent to start the EventDetailsActivity
            Intent intent = new Intent(EventDetailsActivity.this, MapActivity.class);
            intent.putExtra("eventId", eventId);
            intent.putExtra("eventTitle", eventObj.getEventTitle());
            startActivity(intent);
        });
    }

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

            // Hide Join and Leave buttons as they are not applicable for the organizer
            joinWaitingListButton.setVisibility(View.GONE);
            leaveWaitingListButton.setVisibility(View.GONE);
        } else {
            // For non-organizers, handle Join/Leave button visibility
            if (event.getWaitingList().contains(userObj.getUserId())) {
                joinWaitingListButton.setVisibility(View.INVISIBLE);
                leaveWaitingListButton.setVisibility(View.VISIBLE);
            } else {
                joinWaitingListButton.setVisibility(View.VISIBLE);
                leaveWaitingListButton.setVisibility(View.INVISIBLE);
            }
        }

        if (event.isGeolocationRequired()) {
            showGeolocationWarning();
        }
    }

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

    // Need to implement functionality to remove geolocation from entrantLocations if they leave event
    private void leaveWaitingList() {
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

    private void showGeolocationWarning() {
        // Show a dialog or toast message to warn the user about geolocation requirement
        Toast.makeText(this, "This event requires geolocation", Toast.LENGTH_LONG).show();
    }

    private void getUserLocation() {
        userController.retrieveUserLocation(fusedLocationClient, this, new UserController.OnLocationReceivedCallback() {
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

    private void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    joinWaitingListButton.setEnabled(true);
                    leaveWaitingListButton.setEnabled(true);
                    isUserLoaded = true;
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

    private void checkUIUpdate() {
        if (isEventLoaded && isUserLoaded) {
            updateUI(eventObj);
        }
    }

    /*
    Aiden Teal function to get userID
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Zhiyuan Li
    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(),getApplicationContext());
        userController = userControllerViewModel.getUserController();
        return userController;
    }
}
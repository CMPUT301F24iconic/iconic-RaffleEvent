package com.example.iconic_raffleevent.view;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
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

    // Geolocation
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        initializeViews();
        initializeControllers();
        setupListeners();
        loadData();
    }

    private void initializeViews() {
        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Initialize main views
        eventImageView = findViewById(R.id.eventImage);
        eventTitleTextView = findViewById(R.id.eventTitle);
        eventDescriptionTextView = findViewById(R.id.eventDescription);
        eventLocationTextView = findViewById(R.id.eventLocation);
        eventDateTextView = findViewById(R.id.eventDate);
        mapButton = findViewById(R.id.map_button);

        // Initialize buttons
        joinWaitingListButton = findViewById(R.id.joinWaitingListButton);
        leaveWaitingListButton = findViewById(R.id.leaveWaitingListButton);
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        // Initially disable waiting list buttons
        joinWaitingListButton.setEnabled(false);
        leaveWaitingListButton.setEnabled(false);
    }

    private void initializeControllers() {
        eventController = new EventController();
        userController = getUserController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);
    }

    private void setupListeners() {
        // Setup waiting list buttons
        joinWaitingListButton.setOnClickListener(v -> joinWaitingList());
        leaveWaitingListButton.setOnClickListener(v -> leaveWaitingList());

        // Setup navigation buttons
        homeButton.setOnClickListener(v -> startActivity(new Intent(this, EventListActivity.class)));
        qrButton.setOnClickListener(v -> startActivity(new Intent(this, QRScannerActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Setup map button
        mapButton.setOnClickListener(v -> {
            if (eventObj != null) {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("eventTitle", eventObj.getEventTitle());
                startActivity(intent);
            }
        });
    }

    private void loadData() {
        eventId = getIntent().getStringExtra("eventId");
        loadUserProfile();
        fetchEventDetails();
    }

    private void fetchEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                runOnUiThread(() -> {
                    updateUI(event);
                    eventObj = event;
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void updateUI(Event event) {
        if (event != null) {
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
    }

    private void joinWaitingList() {
        if (eventObj == null || userObj == null) {
            Toast.makeText(this, "Unable to join waitlist: Event or user data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        if (eventObj.isGeolocationRequired()) {
            showGeolocationDialog();
        } else {
            joinWaitingListWithoutLocation();
        }
    }

    private void joinWaitingListWithoutLocation() {
        eventController.joinWaitingListWithoutLocation(eventId, userObj.getUserId(), new EventController.JoinWaitingListCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Joined waiting list", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void leaveWaitingList() {
        if (userObj == null) {
            Toast.makeText(this, "Unable to leave waitlist: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.leaveWaitingList(eventId, userObj.getUserId(), new EventController.LeaveWaitingListCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Left waiting list", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void showGeolocationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.activity_warning_geolocation);

        Button allowGeolocation = dialog.findViewById(R.id.allowAccessButton);
        Button declineGeolocation = dialog.findViewById(R.id.declineButton);

        allowGeolocation.setOnClickListener(v -> {
            requestLocationPermissions();
            getUserLocation();
            dialog.dismiss();
        });

        declineGeolocation.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private void showGeolocationWarning() {
        Toast.makeText(this, "This event requires geolocation", Toast.LENGTH_LONG).show();
    }

    private void getUserLocation() {
        userController.retrieveUserLocation(fusedLocationClient, this, new UserController.OnLocationReceivedCallback() {
            @Override
            public void onLocationReceived(GeoPoint location) {
                userLocation = location;
                joinWaitlistWithLocation();
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error getting location: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void joinWaitlistWithLocation() {
        if (userLocation == null) {
            Toast.makeText(this, "Unable to join: Location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.joinWaitingListWithLocation(eventId, userObj.getUserId(), userLocation, new EventController.JoinWaitingListCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Joined waiting list", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    runOnUiThread(() -> {
                        joinWaitingListButton.setEnabled(true);
                        leaveWaitingListButton.setEnabled(true);
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(EventDetailsActivity.this, "Unable to load user profile", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventDetailsActivity.this, "Error loading profile: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        return userController;
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
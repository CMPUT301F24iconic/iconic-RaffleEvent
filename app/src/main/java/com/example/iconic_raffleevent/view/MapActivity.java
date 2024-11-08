package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

/**
 * This activity displays the map for a specific event, showing the locations of entrants on the map.
 * It includes a navigation drawer for quick access to other parts of the app.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private EventController eventController;
    private String eventId;
    private String eventTitle;
    private TextView eventHeader;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private ImageButton notificationButton;

    /**
     * Initializes the activity, setting up the UI elements, navigation drawer, and map.
     * Fetches event details such as event ID and title and initializes the map.
     *
     * @param savedInstanceState Bundle containing saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_map);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        eventController = new EventController();
        eventId = getIntent().getStringExtra("eventId");
        eventTitle = getIntent().getStringExtra("eventTitle");

        eventHeader = findViewById(R.id.event_header);
        eventHeader.setText(eventTitle);

        // Top nav bar
        notificationButton = findViewById(R.id.notification_icon);
        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(MapActivity.this, NotificationsActivity.class))
        );

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this, ProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Called when the map is ready to be used.
     * Fetches and displays the locations of entrants for the specified event on the map.
     *
     * @param googleMap The GoogleMap instance to be used.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        addEntrantLocations(eventId, googleMap);
    }

    /**
     * Fetches and adds markers for entrant locations on the map for the given event ID.
     *
     * @param eventId The event ID whose entrant locations are to be fetched.
     * @param googleMap The GoogleMap instance to add markers on.
     */
    public void addEntrantLocations(String eventId, @NonNull GoogleMap googleMap) {
        eventController.getEventMap(eventId, new EventController.EventMapCallback() {
            @Override
            public void onEventMapFetched(ArrayList<GeoPoint> locations) {
                for (int i = 0; i < locations.size(); i++) {
                    LatLng location = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(location));
                }
                if (!locations.isEmpty()) {
                    LatLng startingLocation = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation, 10));
                }
            }
            @Override
            public void onError(String message) {
                System.out.println("System could not retrieve event and entrant locations");
            }
        });
    }
}

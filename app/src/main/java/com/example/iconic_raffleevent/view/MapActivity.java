package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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
    private Boolean geolocationEnabled;
    private SupportMapFragment mapFragment;
    private TextView placeholderText;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;


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

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        eventController = new EventController();
        eventId = getIntent().getStringExtra("eventId");
        eventTitle = getIntent().getStringExtra("eventTitle");
        geolocationEnabled = getIntent().getBooleanExtra("geolocation", Boolean.FALSE);


        eventHeader = findViewById(R.id.event_header);
        String mapHeaderText = eventTitle + "'s Waitlist Locations";
        eventHeader.setText(mapHeaderText);
        placeholderText = findViewById(R.id.empty_message);

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

        backButton.setOnClickListener(v -> finish());

        // Load user UI including map
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Delay visibility changes until the view is ready
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Triggers onMapReady when the map is initialized
        }

        checkGeolocationEnabled(geolocationEnabled);
    }

    /**
     * Checks if geolocation is enabled for an event and updates the UI accordingly
     *
     * @param geolocationEnabled Boolean value reflecting if the event geolocation is enabled
     */
    public void checkGeolocationEnabled(Boolean geolocationEnabled) {
        if (!geolocationEnabled) {
            String text = "Geolocation for event is currently disabled. Enable geolocation to gain access";
            placeholderText.setText(text);
            placeholderText.setVisibility(View.VISIBLE);
        }
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
            public void onEventMapFetched(Map<String, Object> locations) {
                GeoPoint firstLocation = null;
                if (locations != null && !locations.isEmpty()) {
                    // Toggle map visibility
                    placeholderText.setVisibility(View.INVISIBLE);
                    System.out.println(mapFragment.getView());
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    System.out.println("Here");

                    for (Map.Entry<String,Object> entry : locations.entrySet()) {
                        GeoPoint geo = (GeoPoint) entry.getValue();
                        if (firstLocation == null) {
                            firstLocation = new GeoPoint(geo.getLatitude(), geo.getLongitude());
                        }
                        LatLng location = new LatLng(geo.getLatitude(), geo.getLongitude());
                        Integer substringIndex = entry.getKey().indexOf("-") + 1;
                        String markerTitle = entry.getKey().substring(substringIndex);
                        googleMap.addMarker(new MarkerOptions().position(location).title(markerTitle));
                    }
                    if (firstLocation != null) {
                        LatLng startingLocation = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startingLocation, 10));
                    }
                } else {
                    mapFragment.getView().setVisibility(View.INVISIBLE);
                    String text = "There are currently no entrants in your waitlist. Once people join, their locations will be shown here.";
                    // Set text saying no entrant in waitlist
                    placeholderText.setText(text);
                    placeholderText.setVisibility(View.VISIBLE);
                    System.out.println("Here2");
                }
            }
            @Override
            public void onError(String message) {
                System.out.println("System could not retrieve event and entrant locations");
            }
        });
    }

}

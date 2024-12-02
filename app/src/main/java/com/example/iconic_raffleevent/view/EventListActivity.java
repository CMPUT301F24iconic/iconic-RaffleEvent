package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Displays a list of events available to the user and allows navigation to other sections
 * of the app such as QR scanner, profile, and notifications.
 */
public class EventListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ListView eventListView;
    public EventAdapter eventAdapter;
    public List<Event> eventList;
    private EventController eventController;
    private TextView eventPlaceholder;

    // Nav bar
    private ImageButton homeButton;
    public ImageButton qrButton;
    public ImageButton profileButton;
    private ImageButton menuButton;
    private ImageButton notificationButton;

    public User userObj;
    private UserController userController;

    /**
     * Initializes the activity and sets up views, controllers, listeners, and loads initial data.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down, this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        initializeViews();
        initializeControllers();
        setupListeners();
        loadData();
    }

    /**
     * refresh the event list when the activity resumes
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the event list when the activity resumes
        loadData();
    }

    /**
     * Initializes the views for the activity, including navigation drawer, event list view,
     * and navigation buttons.
     */
    private void initializeViews() {
        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        eventPlaceholder = findViewById(R.id.empty_message);

        // Initialize ListView and Adapter
        eventListView = findViewById(R.id.eventListView);
        eventList = new ArrayList<>();
        String currentUserId = getUserID();
        eventAdapter = new EventAdapter(this, eventList, currentUserId);
        eventListView.setAdapter(eventAdapter);

        // Initialize buttons
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);
        notificationButton = findViewById(R.id.notification_icon);
    }

    /**
     * Initializes the necessary controllers, including the EventController and UserController.
     * Sets up the navigation drawer with helper methods.
     */
    private void initializeControllers() {
        eventController = new EventController();
        userController = getUserController();
    }

    /**
     * Sets up listeners for various user interactions, including event list item clicks
     * and navigation button clicks.
     */
    private void setupListeners() {
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);
            Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
            intent.putExtra("eventId", selectedEvent.getEventId());
            startActivity(intent);
        });

        // Footer buttons logic
        homeButton.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, EventListActivity.class))
        );

        qrButton.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, QRScannerActivity.class))
        );

        profileButton.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, ProfileActivity.class))
        );

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(EventListActivity.this, NotificationsActivity.class))
        );
    }

    /**
     * Loads the user's profile and fetches the list of events.
     */
    private void loadData() {
        loadUserProfile();
        fetchEvents();
    }

    /**
     * Fetches the list of events associated with the current user. Updates the event list
     * and notifies the adapter to refresh the UI.
     */
    private void fetchEvents() {
        String userId = getUserID();
        eventController.getAllUserEvents(userId, new EventController.EventListCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                runOnUiThread(() -> {
                    HashSet<String> eventIds = new HashSet<>();
                    eventList.clear();
                    for (Event event : events) {
                        if (!eventIds.contains(event.getEventId())) {
                            eventIds.add(event.getEventId());
                            eventList.add(event);
                        }
                    }
                    if (eventList.isEmpty()) {
                        eventPlaceholder.setVisibility(View.VISIBLE);
                        eventListView.setVisibility(View.INVISIBLE);
                    } else {
                        eventPlaceholder.setVisibility(View.INVISIBLE);
                        eventListView.setVisibility(View.VISIBLE);
                    }
                    eventAdapter.notifyDataSetChanged();
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(EventListActivity.this,
                            "Error fetching events: " + message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Retrieves the unique identifier for the user based on the device's secure settings.
     *
     * @return A unique string identifier for the device.
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Retrieves and initializes the UserController for managing user information and actions.
     *
     * @return The UserController instance configured for the current user.
     */
    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        return userControllerViewModel.getUserController();
    }

    /**
     * Loads the user's profile information using the UserController and stores it in the user object.
     * Displays an error message if the user profile cannot be loaded.
     */
    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    System.out.println("User fetched successfully: " + user.getName());
                    DrawerHelper.setupDrawer(EventListActivity.this, drawerLayout, userObj.getUserId());
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(EventListActivity.this,
                                    "User information is null", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(EventListActivity.this,
                                "Error loading user profile: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Cleans up resources and performs any necessary cleanup actions when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
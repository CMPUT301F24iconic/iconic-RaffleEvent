package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ListView eventListView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private EventController eventController;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private ImageButton notificationButton;

    private User userObj;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        initializeViews();
        initializeControllers();
        setupListeners();
        loadData();
    }

    private void initializeViews() {
        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Initialize ListView and Adapter
        eventListView = findViewById(R.id.eventListView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        eventListView.setAdapter(eventAdapter);

        // Initialize buttons
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);
        notificationButton = findViewById(R.id.notification_icon);
    }

    private void initializeControllers() {
        eventController = new EventController();
        userController = getUserController();
        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);
    }

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

    private void loadData() {
        loadUserProfile();
        fetchEvents();
    }

    private void fetchEvents() {
        String userId = getUserID();
        eventController.getUserWaitingListEvents(userId, new EventController.EventListCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                runOnUiThread(() -> {
                    eventList.clear();
                    eventList.addAll(events);
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

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        return userControllerViewModel.getUserController();
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
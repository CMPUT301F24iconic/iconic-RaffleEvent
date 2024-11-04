package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.AvatarGenerator;
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

    //Aiden Teal
    private User userObj;

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        eventListView = findViewById(R.id.eventListView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        eventListView.setAdapter(eventAdapter);
        eventController = new EventController();

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        // Aiden Teal
        userController = getUserController();

        loadUserProfile();

        // Set item click listener for the event list
        eventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected event
                Event selectedEvent = eventList.get(position);

                // Create an intent to start the EventDetailsActivity
                Intent intent = new Intent(EventListActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", selectedEvent.getEventId());
                startActivity(intent);
            }
        });

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(EventListActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(EventListActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(EventListActivity.this, ProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Fetch events from the server or local database
        fetchEvents();
    }

    private void fetchEvents() {
        String userId = getUserID();  // Get the user ID
        eventController.getUserWaitingListEvents(userId, new EventController.EventListCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                eventList.clear();  // Clear the list to avoid duplicates
                eventList.addAll(events);  // Add the fetched events
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String message) {
                // Handle errors, e.g., display a toast or log the error
                System.out.println("Error fetching waiting list events: " + message);
            }
        });
    }


    /*
   Aiden Teal function to get userID
    */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }

    private void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
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
}
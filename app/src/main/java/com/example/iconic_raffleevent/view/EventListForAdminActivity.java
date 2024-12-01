package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a list of events available to the user and allows navigation to other sections
 * of the app such as QR scanner, profile, and notifications.
 */
public class EventListForAdminActivity extends AppCompatActivity {
    private ListView eventListView;
    public EventAdapter eventAdapter;
    public List<Event> eventList;
    private EventController eventController;

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
        setContentView(R.layout.activity_event_list_for_admin);

        initializeViews();
        initializeControllers();
        setupListeners();
        loadEventList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the event list when the activity resumes
        loadEventList();
    }

    /**
     * Initializes the views for the activity, including navigation drawer, event list view,
     * and navigation buttons.
     */
    private void initializeViews() {
        // Initialize ListView and Adapter
        eventListView = findViewById(R.id.eventListView);
        eventList = new ArrayList<>();
        String currentUserId = getUserID();
        eventAdapter = new EventAdapter(this, eventList, currentUserId);
        eventListView.setAdapter(eventAdapter);
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
            Intent intent = new Intent(EventListForAdminActivity.this, EventDetailsForAdminActivity.class);
            intent.putExtra("eventId", selectedEvent.getEventId());
            startActivity(intent);
        });
    }

    /**
     * Loads the list of all events from the database and updates the RecyclerView.
     */
    private void loadEventList() {
        eventController.getAllEvents(new EventController.EventListCallback() {
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
                    Toast.makeText(EventListForAdminActivity.this,
                            "Error loading events: " + message, Toast.LENGTH_SHORT).show();
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
     * Cleans up resources and performs any necessary cleanup actions when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
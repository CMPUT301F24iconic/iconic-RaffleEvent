package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.model.Event;

import java.util.ArrayList;

/**
 * Activity that provides an interface for administrators to manage events.
 * Allows loading, viewing, and removing events.
 */
public class EventManagementActivity extends AppCompatActivity {
    private ListView eventListView;
    private EventController eventController;
    private ArrayList<Event> eventList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventTitles;

    /**
     * Initializes the activity, sets up the layout, and starts loading the event list.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_management);

        eventListView = findViewById(R.id.event_list_view);
        eventController = new EventController();

        loadEventList();
    }

    /**
     * Loads the list of events from the EventController and sets up the ListView with event titles.
     * On successful loading, sets up a click listener for each item to show management options.
     */
    private void loadEventList() {
        eventController.getAllEvents(new EventController.EventListCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                eventList = events;
                eventTitles = new ArrayList<>();

                for (Event event : eventList) {
                    eventTitles.add(event.getEventTitle());
                }

                adapter = new ArrayAdapter<>(EventManagementActivity.this, android.R.layout.simple_list_item_1, eventTitles);
                eventListView.setAdapter(adapter);

                eventListView.setOnItemClickListener((parent, view, position, id) -> showEventOptionsDialog(eventList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventManagementActivity.this, "Failed to load events: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays a dialog for managing an event, allowing the user to confirm removal of the selected event.
     *
     * @param event The event selected for management.
     */
    private void showEventOptionsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Event")
                .setMessage("Would you like to remove this event?")
                .setPositiveButton("Remove", (dialog, which) -> removeEvent(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Removes the specified event using the EventController and reloads the event list upon successful deletion.
     *
     * @param event The event to be removed.
     */
    private void removeEvent(Event event) {
        eventController.deleteEvent(event.getEventId(), new EventController.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventManagementActivity.this, "Event removed successfully", Toast.LENGTH_SHORT).show();
                loadEventList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventManagementActivity.this, "Failed to remove event: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

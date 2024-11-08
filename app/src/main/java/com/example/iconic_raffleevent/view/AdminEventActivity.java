// AdminEventActivity.java
package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Event;

import java.util.ArrayList;

/**
 * AdminEventActivity provides an interface for administrators to view and manage events.
 * This activity displays a list of events and allows the admin to delete events through
 * Firebase.
 */
public class AdminEventActivity extends AppCompatActivity {
    private ListView eventListView;
    private ArrayAdapter<String> eventAdapter;
    private ArrayList<Event> eventList;
    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Called when the activity is first created. Initializes views and loads the list of events.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event);

        eventListView = findViewById(R.id.event_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadEventList();
    }

    /**
     * Loads the list of events from Firebase and displays them in the ListView.
     * Sets an item click listener on each event to allow the admin to delete events.
     */
    private void loadEventList() {
        firebaseOrganizer.getAllEvents(new FirebaseOrganizer.GetEventsCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                eventList = events;
                ArrayList<String> eventTitles = new ArrayList<>();
                for (Event event : events) {
                    eventTitles.add(event.getEventTitle());
                }
                eventAdapter = new ArrayAdapter<>(AdminEventActivity.this, android.R.layout.simple_list_item_1, eventTitles);
                eventListView.setAdapter(eventAdapter);
                eventListView.setOnItemClickListener((adapterView, view, i, l) -> showDeleteDialog(eventList.get(i)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminEventActivity.this, "Error loading events: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog to delete an event.
     *
     * @param event The Event object to be deleted.
     */
    private void showDeleteDialog(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the specified event from Firebase and refreshes the event list upon success.
     *
     * @param event The Event object to be deleted.
     */
    private void deleteEvent(Event event) {
        firebaseOrganizer.deleteEvent(event.getEventId(), new FirebaseOrganizer.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                loadEventList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminEventActivity.this, "Error deleting event: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

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

public class EventListForAdminActivity extends AppCompatActivity {

    private ListView eventListView;
    private ArrayList<Event> eventList;
    private FirebaseOrganizer firebaseOrganizer;
    private ArrayAdapter<String> eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list_for_admin);

        eventListView = findViewById(R.id.event_list_view);
        firebaseOrganizer = new FirebaseOrganizer();

        loadEventList();
    }

    private void loadEventList() {
        firebaseOrganizer.getAllEvents(new FirebaseOrganizer.GetEventsCallback() {
            @Override
            public void onEventsFetched(ArrayList<Event> events) {
                eventList = events;
                ArrayList<String> eventNames = new ArrayList<>();

                for (Event event : events) {
                    eventNames.add(event.getEventTitle());
                }

                eventAdapter = new ArrayAdapter<>(EventListForAdminActivity.this, android.R.layout.simple_list_item_1, eventNames);
                eventListView.setAdapter(eventAdapter);

                eventListView.setOnItemClickListener((parent, view, position, id) -> showEventOptionsDialog(eventList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventListForAdminActivity.this, "Error loading events: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEventOptionsDialog(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Manage Event")
                .setMessage("Would you like to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent(Event event) {
        firebaseOrganizer.deleteEvent(event.getEventId(), new FirebaseOrganizer.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EventListForAdminActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                loadEventList(); // Refresh the list after deletion
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventListForAdminActivity.this, "Failed to delete event: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

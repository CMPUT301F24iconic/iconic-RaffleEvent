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

public class EventManagementActivity extends AppCompatActivity {

    private ListView eventListView;
    private EventController eventController;
    private ArrayList<Event> eventList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_management);

        eventListView = findViewById(R.id.event_list_view);
        eventController = new EventController();

        loadEventList();
    }

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

    private void showEventOptionsDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Event")
                .setMessage("Would you like to remove this event?")
                .setPositiveButton("Remove", (dialog, which) -> removeEvent(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

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

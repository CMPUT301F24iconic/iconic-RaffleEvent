package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private ListView eventListView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        eventListView = findViewById(R.id.eventListView);
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(this, eventList);
        eventListView.setAdapter(eventAdapter);

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

        // Fetch events from the server or local database
        fetchEvents();
    }

    private void fetchEvents() {
        // Placeholder implementation. Replace with actual logic to fetch events.
        Event event1 = new Event();
        event1.setEventId("event1");
        event1.setEventTitle("Event 1");
        event1.setEventDescription("Description for Event 1");
        event1.setEventLocation("Location 1");
        event1.setEventStartDate("2023-06-01");
        event1.setEventStartTime("10:00");
        event1.setEventEndDate("2023-06-01");
        event1.setEventEndTime("12:00");
        event1.setEventImageUrl("https://example.com/event1.jpg");
        event1.setMaxAttendees(100);
        event1.setGeolocationRequired(false);

        Event event2 = new Event();
        event2.setEventId("event2");
        event2.setEventTitle("Event 2");
        event2.setEventDescription("Description for Event 2");
        event2.setEventLocation("Location 2");
        event2.setEventStartDate("2023-06-05");
        event2.setEventStartTime("14:00");
        event2.setEventEndDate("2023-06-05");
        event2.setEventEndTime("16:00");
        event2.setEventImageUrl("https://example.com/event2.jpg");
        event2.setMaxAttendees(50);
        event2.setGeolocationRequired(true);

        eventList.add(event1);
        eventList.add(event2);
        eventAdapter.notifyDataSetChanged();
    }
}
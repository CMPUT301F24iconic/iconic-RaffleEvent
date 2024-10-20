package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;
import com.example.iconic_raffleevent.R;

/**
 * EventDetailsActivity shows the details of an event after scanning a QR code.
 * Entrants can register for the event directly from this screen.
 */
public class EventDetailsActivity extends AppCompatActivity {

    private TextView eventTitleTextView;
    private TextView eventDescriptionTextView;
    private TextView eventDateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        // Initialize UI components
        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);

        // Example: Retrieve event data passed from the QR scan
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventDescription = getIntent().getStringExtra("eventDescription");
        String eventDate = getIntent().getStringExtra("eventDate");

        // Set the event details in the UI
        eventTitleTextView.setText(eventTitle);
        eventDescriptionTextView.setText(eventDescription);
        eventDateTextView.setText(eventDate);

        // Example: Button to register for the event
        findViewById(R.id.registerButton).setOnClickListener(v -> {
            // Logic to register the entrant for the event
        });
    }
}
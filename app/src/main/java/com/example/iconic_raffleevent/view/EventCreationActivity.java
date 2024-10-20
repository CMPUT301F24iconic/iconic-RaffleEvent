package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;

/**
 * EventCreationActivity allows organizers to create and manage their events.
 * Organizers can set details such as the event title, date, and capacity.
 */
public class EventCreationActivity extends AppCompatActivity {

    private EditText eventTitleEditText;
    private EditText eventDescriptionEditText;
    private EditText eventDateEditText;
    private EditText eventCapacityEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        // Initialize UI components
        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        eventDescriptionEditText = findViewById(R.id.eventDescriptionEditText);
        eventDateEditText = findViewById(R.id.eventDateEditText);
        eventCapacityEditText = findViewById(R.id.eventCapacityEditText);

        // Example: Create event button
        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            // Logic to create a new event and save it to the database
        });
    }
}
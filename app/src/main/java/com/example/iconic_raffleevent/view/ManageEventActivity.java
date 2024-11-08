package com.example.iconic_raffleevent.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;

/**
 * Activity for managing various lists associated with an event, such as the waiting list,
 * attendee list, cancelled attendee list, and final attendee list.
 */
public class ManageEventActivity extends AppCompatActivity {
    private Button waitingListButton;
    private Button attendeeListButton;
    private Button cancelledAttendeeListButton;
    private Button finalAttendeeListButton;
    private String eventId;

    /**
     * Called when the activity is first created. Sets the layout, retrieves the event ID,
     * links UI elements, and sets click listeners for each button to navigate to the
     * appropriate list activity.
     *
     * @param savedInstanceState The previously saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        // Get the event ID passed from the previous screen
        eventId = getIntent().getStringExtra("eventId");

        // Link UI elements
        waitingListButton = findViewById(R.id.waitingListButton);
        attendeeListButton = findViewById(R.id.attendeeListButton);
        cancelledAttendeeListButton = findViewById(R.id.cancelledAttendeeListButton);
        finalAttendeeListButton = findViewById(R.id.finalAttendeeListButton);

        // Set onClickListeners for each button
        waitingListButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, WaitingListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        attendeeListButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, InvitedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Placeholder for cancelled attendee list functionality
        cancelledAttendeeListButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, DeclinedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });

        // Placeholder for final attendee list functionality
        finalAttendeeListButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageEventActivity.this, ConfirmedListActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }
}

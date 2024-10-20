package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;

/**
 * WaitingListActivity allows organizers to view and manage the waiting list of entrants for an event.
 * Organizers can draw a lottery and invite selected entrants.
 */
public class WaitingListActivity extends AppCompatActivity {

    private TextView waitingListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_list);

        // Initialize UI components
        waitingListTextView = findViewById(R.id.waitingListTextView);

        // Example: Load waiting list data
        loadWaitingList();

        // Example: Draw lottery button
        findViewById(R.id.drawLotteryButton).setOnClickListener(v -> {
            // Logic to randomly draw entrants from the waiting list
        });
    }

    private void loadWaitingList() {
        // Example: Retrieve waiting list data from database
        waitingListTextView.setText("Entrant 1\nEntrant 2\nEntrant 3");
    }
}
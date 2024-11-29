package com.example.iconic_raffleevent.view;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.iconic_raffleevent.R;
import com.google.android.material.navigation.NavigationView;

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

    // Navigation UI
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;

    // Top Nav bar
//    private ImageButton notificationButton;

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

        // Initialize DrawerLayout and NavigationView
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.navigation_view);

        // in onCreate
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
//        notificationButton = findViewById(R.id.notification_icon);
        backButton = findViewById(R.id.back_button);

//        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

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

        // Top nav bar
//        notificationButton.setOnClickListener(v ->
//                startActivity(new Intent(ManageEventActivity.this, NotificationsActivity.class))
//        );
        backButton.setOnClickListener(v -> finish());

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(ManageEventActivity.this, ProfileActivity.class));
        });
    }
}

package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.view.EventCreationActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;

/**
 * MainActivity serves as the home screen of the application.
 * It provides navigation options for users to view events, their profile, or create new events.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Inflate the home screen layout

        // Example button to go to the event creation screen
        findViewById(R.id.createEventButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EventCreationActivity.class);
            startActivity(intent);
        });

        // Example button to view profile
        findViewById(R.id.profileButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import com.example.iconic_raffleevent.R;

/**
 * ProfileActivity allows users to view and edit their profile information, such as name, email, and profile picture.
 */
public class ProfileActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);

        // Example: Load user profile data
        loadUserProfile();

        // Example: Save profile updates
        findViewById(R.id.saveProfileButton).setOnClickListener(v -> {
            // Logic to save updated profile information
        });
    }

    private void loadUserProfile() {
        // Example: Load user's profile details from database
        nameEditText.setText("John Doe");
        emailEditText.setText("john.doe@example.com");
    }
}
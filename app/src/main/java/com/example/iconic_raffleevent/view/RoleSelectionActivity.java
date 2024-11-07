package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;

public class RoleSelectionActivity extends AppCompatActivity {
    private Button entrant_button;
    private Button admin_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        entrant_button = findViewById(R.id.entrant_organizer_button);
        admin_button = findViewById(R.id.admin_button);

        entrant_button.setOnClickListener(v -> {
            // Just sending to profile activity for now to see if user choses entrant/organizer role
            startActivity(new Intent(RoleSelectionActivity.this, EventListActivity.class));
        });

        admin_button.setOnClickListener(v -> {
            // Send user to admin UI when implemented
        });
    }
}

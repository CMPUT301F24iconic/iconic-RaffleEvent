package com.example.iconic_raffleevent.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;

/**
 * AdminHubActivity serves as the main hub for administrative actions within the application.
 * It provides access to various management functionalities, such as managing users, events,
 * images, and QR codes. Access is restricted to users who provide the correct admin password.
 */
public class AdminHubActivity extends AppCompatActivity {
    private static final String ADMIN_PASSWORD = "adminPass123"; // Set your actual admin password here

    private Button manageUsersButton;
    private Button manageEventsButton;
    private Button manageQRCodeButton;
    private Button manageFacilitiesButton;
    private Button backToRoleSelectionButton;
//    private boolean isAdminAuthenticated = false;

    /**
     * Called when the activity is first created. Initializes buttons and prompts for admin
     * authentication if not already authenticated.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hub);

        // Initialize buttons
        manageUsersButton = findViewById(R.id.manage_users_button);
        manageEventsButton = findViewById(R.id.manage_events_button);
        manageQRCodeButton = findViewById(R.id.manage_qr_code_button);
        manageFacilitiesButton = findViewById(R.id.manage_facilities_button);
        backToRoleSelectionButton = findViewById(R.id.back_to_role_selection_button);

        // Set up button listeners (only active if authenticated)
        manageUsersButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserListActivity.class));
        });

        manageEventsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EventListForAdminActivity.class));
        });

        manageQRCodeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, QRCodeGalleryActivity.class));
        });

        manageFacilitiesButton.setOnClickListener(v -> {
            startActivity(new Intent(this, FacilityListForAdminActivity.class));
        });

        backToRoleSelectionButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSelectionActivity.class));
        });
    }
}
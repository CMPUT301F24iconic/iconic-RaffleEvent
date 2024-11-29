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
    private Button manageImagesButton;
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
        manageImagesButton = findViewById(R.id.manage_images_button);
        manageQRCodeButton = findViewById(R.id.manage_qr_code_button);
        manageFacilitiesButton = findViewById(R.id.manage_facilities_button);
        backToRoleSelectionButton = findViewById(R.id.back_to_role_selection_button);

//        // Show password dialog to confirm admin access
//        if (!isAdminAuthenticated) {
//            showPasswordDialog();
//        }

        // Set up button listeners (only active if authenticated)
        manageUsersButton.setOnClickListener(v -> {
            startActivity(new Intent(this, UserListActivity.class));
        });

        manageEventsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, EventListForAdminActivity.class));
        });

        manageImagesButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ImageManagementActivity.class));
        });

        manageQRCodeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, QRCodeManagementActivity.class));
        });

        manageFacilitiesButton.setOnClickListener(v -> {
            startActivity(new Intent(this, FacilityListForAdminActivity.class));
        });

        backToRoleSelectionButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RoleSelectionActivity.class));
        });
    }

//    /**
//     * Displays a dialog prompting the user to enter the admin password.
//     * If the password is correct, the user gains access to administrative features.
//     * If the password is incorrect, the activity closes.
//     */
//    private void showPasswordDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Admin Password");
//
//        // Set up the input field for password
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(input);
//
//        // Set up dialog buttons
//        builder.setPositiveButton("OK", (dialog, which) -> {
//            String password = input.getText().toString();
//            verifyPassword(password);
//        });
//        builder.setNegativeButton("Cancel", (dialog, which) -> {
//            dialog.cancel();
//            finish();  // Close activity if admin access is denied
//        });
//
//        builder.show();
//    }
//
//    /**
//     * Verifies the entered password against the stored admin password.
//     * If the password is correct, sets the admin authentication flag to true.
//     * If the password is incorrect, displays a toast and closes the activity.
//     *
//     * @param password The password entered by the user.
//     */
//    private void verifyPassword(String password) {
//        if (password.equals(ADMIN_PASSWORD)) {
//            isAdminAuthenticated = true;
//            Toast.makeText(this, "Admin access granted", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
//            finish(); // Close activity on incorrect password
//        }
//    }
}
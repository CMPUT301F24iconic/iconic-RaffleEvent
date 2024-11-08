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

public class AdminHubActivity extends AppCompatActivity {
    private static final String ADMIN_PASSWORD = "adminPass123"; // Set your actual admin password here

    private Button manageUsersButton;
    private Button manageEventsButton;
    private Button manageImagesButton;
    private Button manageQRCodeButton;
    private boolean isAdminAuthenticated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_hub);

        // Initialize buttons
        manageUsersButton = findViewById(R.id.manage_users_button);
        manageEventsButton = findViewById(R.id.manage_events_button);
        manageImagesButton = findViewById(R.id.manage_images_button);
        manageQRCodeButton = findViewById(R.id.manage_qr_code_button);

        // Show password dialog to confirm admin access
        if (!isAdminAuthenticated) {
            showPasswordDialog();
        }

        // Set up button listeners (only active if authenticated)
        manageUsersButton.setOnClickListener(v -> {
            if (isAdminAuthenticated) {
                startActivity(new Intent(this, UserListActivity.class));
            } else {
                showPasswordDialog();
            }
        });

        manageEventsButton.setOnClickListener(v -> {
            if (isAdminAuthenticated) {
                startActivity(new Intent(this, EventListForAdminActivity.class));
            } else {
                showPasswordDialog();
            }
        });

        manageImagesButton.setOnClickListener(v -> {
            if (isAdminAuthenticated) {
                startActivity(new Intent(this, ImageManagementActivity.class));
            } else {
                showPasswordDialog();
            }
        });

        manageQRCodeButton.setOnClickListener(v -> {
            if (isAdminAuthenticated) {
                startActivity(new Intent(this, QRCodeManagementActivity.class));
            } else {
                showPasswordDialog();
            }
        });
    }

    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Admin Password");

        // Set up the input field for password
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up dialog buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String password = input.getText().toString();
            verifyPassword(password);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            finish();  // Close activity if admin access is denied
        });

        builder.show();
    }

    private void verifyPassword(String password) {
        if (password.equals(ADMIN_PASSWORD)) {
            isAdminAuthenticated = true;
            Toast.makeText(this, "Admin access granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show();
            finish(); // Close activity on incorrect password
        }
    }
}
package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

public class NotificationSettingsActivity extends AppCompatActivity {

    private UserController userController;
    private User userObj;

    private Switch winSwitch;
    private Switch loseSwitch;
    private Switch enableSwitch;
    private Button saveButton;
    private Button backButton;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initializeViews();
        initializeController();
        setupListeners();
        loadUserNotificationsPreferences();
    }

    private void initializeViews() {
        winSwitch = findViewById(R.id.win_notification_switch);
        loseSwitch = findViewById(R.id.lose_notification_switch);
        enableSwitch = findViewById(R.id.enable_notification_switch);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
    }

    private void initializeController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveNotificationSettings());
        backButton.setOnClickListener(v -> navigateToNotifications());

        // Enable notifications switch logic
        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    // Turn win and lose preference switches off
                    winSwitch.setChecked(false);
                    loseSwitch.setChecked(false);
                    // Disable the two switches
                    winSwitch.setEnabled(false);
                    loseSwitch.setEnabled(false);
                } else {
                    // Enable the two switches
                    winSwitch.setEnabled(true);
                    loseSwitch.setEnabled(true);
                }

            }
        });

        // Footer buttons logic with lambda expressions
        homeButton.setOnClickListener(v -> startActivity(new Intent(this, EventListActivity.class)));
        qrButton.setOnClickListener(v -> startActivity(new Intent(this, QRScannerActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void saveNotificationSettings() {
        if (userObj == null) {
            Toast.makeText(this, "Unable to save settings: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean winNotificationEnabled = winSwitch.isChecked();
        boolean loseNotificationEnabled = loseSwitch.isChecked();
        boolean notificationsEnabled = enableSwitch.isChecked();

        try {
            userController.setWinNotificationsEnabled(userObj, winNotificationEnabled);
            userController.setLoseNotificationsEnabled(userObj, loseNotificationEnabled);
            userController.setNotificationsEnabled(userObj, notificationsEnabled);

            Toast.makeText(this, "Notification settings saved", Toast.LENGTH_SHORT).show();
            navigateToNotifications();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserNotificationsPreferences() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    runOnUiThread(() -> updateSwitchStates(user));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(NotificationSettingsActivity.this,
                                    "Unable to load user preferences", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(NotificationSettingsActivity.this,
                                "Error loading preferences: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private void updateSwitchStates(User user) {
        winSwitch.setChecked(user.isWinNotificationPref());
        loseSwitch.setChecked(user.isLoseNotificationPref());
        enableSwitch.setChecked(user.isNotificationsEnabled());
    }

    private void navigateToNotifications() {
        startActivity(new Intent(this, NotificationsActivity.class));
        finish();
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
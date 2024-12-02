package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

/**
 * Activity that handles user notification settings.
 * This activity allows the user to enable or disable notifications for win and lose events,
 * as well as enable or disable all notifications.
 */
public class NotificationSettingsActivity extends AppCompatActivity {
    private UserController userController;
    private User userObj;

    private Switch generalSwitch;
    private Button saveButton;
    private ImageButton backButton;

    private boolean switchesChanged = false;
    // initial states of switches
    private boolean initialGeneralState;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;

    /**
     * Called when the activity is created.
     * Initializes views, controller, and listeners, and loads the user's notification preferences.
     *
     * @param savedInstanceState The saved instance state if available.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initializeViews();
        initializeController();
        setupListeners();
        loadUserNotificationsPreferences();
    }

    /**
     * Initializes all views used in this activity.
     * This includes switches, buttons, and image buttons for navigation.
     */
    private void initializeViews() {
        generalSwitch = findViewById(R.id.general_notification_switch);
        saveButton = findViewById(R.id.save_button);
        saveButton.setVisibility(View.GONE);  // hide save button until changes made
        backButton = findViewById(R.id.back_button);
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
    }

    /**
     * Initializes the UserController and sets it up using the user's ID.
     */
    private void initializeController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    /**
     * Sets up the listeners for buttons and switches in this activity.
     * This includes the save button, back button, and switches for enabling/disabling notifications.
     */
    private void setupListeners() {
        saveButton.setOnClickListener(v -> saveNotificationSettings());
        backButton.setOnClickListener(v -> navigateToNotifications());

        // Enable notifications switch logic
        generalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != initialGeneralState) {
                switchesChanged = true;
                updateSaveButtonVisibility();
            }
        });

        // Footer buttons logic with lambda expressions
        homeButton.setOnClickListener(v -> startActivity(new Intent(this, EventListActivity.class)));
        qrButton.setOnClickListener(v -> startActivity(new Intent(this, QRScannerActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    /**
     * Makes save button visible if switches have been changed
     */
    private void updateSaveButtonVisibility() {
        if (switchesChanged) {
            saveButton.setVisibility(View.VISIBLE);
        } else {
            saveButton.setVisibility(View.GONE);
        }
    }

    /**
     * Saves the user's notification preferences by updating the UserController.
     * Shows a toast with the result of the save operation.
     */
    private void saveNotificationSettings() {
        if (userObj == null) {
            Toast.makeText(this, "Unable to save settings: User data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        switchesChanged = false;

        boolean generalNotificationEnabled = generalSwitch.isChecked();
        if (generalNotificationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Check for permission POST_NOTIFICATIONS
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                    return; // Exit if permission is not granted yet
                }
            }
        }

        try {
            userController.setGeneralNotificationsEnabled(userObj, generalNotificationEnabled);
            Toast.makeText(this, "Notification settings saved", Toast.LENGTH_SHORT).show();
            navigateToNotifications();
        } catch (Exception e) {
            Toast.makeText(this, "Error saving settings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads the user's notification preferences from the UserController.
     * Once the data is loaded, it updates the switch states based on the user's preferences.
     */
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

    /**
     * Updates the state of the notification switches based on the provided user's preferences.
     *
     * @param user The user object containing the notification preferences.
     */
    private void updateSwitchStates(User user) {
        generalSwitch.setChecked(user.isGeneralNotificationPref());

        // save initial values
        initialGeneralState = generalSwitch.isChecked();
        switchesChanged = false;
        updateSaveButtonVisibility();
    }

    /**
     * Navigates to the NotificationsActivity.
     * This method is called after successfully saving the notification settings.
     */
    private void navigateToNotifications() {
        startActivity(new Intent(this, NotificationsActivity.class));
        finish();
    }

    /**
     * Gets the unique user ID (Android ID) for the current device.
     *
     * @return The Android ID as a String.
     */
    @SuppressLint("HardwareIds")
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Cleans up any resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
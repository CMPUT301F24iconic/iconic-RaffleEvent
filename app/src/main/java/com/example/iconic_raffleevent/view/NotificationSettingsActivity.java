package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;

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

        userController = getUserController();

        winSwitch = findViewById(R.id.win_notification_switch);
        loseSwitch = findViewById(R.id.lose_notification_switch);
        enableSwitch = findViewById(R.id.enable_notification_switch);
        saveButton = findViewById(R.id.save_button);
        backButton = findViewById(R.id.back_button);

        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);


        // Load the switches to user preferences
        loadUserNotificationsPreferences();

        saveButton.setOnClickListener(v -> {
            boolean winNotificationEnabled = winSwitch.isChecked();
            boolean loseNotificationEnabled = loseSwitch.isChecked();
            boolean notificationsEnabled = enableSwitch.isChecked();

            userController.setWinNotificationsEnabled(userObj, winNotificationEnabled);
            userController.setLoseNotificationsEnabled(userObj, loseNotificationEnabled);
            userController.setNotificationsEnabled(userObj, notificationsEnabled);

            // redirect back to notifications activity after saving
            startActivity(new Intent(NotificationSettingsActivity.this, NotificationsActivity.class));
        });

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(NotificationSettingsActivity.this, NotificationsActivity.class));
        });


        // Footer buttons logic
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationSettingsActivity.this, EventListActivity.class));
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationSettingsActivity.this, QRScannerActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationSettingsActivity.this, ProfileActivity.class));
            }
        });
    }

    private void loadUserNotificationsPreferences() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    winSwitch.setChecked(user.isWinNotificationPref());
                    loseSwitch.setChecked(user.isLoseNotificationPref());
                    enableSwitch.setChecked(user.isNotificationsEnabled());
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Cannot fetch user information");
            }
        });
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }
}

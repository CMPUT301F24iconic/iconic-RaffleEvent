package com.example.iconic_raffleevent.view;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

import java.util.Set;

/**
 * MainActivity serves as the home screen of the application.
 * It provides navigation options for users to view events, their profile, or create new events.
 */

public class MainActivity extends AppCompatActivity {

    private Button profileButton;
    private Button eventsButton;
    private Button notificationsButton;
    private Button scanQRCodeButton;

    private UserControllerViewModel userControllerViewModel;
    private UserController userController;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         Get Device ID and check if there is an existing user with that ID
         */
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("Device ID: " + deviceID);

        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceID);
        userController = userControllerViewModel.getUserController();

        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user == null) {
                    // User null, redirect to create user page to make a user
                    startActivity(new Intent(MainActivity.this, NewUserActivity.class));
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("There was an error");
            }
        });

        profileButton = findViewById(R.id.profile_button);
        eventsButton = findViewById(R.id.events_button);
        notificationsButton = findViewById(R.id.notifications_button);
        scanQRCodeButton = findViewById(R.id.scan_qr_code_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EventListActivity.class));
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NotificationsActivity.class));
            }
        });

        scanQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QRScannerActivity.class));
            }
        });
    }

    /*
    private User getCurrentUser() {
        // Placeholder implementation. Replace with actual logic to get the current user.
        User user = new User();
        user.setUserId("user123");
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        return user;
    }
     */
}
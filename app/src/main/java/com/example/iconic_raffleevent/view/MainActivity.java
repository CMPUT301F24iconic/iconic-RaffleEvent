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
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;
    private User userObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Retrieve Device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceID == null || deviceID.isEmpty()) {
            System.out.println("Error: Device ID is null or empty");
            return; // Exit early if deviceID is not valid
        }

        System.out.println("Device ID: " + deviceID);

        // Set up ViewModel
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceID);
        userController = userControllerViewModel.getUserController();

        // Fetch User Information
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user == null) {
                    // No user found, navigate to create user screen
                    startActivity(new Intent(MainActivity.this, NewUserActivity.class));
                } else if (user.checkAdminRole()) {
                    // User is admin, navigate to role selection screen
                    startActivity(new Intent(MainActivity.this, RoleSelectionActivity.class));
                } else {
                    // User is not admin, navigate to event list screen
                    startActivity(new Intent(MainActivity.this, EventListActivity.class));
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user information: " + message);
            }
        });
    }
}
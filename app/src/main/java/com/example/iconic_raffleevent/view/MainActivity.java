package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

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

        initializeUserController();
        checkUserAndNavigate();
    }

    private void initializeUserController() {
        // Retrieve Device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceID == null || deviceID.isEmpty()) {
            System.out.println("Error: Device ID is null or empty");
            return; // Exit early if deviceID is not valid
        }

        System.out.println("Device ID: " + deviceID);

        // Set up ViewModel with context
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceID, getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    private void checkUserAndNavigate() {
        if (userController == null) {
            System.out.println("Error: UserController not initialized");
            return;
        }

        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user == null) {
                    // No user found, navigate to create user screen
                    navigateToActivity(NewUserActivity.class);
                } else if (user.checkAdminRole()) {
                    // User is admin, navigate to role selection screen
                    navigateToActivity(RoleSelectionActivity.class);
                } else {
                    // User is not admin, navigate to event list screen
                    navigateToActivity(EventListActivity.class);
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user information: " + message);
            }
        });
    }

    private void navigateToActivity(Class<?> activityClass) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, activityClass);
            startActivity(intent);
            finish(); // Close MainActivity after navigation
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
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
 * Upon the initial launch of the application, this activity will display a welcome screen as it fetches user data.
 * Depending on if the user exists in the database, and their current permissions, main activity will redirect the
 * user to the appropriate screen
 */
public class MainActivity extends AppCompatActivity {
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;
    private User userObj;

    /**
     * Initializes the activity and sets up the initial view.
     * @param savedInstanceState Bundle containing saved state data, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUserController();
        checkUserAndNavigate();
    }

    /**
     * Initializes the UserController by setting the device ID and context.
     * The method retrieves the device ID, checks its validity, and sets up the
     * UserController instance within the ViewModel.
     */
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

    /**
     * Checks the user status and navigates to the appropriate screen based on their role.
     * If no user is found, the user is redirected to the New User screen.
     * If the user has an admin role, they are redirected to the Role Selection screen.
     * Otherwise, the user is redirected to the Event List screen.
     */
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

    /**
     * Navigates to a specified activity.
     * @param activityClass the class of the activity to navigate to.
     */
    private void navigateToActivity(Class<?> activityClass) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, activityClass);
            startActivity(intent);
            finish(); // Close MainActivity after navigation
        });
    }

    /**
     * Handles the destruction of the activity, releasing resources as needed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
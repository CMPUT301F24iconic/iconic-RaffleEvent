package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.notificationservice.FirestoreListenerService;

/**
 * The main activity for the Iconic Raffle Event application.
 * This activity initializes the user controller, checks user information,
 * and navigates to the appropriate screen based on the user's role.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;

    /**
     * Called when the activity is first created. Sets the layout, initializes
     * the user controller, and checks the user's status to navigate accordingly.
     *
     * @param savedInstanceState The previously saved instance state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!initializeUserController()) {
            showToast("Error initializing app. Please restart.");
            return;
        }

        checkUserAndNavigate();
    }

    /**
     * Initializes the user controller using the device's unique ID.
     *
     * @return True if the user controller was successfully initialized, false otherwise.
     */
    public boolean initializeUserController() {
        try {
            String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (deviceID == null || deviceID.isEmpty()) {
                Log.e(TAG, "Device ID is null or empty.");
                showToast("Device ID error. Please restart.");
                return false;
            }

            userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
            userControllerViewModel.setUserController(deviceID, getApplicationContext());
            userController = userControllerViewModel.getUserController();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UserController", e);
            showToast("Initialization error. Please restart.");
            return false;
        }
    }

    /**
     * Checks the user's information and navigates to the appropriate activity.
     * New users are redirected to registration, while existing users are directed
     * to their respective roles.
     */
    public void checkUserAndNavigate() {
        if (userController == null) {
            Log.e(TAG, "UserController is not initialized.");
            showToast("App error. Please restart.");
            return;
        }

        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user == null) {
                    Log.i(TAG, "User not found. Redirecting to registration.");
                    navigateToActivity(NewUserActivity.class); // Direct new users to registration
                } else if (user.checkAdminRole()) {
                    navigateToActivity(RoleSelectionActivity.class);
                } else {
                    navigateToActivity(EventListActivity.class);
                }
            }
            @Override
            public void onError(String message) {
                Log.e(TAG, "Error fetching user information: " + message);
                showToast("Error loading user information: " + message);
                navigateToActivity(NewUserActivity.class); // Redirect to registration if user not found
            }
        });
    }

    /**
     * Navigates to the specified activity class and closes the current activity.
     *
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(MainActivity.this, FirestoreListenerService.class);
        startService(intent);
        runOnUiThread(() -> {
            Intent intent2 = new Intent(MainActivity.this, activityClass);
            startActivity(intent2);
            finish(); // Close MainActivity after navigation
        });
    }

    /**
     * Displays a toast message on the UI thread.
     *
     * @param message The message to display in the toast.
     */
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    /**
     * Cleans up resources when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
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

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;

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

    private boolean initializeUserController() {
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

    private void checkUserAndNavigate() {
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

    private void navigateToActivity(Class<?> activityClass) {
        runOnUiThread(() -> {
            Intent intent = new Intent(MainActivity.this, activityClass);
            startActivity(intent);
            finish(); // Close MainActivity after navigation
        });
    }

    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}

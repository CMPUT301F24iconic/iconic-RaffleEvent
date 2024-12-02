package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.notificationservice.FirestoreListenerService;

/**
 * NewUserActivity is responsible for handling the creation of a new user within the application.
 * It provides a user interface for entering a name and submitting it to create a new account.
 */
public class NewUserActivity extends AppCompatActivity {
    private EditText nameEditText;
    private Button joinAppButton;
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;

    /**
     * Initializes the activity, sets up the layout, and initializes views, the controller, and event listeners.
     * @param savedInstanceState Bundle containing saved state data, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        initializeViews();
        initializeController();
        setupListeners();
    }

    /**
     * Initializes the views in the layout by finding them by ID.
     */
    private void initializeViews() {
        nameEditText = findViewById(R.id.edit_name_field);
        joinAppButton = findViewById(R.id.join_app_button);
    }

    /**
     * Initializes the UserController by setting the device ID and context.
     * Retrieves the device ID and sets up the UserController instance within the ViewModel.
     */
    private void initializeController() {
        String deviceId = getUserId();
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceId, getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    /**
     * Sets up event listeners for user interactions, such as clicking the "Join App" button.
     */
    private void setupListeners() {
        joinAppButton.setOnClickListener(v -> createNewUser());
    }

    /**
     * Creates a new user with the entered name. If successful, a toast message
     * is displayed, and the user is redirected to the Event List screen. If an error occurs,
     * a toast message with the error is displayed.
     */
    private void createNewUser() {
        String name = nameEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("This field cannot be empty");
            return;
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setUserId(getUserId());

        // Now add user to database
        userController.addUser(newUser, new UserController.AddUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(NewUserActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                navigateToEventList();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(NewUserActivity.this, "Error creating account: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Retrieves the unique device ID used as the user ID.
     * @return a String representing the device ID.
     */
    private String getUserId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Navigates to the Event List screen and clears the back stack to prevent returning to this screen.
     */
    private void navigateToEventList() {
        Intent intent = new Intent(NewUserActivity.this, FirestoreListenerService.class);
        startService(intent);
        Intent intent2 = new Intent(NewUserActivity.this, EventListActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent2);
        finish();
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

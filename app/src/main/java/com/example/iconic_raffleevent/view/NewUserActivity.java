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

public class NewUserActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private Button joinAppButton;
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        initializeViews();
        initializeController();
        setupListeners();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.edit_username_field);
        joinAppButton = findViewById(R.id.join_app_button);
    }

    private void initializeController() {
        String deviceId = getUserId();
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceId, getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    private void setupListeners() {
        joinAppButton.setOnClickListener(v -> createNewUser());
    }

    private void createNewUser() {
        String username = usernameEditText.getText().toString().trim();

        if (username.isEmpty()) {
            usernameEditText.setError("This field cannot be empty");
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setUserId(getUserId());

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

    private String getUserId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void navigateToEventList() {
        Intent intent = new Intent(NewUserActivity.this, EventListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}

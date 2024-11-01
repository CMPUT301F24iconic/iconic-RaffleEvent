package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;

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

        usernameEditText = findViewById(R.id.edit_username_field);
        joinAppButton = findViewById(R.id.join_app_button);

        joinAppButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            if (username.isEmpty()) {
                usernameEditText.setError("This field cannot be empty");
            } else {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setUserId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

                userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
                userControllerViewModel.setUserController(newUser.getUserId());
                userController = userControllerViewModel.getUserController();
                userController.addUser(newUser);

                // Just sending to profile activity for now to see if user is created
                startActivity(new Intent(NewUserActivity.this, ProfileActivity.class));}
        });

    }
}

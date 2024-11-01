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
                else if (user.checkAdminRole() == Boolean.TRUE) {
                    startActivity(new Intent(MainActivity.this, RoleSelectionActivity.class));
                }
                else {
                    startActivity(new Intent(MainActivity.this, HubActivity.class));
                }
            }
            @Override
            public void onError(String message) {
                System.out.println("There was an error");
            }
        });
    }
}
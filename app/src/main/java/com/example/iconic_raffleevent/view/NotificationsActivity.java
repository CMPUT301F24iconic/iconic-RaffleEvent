package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.NotificationController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity class to display the list of notifications for the user.
 * This activity handles displaying notifications, as well as setting up the navigation drawer
 * and other buttons for navigating to different parts of the app.
 */
public class NotificationsActivity extends AppCompatActivity {
    private ListView notificationListView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private NotificationController notificationController;

    private ImageButton settingsButton;

    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;

     private ImageButton menuButton;
     private DrawerLayout drawerLayout;
     private NavigationView navigationView;

    private UserController userController;
    private User userObj;

    /**
     * Initializes the activity, sets the content view, and initializes the views and listeners.
     * Also sets up the drawer and fetches notifications from the NotificationController.
     *
     * @param savedInstanceState If the activity is being reinitialized, this contains the previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initializeViews();
        initializeUserController();
        loadUserProfile();
        setupListeners();

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);
        notificationController = new NotificationController();

        fetchNotifications();

    }

    /**
     * Initializes all the views, including buttons, ListView, and the navigation drawer components.
     */
    private void initializeViews() {
        // Initialize list view
        notificationListView = findViewById(R.id.notification_list);

        // Initialize buttons
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        settingsButton = findViewById(R.id.settings_icon);

        // Setting up hamburger button
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuButton = findViewById(R.id.menu_button);
    }

    private void initializeUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    DrawerHelper.setupDrawer(NotificationsActivity.this, drawerLayout, navigationView, userObj.getUserId());
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Cannot fetch user information: " + message);
            }
        });
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Sets up the listeners for various buttons (settings, home, QR, profile, and menu).
     * These listeners trigger navigation or actions when the respective buttons are clicked.
     */
    private void setupListeners() {
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationsActivity.this, NotificationSettingsActivity.class));
            }
        });

        // Footer buttons logic
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationsActivity.this, EventListActivity.class));
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationsActivity.this, QRScannerActivity.class));
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationsActivity.this, ProfileActivity.class));
            }
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    /**
     * Fetches the notifications for the user using the NotificationController.
     * The notifications are then updated in the notification list and displayed using the adapter.
     */
    private void fetchNotifications() {
        notificationController.getNotifications(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID),
                new NotificationController.GetNotificationsCallback() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                if (notifications != null && !notifications.isEmpty()) {
                    notificationList.clear();
                    notificationList.addAll(notifications);
                    notificationAdapter.notifyDataSetChanged();
                    // System.out.println(notifications);
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(NotificationsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

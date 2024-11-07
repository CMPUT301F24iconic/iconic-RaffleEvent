package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.NotificationController;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private ListView notificationListView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    private NotificationController notificationController;
    private User currentUser;

    private Button settingsButton;

    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notificationListView = findViewById(R.id.notification_list);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(this, notificationList);
        notificationListView.setAdapter(notificationAdapter);
        notificationController = new NotificationController();

        currentUser = getCurrentUser();

        settingsButton = findViewById(R.id.notification_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotificationsActivity.this, NotificationSettingsActivity.class));
            }
        });

        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);

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

        fetchNotifications();

    }

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




    private User getCurrentUser() {
        // Placeholder implementation. Replace with actual logic to get the current user.
        User user = new User();
        user.setUserId("user123");
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        return user;
    }
}
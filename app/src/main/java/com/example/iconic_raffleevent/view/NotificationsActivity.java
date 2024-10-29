package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
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

        fetchNotifications();
    }

    private void fetchNotifications() {
        notificationController.getNotifications(currentUser.getUserId(), new NotificationController.GetNotificationsCallback() {
            @Override
            public void onNotificationsFetched(List<Notification> notifications) {
                notificationList.clear();
                notificationList.addAll(notifications);
                notificationAdapter.notifyDataSetChanged();
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
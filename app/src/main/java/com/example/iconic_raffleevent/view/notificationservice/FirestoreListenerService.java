package com.example.iconic_raffleevent.view.notificationservice;

import static android.icu.number.NumberRangeFormatter.with;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.Manifest;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class FirestoreListenerService extends Service {
    private static final String CHANNEL_ID = "RAFFLE_EVENTS";
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        db = FirebaseFirestore.getInstance();
        CollectionReference notificationsCollection = db.collection("Notification");

        listenerRegistration = notificationsCollection.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null) {
                // Handle snapshot updates here (e.g., notify UI or trigger some action)
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED, MODIFIED:
                            // Get notification
                            HashMap<String, Object> notification = (HashMap<String, Object>) change.getDocument().getData();
                            if (notification.get("userId").toString().equals(getUserId())) {
                                // Generate Notification for user device
                                generateSystemNotification(notification);
                            }
                            break;
                        case REMOVED:
                            Log.d("FirestoreListener", "Removed document: " + change.getDocument().getData());
                            break;
                    }
                }
            }
        });

        return START_STICKY;
    }



    private void generateSystemNotification(HashMap<String, Object> notification) {
        // Create an explicit intent for an Activity in your app.
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setContentTitle(notification.get("notificationType").toString())
                .setContentText(notification.get("message").toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // NotificationManagerCompat to show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // Use a unique notification ID, e.g., 1
        int notificationId = 1;  // You can change this to dynamically generate or track notification IDs
        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AppNotification";
            String description = "Event Raffle Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    /**
     * Retrieves the unique device ID used as the user ID.
     * @return a String representing the device ID.
     */
    private String getUserId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onDestroy() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


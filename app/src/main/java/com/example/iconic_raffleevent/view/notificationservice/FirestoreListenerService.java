package com.example.iconic_raffleevent.view.notificationservice;

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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A background service that listens to Firestore updates for notifications.
 * It monitors changes to the "Notification" collection in Firestore and generates
 * system notifications for the device for changes in notifications relating to the device ID
 */
public class FirestoreListenerService extends Service {
    private static final String CHANNEL_ID = "RAFFLE_EVENTS";
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    /**
     * Called when the service is started. Initializes firestore and places a listener to check for
     * any updates in the Notification collection
     *
     * @param intent The Intent supplied to {@link android.content.Context#startService},
     * as given.  This may be null if the service is being restarted after
     * its process has gone away, and it had previously returned anything
     * except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with {@link #stopSelfResult(int)}.
     *
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AtomicReference<Boolean> initialAppLoad = new AtomicReference<>(Boolean.TRUE);
        AtomicInteger notiID = new AtomicInteger(1);

        createNotificationChannel();
        db = FirebaseFirestore.getInstance();
        CollectionReference notificationsCollection = db.collection("Notification");

        listenerRegistration = notificationsCollection.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                return;
            }
            if (snapshot != null) {
                // Handle snapshot updates here (e.g., notify UI or trigger some action)
                if (!initialAppLoad.get()) {
                    for (DocumentChange change : snapshot.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED, MODIFIED:
                                // Get notification
                                HashMap<String, Object> notification = (HashMap<String, Object>) change.getDocument().getData();
                                if (notification.get("userId").toString().equals(getUserId())) {
                                    // Generate Notification for user device
                                    generateSystemNotification(notification, notiID);
                                }
                                break;
                            case REMOVED:
                                Log.d("FirestoreListener", "Removed document: " + change.getDocument().getData());
                                break;
                        }
                    }
                }
                initialAppLoad.set(Boolean.FALSE);
            }
        });

        return START_STICKY;
    }

    /**
     * Create an android notification to be shown on device
     *
     * @param notification notification to be displayed
     * @param notiID ID of the system notification
     */
    private void generateSystemNotification(HashMap<String, Object> notification, AtomicInteger notiID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

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
        int notificationId = notiID.intValue();

        notificationManager.notify(notificationId, builder.build());
        notiID.set(notiID.get() + 1);
    }

    /**
     * Creates a notification channel for the app notifications if the sdk is 35 or higher
     */
    private void createNotificationChannel() {
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


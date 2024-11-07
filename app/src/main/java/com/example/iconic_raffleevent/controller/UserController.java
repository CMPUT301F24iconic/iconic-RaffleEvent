package com.example.iconic_raffleevent.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UserController {
    private static final String TAG = "UserController";
    private final FirebaseAttendee firebaseAttendee;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;
    private final String currentUserID;
    private final Context context;

    public UserController(String userID, Context context) {
        this.currentUserID = userID;
        this.context = context;
        this.firebaseAttendee = new FirebaseAttendee();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();

        // Initialize Firebase Storage settings
        firebaseStorage.setMaxUploadRetryTimeMillis(30000); // 30 seconds
        firebaseStorage.setMaxOperationRetryTimeMillis(30000);
    }

    // Basic constructor without context (for backward compatibility)
    public UserController(String userID) {
        this(userID, null);
    }

    // Adds a new user to the database
    public void addUser(User user) {
        firebaseAttendee.updateUser(user);
    }

    // Updates the profile with new details
    public void updateProfile(User user, String name, String email, String phoneNo) {
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNo(phoneNo);
        firebaseAttendee.updateUser(user);
    }

    // Uploads profile image to Firebase Storage
    public void uploadProfileImage(User user, Uri imageUri, ProfileImageUploadCallback callback) {
        if (imageUri == null) {
            callback.onError("Image URI is null");
            return;
        }

        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            callback.onError("Invalid user data");
            return;
        }

        try {
            // Log the initial upload attempt
            Log.d(TAG, "Starting image upload for user: " + user.getUserId());
            Log.d(TAG, "Image URI: " + imageUri.toString());

            // Generate unique filename
            String timestamp = String.valueOf(System.currentTimeMillis());
            String filename = "profile_" + timestamp + ".jpg";

            // Create the full storage path
            String storagePath = String.format("profile_images/%s/%s", user.getUserId(), filename);
            StorageReference imageRef = storageReference.child(storagePath);

            Log.d(TAG, "Storage path: " + storagePath);

            // Create file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            // Start upload task
            UploadTask uploadTask = imageRef.putFile(imageUri, metadata);

            // Add progress listener
            uploadTask.addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "Upload progress: " + progress + "%");
                    })
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get download URL after successful upload
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Log.d(TAG, "Upload successful. URL: " + downloadUrl);

                            // Update user profile with new image URL
                            user.setProfileImageUrl(downloadUrl);
                            firebaseAttendee.updateUser(user);

                            callback.onProfileImageUploaded(downloadUrl);
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                            callback.onError("Failed to get download URL: " + e.getMessage());
                        });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Upload failed: " + e.getMessage());
                        callback.onError("Upload failed: " + e.getMessage());
                    });

        } catch (Exception e) {
            Log.e(TAG, "Exception during upload: " + e.getMessage(), e);
            callback.onError("Error during upload: " + e.getMessage());
        }
    }

    // Removes profile image from Firebase Storage
    public void removeProfileImage(User user, ProfileImageRemovalCallback callback) {
        if (user == null || user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            callback.onError("No profile image to remove");
            return;
        }

        try {
            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(user.getProfileImageUrl());
            photoRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        user.setProfileImageUrl(null);
                        firebaseAttendee.updateUser(user);
                        callback.onProfileImageRemoved();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to remove image: " + e.getMessage());
                        callback.onError("Failed to remove image: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error removing profile image: " + e.getMessage());
            callback.onError("Error removing profile image: " + e.getMessage());
        }
    }

    // Enables or disables notifications for the user
    public void setNotificationsEnabled(User user, boolean enabled) {
        user.setNotificationsEnabled(enabled);
        firebaseAttendee.updateNotificationPreference(user);
    }

    // Sets win notifications preference for the user
    public void setWinNotificationsEnabled(User user, boolean enabled) {
        user.setWinNotificationPref(enabled);
        firebaseAttendee.updateWinNotificationPreference(user);
    }

    // Sets lose notifications preference for the user
    public void setLoseNotificationsEnabled(User user, boolean enabled) {
        user.setLoseNotificationPref(enabled);
        firebaseAttendee.updateLoseNotificationPreference(user);
    }

    // Retrieves the current user profile
    public void getUserInformation(UserFetchCallback callback) {
        firebaseAttendee.getUser(currentUserID, callback);
    }

    // Retrieves the user's location using FusedLocationProviderClient
    public void retrieveUserLocation(FusedLocationProviderClient fusedLocationClient, Context context,
                                     OnLocationReceivedCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError("Location permissions not granted");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        callback.onLocationReceived(userLocation);
                    } else {
                        callback.onError("Location unavailable");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error retrieving location: " + e.getMessage()));
    }

    // Callback interfaces
    public interface ProfileImageUploadCallback {
        void onProfileImageUploaded(String imageUrl);
        void onError(String message);
    }

    public interface ProfileImageRemovalCallback {
        void onProfileImageRemoved();
        void onError(String message);
    }

    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    public interface OnLocationReceivedCallback {
        void onLocationReceived(GeoPoint location);
        void onError(String message);
    }
}
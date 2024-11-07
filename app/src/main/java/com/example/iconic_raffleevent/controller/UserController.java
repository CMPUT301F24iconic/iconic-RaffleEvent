package com.example.iconic_raffleevent.controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

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
    private FirebaseAttendee firebaseAttendee;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String currentUserID;
    private Context context;

    // Constructor with just userID
    public UserController(String userID) {
        this.currentUserID = userID;
        this.firebaseAttendee = new FirebaseAttendee();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
    }

    // Constructor with both userID and context
    public UserController(String userID, Context context) {
        this(userID);
        this.context = context;
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

    public void updateUser(User user) {
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
            Log.d("UserController", "Starting image upload for user: " + user.getUserId());
            Log.d("UserController", "Image URI: " + imageUri.toString());

            // Generate unique filename
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String filename = "profile_" + timeStamp + ".jpg";

            // Create the full storage path
            String storagePath = String.format("profile_images/%s/%s", user.getUserId(), filename);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(storagePath);

            Log.d("UserController", "Storage path: " + storagePath);

            // Create file metadata
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            // Start upload task
            UploadTask uploadTask = imageRef.putFile(imageUri, metadata);

            // Add progress listener
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("UserController", "Upload progress: " + progress + "%");
            }).addOnSuccessListener(taskSnapshot -> {
                // Get download URL after successful upload
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("UserController", "Upload successful. URL: " + downloadUrl);

                    // Update user profile with new image URL
                    user.setProfileImageUrl(downloadUrl);
                    firebaseAttendee.updateUser(user);

                    callback.onProfileImageUploaded(downloadUrl);
                }).addOnFailureListener(e -> {
                    Log.e("UserController", "Failed to get download URL: " + e.getMessage());
                    callback.onError("Failed to get download URL: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                Log.e("UserController", "Upload failed: " + e.getMessage());
                callback.onError("Upload failed: " + e.getMessage());
            });

        } catch (Exception e) {
            Log.e("UserController", "Exception during upload: " + e.getMessage(), e);
            callback.onError("Error during upload: " + e.getMessage());
        }
    }

    // Removes profile image from Firebase Storage
    public void removeProfileImage(User user, ProfileImageRemovalCallback callback) {
        String imageUrl = user.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageUrl);
            photoRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        user.setProfileImageUrl(null);
                        firebaseAttendee.updateUser(user);
                        callback.onProfileImageRemoved();
                    })
                    .addOnFailureListener(e -> callback.onError("Failed to remove image: " + e.getMessage()));
        } else {
            callback.onError("No profile image to remove");
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
    public void retrieveUserLocation(FusedLocationProviderClient fusedLocationClient, Context context, OnLocationReceivedCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

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
        } else {
            callback.onError("Location permissions not granted");
        }
    }

    // Helper method to get MIME type from URI
    private String getMimeType(Uri uri) {
        if (context == null) return "image/jpeg";

        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = resolver.getType(uri);
        return type != null ? type : "image/jpeg";
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
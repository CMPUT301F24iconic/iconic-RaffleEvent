package com.example.iconic_raffleevent.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UserController {

    private FirebaseAttendee firebaseAttendee;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String currentUserID;

    public UserController(String userID) {
        this.currentUserID = userID;
        this.firebaseAttendee = new FirebaseAttendee();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
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

    // Uploads the profile image to Firebase Storage and updates the user profile with the image URL
    public void uploadProfileImage(User user, Uri imageUri, ProfileImageUploadCallback callback) {
        String userId = user.getUserId();
        StorageReference ref = storageReference.child("profile_images/" + userId + "/" + imageUri.getLastPathSegment());

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    user.setProfileImageUrl(downloadUrl);
                    firebaseAttendee.updateUser(user);  // Update user profile with the image URL
                    callback.onProfileImageUploaded(downloadUrl);
                }))
                .addOnFailureListener(e -> callback.onError("Failed to upload image: " + e.getMessage()));
    }

    // Removes the profile image from Firebase Storage and updates the user profile
    public void removeProfileImage(User user, ProfileImageRemovalCallback callback) {
        String imageUrl = user.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageUrl);
            photoRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        user.setProfileImageUrl(null);
                        firebaseAttendee.updateUser(user);  // Update user profile after removing image
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

    // Retrieves the user's location using FusedLocationProviderClient with explicit permission check
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

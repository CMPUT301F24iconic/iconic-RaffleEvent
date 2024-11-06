package com.example.iconic_raffleevent.controller;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.core.app.ActivityCompat;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.UUID;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController {

    // Aiden Teal
    private String currentUserID;

    private User currentUser;
    private FirebaseAttendee firebaseAttendee;

    //Zhiyuan Li - profile image upload and removal functionality
    private FirebaseStorage storage;

    // Zhiyuan - modified usercontroller for usercontrollerviewmodel.java
    public UserController(String deviceID) {
        this.firebaseAttendee = new FirebaseAttendee();
        this.storage = FirebaseStorage.getInstance();

        // Fetch the user based on deviceID, create if not found
        firebaseAttendee.getUser(deviceID, new UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
                } else {
                    // Create a new user if none exists
                    currentUser = new User();
                    currentUser.setUserId(deviceID);
                    addUser(currentUser);
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user: " + message);
            }
        });
    }

    /*
        Aiden Teal
        Test constructor to see if we can get User from firebase
     */
//    public UserController(String userID) {
//        this.firebaseAttendee = new FirebaseAttendee();
//        this.currentUserID = userID;
//    }

    /**
     * Updates the user's profile information in Firestore.
     */
    public void updateProfile(User userObj, String name, String email, String phoneNo) {
        userObj.setName(name);
        userObj.setEmail(email);
        userObj.setPhoneNo(phoneNo);
        saveProfileToDatabase(userObj);
    }


    public void uploadProfileImage(User userObj, Uri filePath, ProfileImageUploadCallback callback) {
        if (filePath != null) {
            // Generate a unique path for the image in Firebase Storage
            StorageReference ref = storage.getReference().child("profile_images/" + UUID.randomUUID().toString());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        userObj.setProfileImageUrl(downloadUrl);
                        saveProfileToDatabase(userObj);
                        callback.onProfileImageUploaded(downloadUrl);
                    }))
                    .addOnFailureListener(e -> callback.onError("Upload failed: " + e.getMessage()));
        } else {
            callback.onError("No image selected");
        }
    }

//    // Aiden Teal method
//    public void uploadProfileImage(User userObj, String imageUrl) {
//        userObj.setProfileImageUrl(imageUrl);
//        saveProfileToDatabase(userObj);
//    }

    // Aiden Teal method (Zhiyuan modified)
    /**
     * Removes the user's profile image from Firebase Storage and updates the profile image URL to null in Firestore.
     */
    public void removeProfileImage(User userObj, ProfileImageRemovalCallback callback) {
        String imageUrl = userObj.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference photoRef = storage.getReferenceFromUrl(imageUrl);
            photoRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        userObj.setProfileImageUrl(null);
                        saveProfileToDatabase(userObj);
                        callback.onProfileImageRemoved();
                    })
                    .addOnFailureListener(e -> callback.onError("Failed to remove profile picture"));
        } else {
            callback.onError("No profile picture to remove");
        }
    }

    public void joinWaitingList(String eventId) {
        currentUser.getWaitingListEventIds().add(eventId);
        saveWaitingListToDatabase();
    }

    public void leaveWaitingList(String eventId) {
        currentUser.getWaitingListEventIds().remove(eventId);
        saveWaitingListToDatabase();
    }

    public void acceptEventInvitation(String eventId) {
        currentUser.getWaitingListEventIds().remove(eventId);
        currentUser.getRegisteredEventIds().add(eventId);
        saveRegisteredEventsToDatabase();
    }

    public void declineEventInvitation(String eventId) {
        currentUser.getWaitingListEventIds().remove(eventId);
        saveWaitingListToDatabase();
    }

    // Aiden Teal method
    public void setNotificationsEnabled(User userObj, boolean enabled) {
        userObj.setNotificationsEnabled(enabled);
        saveNotificationPreferenceToDatabaseTest(userObj);
    }

    // Manh Duong Hoang
    public void setWinNotificationsEnabled(User userObj, boolean enabled) {
        userObj.setWinNotificationPref(enabled);
        saveWinNotificationPreferenceToDatabase(userObj);
    }

    public void setLoseNotificationsEnabled(User userObj, boolean enabled) {
        userObj.setLoseNotificationPref(enabled);
        saveLoseNotificationPreferenceToDatabase(userObj);
    }

    private void saveProfileToDatabase() {
        firebaseAttendee.updateUser(currentUser);
    }

    // Aiden Teal method, Zhiyuan removed "Test"
    private void saveProfileToDatabase(User userObj) {
        firebaseAttendee.updateUser(userObj);
    }

    private void saveWaitingListToDatabase() {
        firebaseAttendee.updateWaitingList(currentUser);
    }

    private void saveRegisteredEventsToDatabase() {
        firebaseAttendee.updateRegisteredEvents(currentUser);
    }

    // Aiden Teal method
    private void saveNotificationPreferenceToDatabaseTest(User userObj) {
        firebaseAttendee.updateNotificationPreference(userObj);
    }

    // Manh Duong Hoang
    private void saveWinNotificationPreferenceToDatabase(User userObj) {
        firebaseAttendee.updateWinNotificationPreference(userObj);
    }

    private void saveLoseNotificationPreferenceToDatabase(User userObj) {
        firebaseAttendee.updateLoseNotificationPreference(userObj);
    }
    //

    public User getCurrentUser() {
        return currentUser;
    }

    // Aiden Teal
    public void getUserInformation(UserFetchCallback callback) {
        firebaseAttendee.getUser(this.currentUserID, callback);
    }

    // Aiden Teal
    public void addUser(User newUser) {
        firebaseAttendee.updateUser(newUser);
    }

    //Zhiyuan Li
    public void retrieveUserLocation(FusedLocationProviderClient fusedLocationClient, Context context, OnLocationReceivedCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
                if (location != null) {
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();
                    GeoPoint userLocation = new GeoPoint(latitude, longitude);
                    if (callback != null) {
                        callback.onLocationReceived(userLocation);
                    }
                }
            });
        }
    }

    // Zhiyuan Li - Callback Interfaces
    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    public interface OnLocationReceivedCallback {
        void onLocationReceived(GeoPoint location);
    }

    public interface ProfileImageUploadCallback {
        void onProfileImageUploaded(String imageUrl);
        void onError(String message);
    }

    public interface ProfileImageRemovalCallback {
        void onProfileImageRemoved();
        void onError(String message);
    }
}
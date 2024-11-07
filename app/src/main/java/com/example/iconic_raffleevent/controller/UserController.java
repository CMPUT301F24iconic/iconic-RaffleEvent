package com.example.iconic_raffleevent.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController {

    // Aiden Teal
    private String currentUserID;

    private User currentUser;
    private FirebaseAttendee firebaseAttendee;

    public UserController(User currentUser) {
        this.currentUser = currentUser;
        this.firebaseAttendee = new FirebaseAttendee();
    }

    /*
        Aiden Teal
        Test constructor to see if we can get User from firebase
     */
    public UserController(String userID) {
        this.firebaseAttendee = new FirebaseAttendee();
        this.currentUserID = userID;
    }

    public void updateProfile(User userObj, String name, String email, String phoneNo) {
        userObj.setName(name);
        userObj.setEmail(email);
        userObj.setPhoneNo(phoneNo);

        saveProfileToDatabaseTest(userObj);
    }

    public void uploadProfileImage(String imageUrl) {
        currentUser.setProfileImageUrl(imageUrl);
        saveProfileToDatabase();
    }

    // Aiden Teal method
    public void uploadProfileImageTest(User userObj, String imageUrl) {
        userObj.setProfileImageUrl(imageUrl);
        saveProfileToDatabaseTest(userObj);
    }

    public void removeProfileImage() {
        currentUser.setProfileImageUrl(null);
        saveProfileToDatabase();
    }

    // Aiden Teal method
    public void removeProfileImage(User userObj) {
        userObj.setProfileImageUrl(null);
        saveProfileToDatabaseTest(userObj);
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
    //

    private void saveProfileToDatabase() {
        firebaseAttendee.updateUser(currentUser);
    }

    // Aiden Teal method
    private void saveProfileToDatabaseTest(User userObj) {
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

    public void retrieveUserLocation(FusedLocationProviderClient fusedLocationClient, Context context, OnLocationReceivedCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                GeoPoint userLocation = new GeoPoint(latitude, longitude);
                if (callback != null) {
                    callback.onLocationReceived(userLocation);
                }
            });
        }
    }

    // Callback Interfaces
    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    public interface OnLocationReceivedCallback {
        void onLocationReceived(GeoPoint location);
    }
}
package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController implements Serializable {

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

    /*
    public void setNotificationsEnabled(boolean enabled) {
        currentUser.setNotificationsEnabled(enabled);
        saveNotificationPreferenceToDatabase();
    } */

    // Aiden Teal method
    public void setNotificationsEnabled(User userObj, boolean enabled) {
        userObj.setNotificationsEnabled(enabled);
        saveNotificationPreferenceToDatabaseTest(userObj);
    }

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

    // Callback Interfaces
    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }
}
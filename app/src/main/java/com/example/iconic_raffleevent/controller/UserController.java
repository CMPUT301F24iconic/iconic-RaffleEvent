package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController {

    private User currentUser;
    private FirebaseAttendee firebaseAttendee;

    public UserController(User currentUser) {
        this.currentUser = currentUser;
        this.firebaseAttendee = new FirebaseAttendee();
    }

    public void updateProfile(String name, String email, String phoneNo) {
        if (name.isEmpty() || email.isEmpty()) {
            // Handle validation error
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);
        currentUser.setPhoneNo(phoneNo);

        saveProfileToDatabase();
    }

    public void uploadProfileImage(String imageUrl) {
        currentUser.setProfileImageUrl(imageUrl);
        saveProfileToDatabase();
    }

    public void removeProfileImage() {
        currentUser.setProfileImageUrl(null);
        saveProfileToDatabase();
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

    public void setNotificationsEnabled(boolean enabled) {
        currentUser.setNotificationsEnabled(enabled);
        saveNotificationPreferenceToDatabase();
    }

    private void saveProfileToDatabase() {
        firebaseAttendee.updateUser(currentUser);
    }

    private void saveWaitingListToDatabase() {
        firebaseAttendee.updateWaitingList(currentUser);
    }

    private void saveRegisteredEventsToDatabase() {
        firebaseAttendee.updateRegisteredEvents(currentUser);
    }

    private void saveNotificationPreferenceToDatabase() {
        firebaseAttendee.updateNotificationPreference(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
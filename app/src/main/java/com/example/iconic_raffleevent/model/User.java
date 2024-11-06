package com.example.iconic_raffleevent.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system.
 * Each user is associated with their device using the Android ID, and they can optionally provide
 * their email and phone number when signing up for events. Users can have multiple roles.
 **/

public class User {

    private String userId;
    private String username;
    private String name;
    private String email;
    private String phoneNo;
    private String profileImageUrl;
    private boolean locationPermission;
    private List<String> waitingListEventIds;
    private List<String> registeredEventIds;
    private boolean notificationsEnabled;
    private boolean winNotificationPref;
    private boolean loseNotificationPref;
    private Facility facility;

    // Needed for testing role selection page
    private List<String> roles;

    public User() {
        waitingListEventIds = new ArrayList<>();
        registeredEventIds = new ArrayList<>();
        notificationsEnabled = true;
        winNotificationPref = true;
        loseNotificationPref = true;
        locationPermission = false;
        userId = "";
        username = "";
        name = "";
        email = "";
        phoneNo = "";
        profileImageUrl = "";
        this.roles = new ArrayList<>();
        this.roles.add("entrant");
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }

    public List<String> getWaitingListEventIds() {
        return waitingListEventIds;
    }

    public void setWaitingListEventIds(List<String> waitingListEventIds) {
        this.waitingListEventIds = waitingListEventIds;
    }

    public void addWaitingListEvent(String waitlistID) {
        this.waitingListEventIds.add(waitlistID);
    }

    public List<String> getRegisteredEventIds() {
        return registeredEventIds;
    }

    public void setRegisteredEventIds(List<String> registeredEventIds) {
        this.registeredEventIds = registeredEventIds;
    }

    public void addRegisteredEvent(String registeredEventID) {
        this.registeredEventIds.add(registeredEventID);
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    // Duong Hoang
    public boolean isLoseNotificationPref() {
        return loseNotificationPref;
    }

    public void setLoseNotificationPref(boolean loseNotificationPref) {
        this.loseNotificationPref = loseNotificationPref;
    }

    public boolean isWinNotificationPref() {
        return winNotificationPref;
    }

    public void setWinNotificationPref(boolean winNotificationPref) {
        this.winNotificationPref = winNotificationPref;
    }
    //

    public List<String> getRoles() {
        return this.roles;
    }

    public void addOrganizerRole() {
        this.roles.add("organizer");
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public boolean checkAdminRole() {
        return this.roles.contains("admin");
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

//    /**
//     * Prompts the user to provide their email and phone number when signing up for an event.
//     * This can be called from the event sign-up process.
//     *
//     * @param email The email provided by the user.
//     * @param phoneNo The phone number provided by the user.
//     */
//    public void signUpForEvent(String name, String email, String phoneNo) {
//        this.name = name;
//        this.email = email;
//        this.phoneNo = phoneNo;
//    }
}
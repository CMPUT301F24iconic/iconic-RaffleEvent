package com.example.iconic_raffleevent.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system.
 * Each user is associated with their device using a unique user ID.
 * Users can provide optional details like email, phone, and a profile image.
 * Users can also have multiple roles, such as "entrant" or "organizer."
 */
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
    private String facilityId;
    private List<String> roles;

    // Constructor
    public User() {
        this.waitingListEventIds = new ArrayList<>();
        this.registeredEventIds = new ArrayList<>();
        this.notificationsEnabled = true;
        this.winNotificationPref = true;
        this.loseNotificationPref = true;
        this.locationPermission = false;
        this.userId = "";
        this.username = "";
        this.name = "";
        this.email = "";
        this.phoneNo = "";
        this.profileImageUrl = "";
        this.facilityId = "";
        this.roles = new ArrayList<>();
        this.roles.add("entrant");
    }

    // Getters and Setters

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

    public void addWaitingListEvent(String eventId) {
        this.waitingListEventIds.add(eventId);
    }

    public List<String> getRegisteredEventIds() {
        return registeredEventIds;
    }

    public void setRegisteredEventIds(List<String> registeredEventIds) {
        this.registeredEventIds = registeredEventIds;
    }

    public void addRegisteredEvent(String eventId) {
        this.registeredEventIds.add(eventId);
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isWinNotificationPref() {
        return winNotificationPref;
    }

    public void setWinNotificationPref(boolean winNotificationPref) {
        this.winNotificationPref = winNotificationPref;
    }

    public boolean isLoseNotificationPref() {
        return loseNotificationPref;
    }

    public void setLoseNotificationPref(boolean loseNotificationPref) {
        this.loseNotificationPref = loseNotificationPref;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        if (!roles.contains(role)) {
            this.roles.add(role);
        }
    }

    public void removeRole(String role) {
        this.roles.remove(role);
    }

    public boolean checkAdminRole() {
        return this.roles.contains("admin");
    }

    // Utility methods

    /**
     * Updates user information with provided name, email, and phone number.
     * Typically called during event sign-up.
     *
     * @param name    User's name.
     * @param email   User's email address.
     * @param phoneNo User's phone number.
     */
    public void signUpForEvent(String name, String email, String phoneNo) {
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
    }
}

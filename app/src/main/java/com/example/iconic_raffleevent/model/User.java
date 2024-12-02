package com.example.iconic_raffleevent.model;

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
    private boolean generalNotificationPref;
    private String facilityId;
    private List<String> roles;

    /**
     * Default constructor initializing user with default values.
     * Initializes lists for event IDs and roles, and sets default notification preferences.
     */
    public User() {
        this.waitingListEventIds = new ArrayList<>();
        this.registeredEventIds = new ArrayList<>();
        this.generalNotificationPref = false;
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

    /**
     * Gets the unique user ID.
     * @return The user ID as a String.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     * @param userId The user ID to set.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the username.
     * @return The username as a String.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the user's name.
     * @return The user's name as a String.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     * @param name The name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email address.
     * @return The user's email as a String.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     * @param email The email to set for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's phone number.
     * @return The user's phone number as a String.
     */
    public String getPhoneNo() {
        return phoneNo;
    }

    /**
     * Sets the user's phone number.
     * @param phoneNo The phone number to set for the user.
     */
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    /**
     * Gets the URL of the user's profile image.
     * @return The profile image URL as a String.
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Sets the URL of the user's profile image.
     * @param profileImageUrl The URL to set for the user's profile image.
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * Gets the user's location permission status.
     * @return True if location permission is granted, false otherwise.
     */
    public boolean isLocationPermission() {
        return locationPermission;
    }

    /**
     * Sets the user's location permission status.
     * @param locationPermission The location permission status to set.
     */
    public void setLocationPermission(boolean locationPermission) {
        this.locationPermission = locationPermission;
    }

    /**
     * Gets the list of event IDs the user is waiting for.
     * @return The list of waiting event IDs.
     */
    public List<String> getWaitingListEventIds() {
        return waitingListEventIds;
    }

    /**
     * Sets the list of event IDs the user is waiting for.
     * @param waitingListEventIds The list of waiting event IDs to set.
     */
    public void setWaitingListEventIds(List<String> waitingListEventIds) {
        this.waitingListEventIds = waitingListEventIds;
    }

    /**
     * Adds an event ID to the user's waiting list.
     * @param eventId The event ID to add to the waiting list.
     */
    public void addWaitingListEvent(String eventId) {
        this.waitingListEventIds.add(eventId);
    }

    /**
     * Gets the list of event IDs the user is registered for.
     * @return The list of registered event IDs.
     */
    public List<String> getRegisteredEventIds() {
        return registeredEventIds;
    }

    /**
     * Sets the list of event IDs the user is registered for.
     * @param registeredEventIds The list of registered event IDs to set.
     */
    public void setRegisteredEventIds(List<String> registeredEventIds) {
        this.registeredEventIds = registeredEventIds;
    }

    /**
     * Adds an event ID to the user's registered events.
     * @param eventId The event ID to add to the registered events list.
     */
    public void addRegisteredEvent(String eventId) {
        this.registeredEventIds.add(eventId);
    }

    /**
     * Checks if the user has opted for general notifications.
     * @return True if general notifications are enabled, false otherwise.
     */
    public boolean isGeneralNotificationPref() {
        return generalNotificationPref;
    }

    /**
     * Sets the general notifications preference for the user.
     * @param generalNotificationPref The general notification preference to set.
     */
    public void setGeneralNotificationPref(boolean generalNotificationPref) {
        this.generalNotificationPref = generalNotificationPref;
    }

    /**
     * Gets the facility ID associated with the user.
     * @return The facility ID as a String.
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the facility ID for the user.
     * @param facilityId The facility ID to set.
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the roles associated with the user.
     * @return A list of roles the user has.
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * Adds a role to the user.
     * @param role The role to add.
     */
    public void addRole(String role) {
        if (!roles.contains(role)) {
            this.roles.add(role);
        }
    }

    /**
     * Removes a role from the user.
     * @param role The role to remove.
     */
    public void removeRole(String role) {
        this.roles.remove(role);
    }

    /**
     * Checks if the user has the admin role.
     * @return True if the user has the "admin" role, false otherwise.
     */
    public boolean checkAdminRole() {
        return this.roles.contains("admin");
    }
}


package com.example.iconic_raffleevent.model;

import android.content.Context;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system.
 * Each user is associated with their device using the Android ID, and they can optionally provide
 * their email and phone number when signing up for events. Users can have multiple roles.
 */
public class User {
    private String userId;             // Unique identifier for the user (e.g., Android ID)
    private String username;           // Username entered by the user when first using the app
    private String name;               // Optional name entered during event sign up
    private String email;              // Optional email entered during event sign-up
    private String phoneNo;            // Optional phone number entered during event sign-up
    private List<Role> roles;          // List of roles the user has (USER, organizer, admin)

    /**
     * Enum to define different user roles.
     */
    public enum Role {
        USER, ORGANIZER, ADMIN
    }

    /**
     * Constructor that initializes the user with a username and generates a unique userId based on the device.
     *
     * @param context Android context to access system services (used to retrieve Android ID).
     * @param username The username entered by the user.
     */
    public User(Context context, String username) {
        this.username = username;
        this.userId = getDeviceId(context);  // Associate user with their device's Android ID
        this.roles = new ArrayList<>();      // Initialize the list of roles
        this.roles.add(Role.USER);           // Every user has the USER role by default
    }

    /**
     * Returns the Android ID of the device, which serves as a unique identifier for the user.
     *
     * @param context The context from which to retrieve system services.
     * @return The unique Android ID string of the device.
     */
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    // Getters and setters

    public String getUserId() {
        return userId;
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

    public List<Role> getRoles() {
        return roles;
    }

    /**
     * Assigns a role to the user.
     *
     * @param role The role to be added (e.g., USER, ORGANIZER, ADMIN).
     */
    public void addRole(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }

    /**
     * Removes a role from the user.
     *
     * @param role The role to be removed.
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param role The role to check for (e.g., USER, ORGANIZER, ADMIN).
     * @return True if the user has the role, false otherwise.
     */
    public boolean hasRole(Role role) {
        return roles.contains(role);
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
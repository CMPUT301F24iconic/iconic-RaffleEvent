package com.example.iconic_raffleevent.model;

import android.content.Context;
import android.provider.Settings;
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
    private List<Role> roles;

    public User(String userId, String username, String name, String email, String phoneNo, List<Role> roles) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.roles = roles;
    }
    public enum Role {
        ADMIN,
        ORGANIZER,
        USER
    }
    public User() {
        roles = new ArrayList<>();
    }

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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public void addRole(Role role) {
        roles.add(role);
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
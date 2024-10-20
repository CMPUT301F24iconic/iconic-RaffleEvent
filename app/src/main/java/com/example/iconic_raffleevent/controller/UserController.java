package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController {

    private User currentUser;

    public UserController(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Updates the user's profile information.
     *
     * @param name The updated name of the user.
     * @param email The updated email of the user.
     */
    public void updateProfile(String name, String email) {
        // Validate new profile data
        if (name.isEmpty() || email.isEmpty()) {
            // Handle validation error (e.g., show error message)
            return;
        }

        // Update the user's profile data
        currentUser.setName(name);
        currentUser.setEmail(email);

        // Logic to save updated profile to the database (e.g., Firebase)
        saveProfileToDatabase();
    }

    /**
     * Saves the updated profile information to the database (Firebase).
     */
    private void saveProfileToDatabase() {
        // Code to update Firebase with new user details
        // This can be integrated with the FirebaseModel
    }

    /**
     * Loads the user's profile from the database and returns it to the view.
     *
     * @param userId The ID of the user whose profile needs to be loaded.
     */
    public void loadUserProfile(String userId) {
        // Code to retrieve user profile from Firebase or other database
        // Example: firebaseModel.getUserProfile(userId)
    }
}
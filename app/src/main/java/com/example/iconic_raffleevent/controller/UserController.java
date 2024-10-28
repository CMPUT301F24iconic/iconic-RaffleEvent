package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;

/**
 * UserController manages user interactions such as profile management, registration, and authentication.
 */
public class UserController {

    private User currentUser;
    private FirebaseController firebaseController;

    public UserController(User currentUser) {
        // Controller to access firebase
        this.firebaseController = new FirebaseController();
        this.currentUser = currentUser;
    }

    /**
     * Updates the user's profile information.
     *
     * @param name The updated name of the user.
     * @param email The updated email of the user.
     */
    public void updateProfile(String name, String email, String phoneNo) {
        if (name.isEmpty() || email.isEmpty()) {
            // Handle validation error
            return;
        }

        currentUser.setName(name);
        currentUser.setEmail(email);

        if (!phoneNo.isEmpty()) {
            currentUser.setPhoneNo(phoneNo);
        }

        saveProfileToDatabase();
    }

    /**
     * Creates a new user and saves it to Firebase
     *
     * @param user The User object to add
     */
    public void addUserToDatabase(User user) {
        // Minor error checking (TODO: implement more)
        if (user.getName().isEmpty() || user.getRoles().isEmpty() || user.getEmail().isEmpty()
                || user.getUserId().isEmpty() || user.getUsername().isEmpty()) {
            // Handle error validation
            return;
        }

        firebaseController.addUser(user);
    }

    /**
     * Saves the updated profile information to the database (Firebase).
     */
    private void saveProfileToDatabase() {
        // Code to update Firebase with new user details
        // This can be integrated with the FirebaseModel
        firebaseController.updateUser(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
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
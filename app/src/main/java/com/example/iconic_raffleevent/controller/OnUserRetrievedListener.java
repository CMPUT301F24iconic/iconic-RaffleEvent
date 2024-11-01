package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;

/**
 * Interface for listening to user retrieval callbacks.
 *
 * This interface should be implemented by any class that wants to receive
 * notifications when a user has been retrieved from a data source.
 * Implementing classes must define the behavior of the
 * {@link #onUserRetrieved(User)} method to handle the retrieved user.
 */
public interface OnUserRetrievedListener {
    /**
     * Called when a user has been successfully retrieved.
     *
     * @param user The retrieved User object. If no user is found,
     *             this parameter will be null
     */
    void onUserRetrieved(User user);
}

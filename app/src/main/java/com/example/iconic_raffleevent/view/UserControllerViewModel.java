package com.example.iconic_raffleevent.view;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.iconic_raffleevent.controller.UserController;

/**
 * ViewModel class for managing an instance of UserController.
 * Ensures that the UserController is created and retained across configuration changes.
 */
public class UserControllerViewModel extends ViewModel {
    private UserController userController;

    /**
     * Sets up the UserController if it has not been initialized yet.
     * This method is only called once to avoid re-creating the controller.
     *
     * @param userID  the unique ID of the user.
     * @param context the context in which the UserController operates.
     */
    public void setUserController(String userID, Context context) {
        if (userController == null) {
            userController = new UserController(userID, context);
        }
    }

    /**
     * Retrieves the UserController instance.
     *
     * @return the UserController associated with this ViewModel.
     */
    public UserController getUserController() {
        return userController;
    }
}

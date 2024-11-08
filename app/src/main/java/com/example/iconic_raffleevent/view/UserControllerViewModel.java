package com.example.iconic_raffleevent.view;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.iconic_raffleevent.controller.UserController;

public class UserControllerViewModel extends ViewModel {
    private UserController userController;

    public void setUserController(String userID, Context context) {
        if (userController == null) {
            userController = new UserController(userID, context);
        }
    }

    public UserController getUserController() {
        return userController;
    }
}

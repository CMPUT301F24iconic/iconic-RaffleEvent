package com.example.iconic_raffleevent.view;

import android.provider.Settings;

import androidx.lifecycle.ViewModel;

import com.example.iconic_raffleevent.controller.UserController;

public class UserControllerViewModel extends ViewModel {
    private UserController userController;

    public void setUserController(String deviceID) {
        if (userController == null) {
            userController = new UserController(deviceID);
        }
    }

    public UserController getUserController() {
        return this.userController;
    }
}

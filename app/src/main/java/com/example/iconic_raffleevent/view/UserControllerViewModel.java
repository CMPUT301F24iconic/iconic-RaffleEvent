package com.example.iconic_raffleevent.view;

import androidx.lifecycle.ViewModel;
import com.example.iconic_raffleevent.controller.UserController;

public class UserControllerViewModel extends ViewModel {
    private UserController userController;

    public void setUserController(String deviceID) {
        this.userController = new UserController(deviceID);
    }

    public UserController getUserController() {
        return this.userController;
    }
}

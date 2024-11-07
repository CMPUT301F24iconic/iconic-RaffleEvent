package com.example.iconic_raffleevent.view;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.example.iconic_raffleevent.controller.UserController;

public class UserControllerViewModel extends ViewModel {
    private UserController userController;

    public void setUserController(String deviceID, Context context) {
        if (userController == null) {
            userController = new UserController(deviceID, context);
        }
    }

    public UserController getUserController() {
        return this.userController;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        userController = null;
    }
}
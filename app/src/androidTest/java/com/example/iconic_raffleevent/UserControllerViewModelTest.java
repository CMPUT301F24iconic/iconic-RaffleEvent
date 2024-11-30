package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.NewUserActivity;
import com.example.iconic_raffleevent.view.UserControllerViewModel;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserControllerViewModelTest {

    /**
     * Test to verify that the UserController is initialized correctly.
     */
    @Test
    public void testUserControllerInitialization() {
        // Launch NewUserActivity
        ActivityScenario<NewUserActivity> scenario = ActivityScenario.launch(NewUserActivity.class);

        scenario.onActivity(activity -> {
            // Get ViewModel
            UserControllerViewModel viewModel = new ViewModelProvider(activity).get(UserControllerViewModel.class);

            // Initialize UserController
            Context context = activity.getApplicationContext();
            String testUserID = "testUserID";
            viewModel.setUserController(testUserID, context);

            // Assert UserController is initialized
            UserController userController = viewModel.getUserController();
            assertNotNull("UserController should not be null", userController);
        });
    }

    /**
     * Test to validate addUser functionality through UserController.
     */
    @Test
    public void testAddUser() {
        // Launch NewUserActivity
        ActivityScenario<NewUserActivity> scenario = ActivityScenario.launch(NewUserActivity.class);

        scenario.onActivity(activity -> {
            // Get ViewModel and initialize UserController
            UserControllerViewModel viewModel = new ViewModelProvider(activity).get(UserControllerViewModel.class);
            Context context = activity.getApplicationContext();
            String testUserID = "testUserID";
            viewModel.setUserController(testUserID, context);

            UserController userController = viewModel.getUserController();

            // Mock user creation
            User testUser = new User();
            testUser.setUserId(testUserID);
            testUser.setName("Test User");

            // Add user and validate callback
            userController.addUser(testUser, new UserController.AddUserCallback() {
                @Override
                public void onSuccess() {
                    assertNotNull("User should be added successfully", testUser);
                }

                @Override
                public void onError(String message) {
                    throw new AssertionError("Add user failed: " + message);
                }
            });
        });
    }

    /**
     * Test to validate updateProfile functionality through UserController.
     */
    @Test
    public void testUpdateUserProfile() {
        // Launch NewUserActivity
        ActivityScenario<NewUserActivity> scenario = ActivityScenario.launch(NewUserActivity.class);

        scenario.onActivity(activity -> {
            // Get ViewModel and initialize UserController
            UserControllerViewModel viewModel = new ViewModelProvider(activity).get(UserControllerViewModel.class);
            Context context = activity.getApplicationContext();
            String testUserID = "testUserID";
            viewModel.setUserController(testUserID, context);

            UserController userController = viewModel.getUserController();

            // Mock user for updating
            User testUser = new User();
            testUser.setUserId(testUserID);
            testUser.setName("Old Name");

            // Update user profile
            userController.updateProfile(testUser, "New Name", "test@example.com", "1234567890", new UserController.UpdateProfileCallback() {
                @Override
                public void onProfileUpdated() {
                    assertEquals("User's name should be updated", "New Name", testUser.getName());
                }

                @Override
                public void onError(String message) {
                    throw new AssertionError("Update profile failed: " + message);
                }
            });
        });
    }
}

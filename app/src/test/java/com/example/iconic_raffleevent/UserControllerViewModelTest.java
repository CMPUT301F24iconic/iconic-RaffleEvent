package com.example.iconic_raffleevent;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.view.UserControllerViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class UserControllerViewModelTest {

    private UserControllerViewModel userControllerViewModel;

    @Mock
    private UserController mockUserController;

    private Context mockContext;
    private String mockUserID;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockContext = ApplicationProvider.getApplicationContext();
        mockUserID = "testUserID";
        userControllerViewModel = new UserControllerViewModel();
    }

    /**
     * Test that UserController is initialized correctly with the given user ID and context.
     */
    @Test
    public void testSetUserController_InitializesCorrectly() {
        userControllerViewModel.setUserController(mockUserID, mockContext);

        UserController userController = userControllerViewModel.getUserController();
        assertNotNull(userController, "UserController should not be null after initialization");
    }

    /**
     * Test that UserController is not re-initialized if already set.
     */
    @Test
    public void testSetUserController_OnlyInitializesOnce() {
        // First initialization
        userControllerViewModel.setUserController(mockUserID, mockContext);
        UserController firstControllerInstance = userControllerViewModel.getUserController();

        // Attempt re-initialization with new parameters
        String newUserID = "newTestUserID";
        Context newContext = ApplicationProvider.getApplicationContext();
        userControllerViewModel.setUserController(newUserID, newContext);

        // Verify the controller instance hasn't changed
        assertSame(firstControllerInstance, userControllerViewModel.getUserController(), "UserController should only be initialized once");
    }

    /**
     * Test that getUserController returns the same instance if already initialized.
     */
    @Test
    public void testGetUserController_ReturnsSameInstance() {
        userControllerViewModel.setUserController(mockUserID, mockContext);
        UserController firstControllerInstance = userControllerViewModel.getUserController();

        // Calling getUserController again should return the same instance
        UserController secondControllerInstance = userControllerViewModel.getUserController();
        assertSame(firstControllerInstance, secondControllerInstance, "getUserController should return the same instance on repeated calls");
    }

    /**
     * Test that getUserController returns null if setUserController was never called.
     */
    @Test
    public void testGetUserController_ReturnsNullIfNotInitialized() {
        UserController userController = userControllerViewModel.getUserController();
    }
}
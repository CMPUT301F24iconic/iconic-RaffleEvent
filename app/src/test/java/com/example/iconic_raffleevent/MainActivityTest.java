package com.example.iconic_raffleevent;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.MainActivity;
import com.example.iconic_raffleevent.view.NewUserActivity;
import com.example.iconic_raffleevent.view.RoleSelectionActivity;
import com.example.iconic_raffleevent.view.UserControllerViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Mock
    private UserControllerViewModel userControllerViewModelMock;

    @Mock
    private UserController userControllerMock;

    private User mockUserAdmin;
    private User mockUserNonAdmin;
    private String deviceID;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Intents.init();

        deviceID = "testDeviceId"; // Mock device ID for testing

        // Mock admin and non-admin users
        mockUserAdmin = new User();
        mockUserAdmin.addRole("admin");

        mockUserNonAdmin = new User();

        when(userControllerViewModelMock.getUserController()).thenReturn(userControllerMock);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that MainActivity initializes UserController successfully.
     */
    @Test
    public void testUserControllerInitialization_Success() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                boolean isInitialized = activity.initializeUserController();
                assertTrue(isInitialized);
            });
        }
    }

    /**
     * Test that MainActivity handles UserController initialization failure.
     */
    @Test
    public void testUserControllerInitialization_Failure() {
        // Simulate null device ID to cause initialization failure
        doReturn(null).when(Settings.Secure.getString(any(Context.class).getContentResolver(), eq(Settings.Secure.ANDROID_ID)));

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                boolean isInitialized = activity.initializeUserController();
                assertFalse(isInitialized);
                onView(withText("Device ID error. Please restart.")).check(matches(isDisplayed()));
            });
        }
    }

    /**
     * Test navigation to RoleSelectionActivity when an admin user is fetched.
     */
    @Test
    public void testNavigateToAdminRoleSelection() {
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(mockUserAdmin); // Mock admin user
            return null;
        }).when(userControllerMock).getUserInformation(any(UserController.UserFetchCallback.class));

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(MainActivity::checkUserAndNavigate);
            intended(hasComponent(RoleSelectionActivity.class.getName()));
        }
    }

    /**
     * Test navigation to NewUserActivity when user is not found (new user registration).
     */
    @Test
    public void testNavigateToNewUserActivity_UserNotFound() {
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onError("User not found");
            return null;
        }).when(userControllerMock).getUserInformation(any(UserController.UserFetchCallback.class));

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(MainActivity::checkUserAndNavigate);
            intended(hasComponent(NewUserActivity.class.getName()));
        }
    }

    /**
     * Test navigation to EventListActivity when a non-admin user is fetched.
     */
    @Test
    public void testNavigateToEventListActivity_NonAdminUser() {
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(mockUserNonAdmin); // Mock non-admin user
            return null;
        }).when(userControllerMock).getUserInformation(any(UserController.UserFetchCallback.class));

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(MainActivity::checkUserAndNavigate);
            intended(hasComponent(EventListActivity.class.getName()));
        }
    }
}

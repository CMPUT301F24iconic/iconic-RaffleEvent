package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.net.Uri;

import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserControllerTest {

    @Mock
    private Context context;

    private UserController userController;
    private final String testUserId = "testUser123";
    private final User testUser = new User();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController(testUserId, context);
    }

    @Test
    public void addUser_success() {
        userController.addUser(testUser, new UserController.AddUserCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(String message) {
                fail("Add user should succeed");
            }
        });
    }

    @Test
    public void uploadProfileImage_success() {
        Uri testUri = mock(Uri.class);
        userController.uploadProfileImage(testUser, testUri, new UserController.ProfileImageUploadCallback() {
            @Override
            public void onProfileImageUploaded(String imageUrl) {
                assertNotNull(imageUrl);
            }

            @Override
            public void onError(String message) {
                fail("Upload profile image should succeed");
            }
        });
    }

    @Test
    public void setNotificationsEnabled_setsNotificationPreference() {
        testUser.setGeneralNotificationPref(true);
        userController.setGeneralNotificationsEnabled(testUser, false);
        assertFalse(testUser.isGeneralNotificationPref());
    }
}

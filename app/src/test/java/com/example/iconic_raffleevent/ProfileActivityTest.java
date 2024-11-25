package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class ProfileActivityTest {

    @Mock
    private UserController mockUserController;

    private User testUser;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Intents.init();

        // Create test user
        testUser = new User();
        testUser.setUserId("test-user-id");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPhoneNo("123-456-7890");
        testUser.setNotificationsEnabled(true);

        // Mock user controller behavior for void method
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(testUser);
            return null;
        }).when(mockUserController).getUserInformation(any());
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testInitialUIElements() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
        });

        // Verify all UI elements are displayed
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
        onView(withId(R.id.name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.phone_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.notifications_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.upload_photo_button)).check(matches(isDisplayed()));
        onView(withId(R.id.remove_photo_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadUserProfile() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
        });

        // Verify user data is displayed correctly
        onView(withId(R.id.name_edit_text)).check(matches(withText(testUser.getName())));
        onView(withId(R.id.email_edit_text)).check(matches(withText(testUser.getEmail())));
        onView(withId(R.id.phone_edit_text)).check(matches(withText(testUser.getPhoneNo())));
    }

    @Test
    public void testProfileUpdate() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
            activity.currentUser = testUser;
        });

        // Mock successful profile update
        doAnswer(invocation -> {
            UserController.UpdateProfileCallback callback = invocation.getArgument(3);
            callback.onProfileUpdated();
            return null;
        }).when(mockUserController).updateProfile(any(), anyString(), anyString(), anyString(), any());

        // Update profile fields
        String newName = "Updated Name";
        String newEmail = "updated@example.com";
        String newPhone = "987-654-3210";

        onView(withId(R.id.name_edit_text)).perform(replaceText(newName), closeSoftKeyboard());
        onView(withId(R.id.email_edit_text)).perform(replaceText(newEmail), closeSoftKeyboard());
        onView(withId(R.id.phone_edit_text)).perform(replaceText(newPhone), closeSoftKeyboard());

        // Click save button
        onView(withId(R.id.save_button)).perform(click());

        // Verify updated values
        onView(withId(R.id.name_edit_text)).check(matches(withText(newName)));
        onView(withId(R.id.email_edit_text)).check(matches(withText(newEmail)));
        onView(withId(R.id.phone_edit_text)).check(matches(withText(newPhone)));
    }

    @Test
    public void testImageUpload() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
            activity.currentUser = testUser;
        });

        // Mock image selection result
        Intent resultData = new Intent();
        resultData.setData(Uri.parse("content://test/image.jpg"));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(
                Activity.RESULT_OK, resultData);

        // Mock image upload success
        doAnswer(invocation -> {
            UserController.ProfileImageUploadCallback callback = invocation.getArgument(2);
            callback.onProfileImageUploaded("https://test.com/image.jpg");
            return null;
        }).when(mockUserController).uploadProfileImage(any(), any(), any());

        // Click upload button and verify intent
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK))
                .respondWith(result);
        onView(withId(R.id.upload_photo_button)).perform(click());
    }

    @Test
    public void testImageRemoval() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
            activity.currentUser = testUser;
            testUser.setProfileImageUrl("https://test.com/image.jpg");
        });

        // Mock successful image removal
        doAnswer(invocation -> {
            UserController.ProfileImageRemovalCallback callback = invocation.getArgument(1);
            callback.onProfileImageRemoved();
            return null;
        }).when(mockUserController).removeProfileImage(any(), any());

        // Click remove button
        onView(withId(R.id.remove_photo_button)).perform(click());

        // Verify default image is displayed
        scenario.onActivity(activity -> {
            ImageView profileImage = activity.findViewById(R.id.profile_image);
            profileImage.getDrawable().getConstantState().equals(
                    activity.getResources().getDrawable(R.drawable.default_profile).getConstantState()
            );
        });
    }

    @Test
    public void testNavigationDrawer() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
        });

        // Test menu button opens drawer
        onView(withId(R.id.menu_button)).perform(click());
        onView(withId(R.id.navigation_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testFooterNavigation() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
        });

        // Test home button navigation
        onView(withId(R.id.home_button)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(EventListActivity.class.getName()));

        // Test QR button navigation
        onView(withId(R.id.qr_button)).perform(click());
        Intents.intended(IntentMatchers.hasComponent(QRScannerActivity.class.getName()));
    }

    @Test
    public void testInvalidEmailValidation() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
            activity.currentUser = testUser;
        });

        // Enter invalid email
        onView(withId(R.id.email_edit_text)).perform(replaceText("invalid-email"), closeSoftKeyboard());

        // Try to save
        onView(withId(R.id.save_button)).perform(click());

        // Verify error state
        onView(withId(R.id.email_edit_text)).check(matches(withText("invalid-email")));
    }

    @Test
    public void testBackNavigationWithChanges() {
        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
        scenario.onActivity(activity -> {
            activity.userController = mockUserController;
            activity.currentUser = testUser;
        });

        // Make changes
        onView(withId(R.id.name_edit_text)).perform(replaceText("Changed Name"), closeSoftKeyboard());

        // Press back
        scenario.onActivity(Activity::onBackPressed);

        // Verify dialog is shown
        onView(withText("Discard changes?")).check(matches(isDisplayed()));
    }
}
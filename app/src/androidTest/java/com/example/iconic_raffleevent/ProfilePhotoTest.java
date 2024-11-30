package com.example.iconic_raffleevent;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import com.example.iconic_raffleevent.view.ProfileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ProfilePhotoTest {

    private UiDevice device;

    @Rule
    public ActivityScenarioRule<ProfileActivity> scenario = new ActivityScenarioRule<>(ProfileActivity.class);

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void testProfilePhotoGalleryAccess() {
        // Click on the upload photo button
        onView(withId(R.id.upload_photo_button)).perform(click());

        // Handle permission dialog if it appears
        UiObject2 allowButton = device.wait(Until.findObject(By.text("Allow")), 5000);
        if (allowButton != null) {
            allowButton.click();
        }

        // Select an image from the gallery
        Intent resultData = new Intent();
        Uri imageUri = Uri.parse("file:///sdcard/sample_image.jpg");
        resultData.setData(imageUri);
        ActivityScenario<ProfileActivity> activityScenario = scenario.getScenario();
        activityScenario.onActivity(activity -> activity.onActivityResult(ProfileActivity.PICK_IMAGE_REQUEST, -1, resultData));

        // Verify that the profile image is displayed
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
    }
}
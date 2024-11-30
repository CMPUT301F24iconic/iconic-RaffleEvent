package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationAndQRTests {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

    @Test
    public void testNotificationBackNavigation() {
        // Launch notification activity
        ActivityScenario.launch(NotificationsActivity.class);

        // Verify notification elements are displayed
        onView(withId(R.id.settings_icon)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_list)).check(matches(isDisplayed()));

        // Test back button click
        onView(withId(R.id.back_button)).perform(click());
    }

    @Test
    public void testQRScannerNavigation() throws UiObjectNotFoundException {
        // Launch QR scanner activity
        ActivityScenario.launch(QRScannerActivity.class);

        // Handle camera permission dialog
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject allowButton = device.findObject(new UiSelector().text("While using the app"));
        if(allowButton.exists()) {
            allowButton.click();
        }

        // Wait for camera to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify QR elements are displayed
        onView(withId(R.id.camera_preview)).check(matches(isDisplayed()));
        onView(withId(R.id.flashlight_button)).check(matches(isDisplayed()));
        onView(withId(R.id.gallery_button)).check(matches(isDisplayed()));
        onView(withId(R.id.cancel_button)).check(matches(isDisplayed()));

        // Test cancel button click
        onView(withId(R.id.cancel_button)).perform(click());
    }
}
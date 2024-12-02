package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.NotificationSettingsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationSettingsActivityTest {

    @Before
    public void setUp() {
        // Launch the NotificationSettingsActivity
        ActivityScenario.launch(NotificationSettingsActivity.class);
    }

    @Test
    public void testSwitchVisibility() {
        // Verify that the notification switches are displayed
        onView(withId(R.id.general_notification_switch)).check(matches(isDisplayed()));
    }

    @Test
    public void testSaveButtonVisibilityOnSwitchToggle() {
        // Toggle the general notification switch
        onView(withId(R.id.general_notification_switch)).perform(click());

        // Verify that the save button becomes visible
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testSaveButtonFunctionality() {
//        // Toggle the general notification switch and click the save button
//        onView(withId(R.id.general_notification_switch)).perform(click());
//        onView(withId(R.id.save_button)).perform(click());
//
//        // Verify that it navigates back to NotificationsActivity
//        onView(withId(R.id.notification_list)).check(matches(isDisplayed()));
//    }

    @Test
    public void testBackButtonFunctionality() {
        // Click the back button
        onView(withId(R.id.back_button)).perform(click());

        // Verify that it navigates back to NotificationsActivity
        onView(withId(R.id.notification_list)).check(matches(isDisplayed()));
    }
}

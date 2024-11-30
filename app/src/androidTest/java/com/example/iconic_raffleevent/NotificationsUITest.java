package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.EventDetailsActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NotificationSettingsActivity;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NotificationsUITest {

    @Rule
    public ActivityScenarioRule<NotificationsActivity> activityRule =
            new ActivityScenarioRule<>(NotificationsActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testNavigateToNotificationSettings() {
        onView(withId(R.id.settings_icon)).perform(click());
        intended(hasComponent(NotificationSettingsActivity.class.getName()));
    }



    @Test
    public void testNotificationSettingsSwitches() {
        onView(withId(R.id.settings_icon)).perform(click());
    }

    @Test
    public void testBackButton() {
        onView(withId(R.id.back_button)).perform(click());
        // Verify that the current activity finishes
        activityRule.getScenario().onActivity(activity -> {
            assert activity.isFinishing();
        });
    }
}
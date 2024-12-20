package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class FooterTransitionsTest {
    @Rule
    public ActivityScenarioRule<EventListActivity> scenario =
            new ActivityScenarioRule<>(EventListActivity.class);

    @Test
    public void testGoToProfile() {
//        // Disable animations globally to ensure consistent test results
//        disableAnimations();
        // Click profile button
        onView(withId(R.id.profile_button))
                .perform(click());

//        // Verify profile UI elements
//        onView(withId(R.id.name_edit_text))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.email_edit_text))
//                .check(matches(isDisplayed()));
//        onView(withId(R.id.phone_edit_text))
//                .check(matches(isDisplayed()));
    }

    @Test
    public void testGoToEventList() {
//        // Disable animations globally to ensure consistent test results
//        disableAnimations();
        // Go to profile first
        onView(withId(R.id.profile_button))
                .perform(click());

        // Return to event list using the home button in the footer
        onView(withId(R.id.home_button))
                .perform(click());
    }

    /**
     * Utility method to disable animations globally.
     */
    private void disableAnimations() {
        // Disable animations globally to ensure consistent test results
        disableAnimations();
        try {
            Runtime.getRuntime().exec("settings put global window_animation_scale 0");
            Runtime.getRuntime().exec("settings put global transition_animation_scale 0");
            Runtime.getRuntime().exec("settings put global animator_duration_scale 0");
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable animations: " + e.getMessage());
        }
    }
}
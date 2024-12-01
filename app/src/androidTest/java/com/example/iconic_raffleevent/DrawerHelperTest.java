package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.EventListActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawerHelperTest {

    @Before
    public void setUp() {
        // Disable animations globally to ensure consistent test results
        disableAnimations();

        // Launch EventListActivity since it has the drawer
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventListActivity.class);
        ActivityScenario.launch(intent);
    }

    /**
     * Test that verifies the drawer opens and navigation items are visible.
     */
    @Test
    public void testDrawerLayout() {
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());

        // Verify the drawer is displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));

        // Verify menu items are visible
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_scan_qr)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_create_event)).check(matches(isDisplayed()));
    }

    /**
     * Test that verifies navigation to ProfileActivity works from the drawer.
     */
    @Test
    public void testNavigateToProfileActivity() {
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());

        // Click on the Profile menu item
        onView(withId(R.id.nav_profile)).perform(click());
    }

    /**
     * Utility method to disable animations globally.
     */
    private void disableAnimations() {
        try {
            Runtime.getRuntime().exec("settings put global window_animation_scale 0");
            Runtime.getRuntime().exec("settings put global transition_animation_scale 0");
            Runtime.getRuntime().exec("settings put global animator_duration_scale 0");
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable animations: " + e.getMessage());
        }
    }
}

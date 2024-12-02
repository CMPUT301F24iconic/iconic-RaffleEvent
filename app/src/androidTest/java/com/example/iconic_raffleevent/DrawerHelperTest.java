package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.EventListActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DrawerHelperTest {

    @Before
    public void setUp() {
        // Launch an activity that uses the DrawerHelper
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.iconic_raffleevent", "com.example.iconic_raffleevent.view.EventListActivity");
        ActivityScenario.launch(intent);
    }

    @Test
    public void testDrawerNavigation() {
        // Open the drawer by clicking the menu button
        onView(withId(R.id.menu_button)).perform(click());

        // Verify that the navigation drawer is displayed
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));

        // Click on "Create Event" and verify navigation
        onView(withId(R.id.nav_create_event)).perform(click());

    }

    @Test
    public void testProfileNavigation() {
        // Open the drawer
        onView(withId(R.id.menu_button)).perform(click());

        // Click on the profile photo and verify navigation
        onView(withId(R.id.nav_profile_photo)).perform(click());
    }

    @Test
    public void testAdminPanelVisibilityForAdmin() {
        // Open the drawer
        onView(withId(R.id.menu_button)).perform(click());

        // Verify admin panel elements are displayed for admin users
        onView(withId(R.id.admin_panel_header)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_manage_users)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_manage_events)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_manage_qr_codes)).check(matches(isDisplayed()));
        onView(withId(R.id.admin_manage_facilities)).check(matches(isDisplayed()));
    }

    @Test
    public void testFacilityNavigation() {
        // Open the drawer
        onView(withId(R.id.menu_button)).perform(click());

        // Click on "Facility" and verify navigation
        onView(withId(R.id.nav_facility)).perform(click());
    }
}

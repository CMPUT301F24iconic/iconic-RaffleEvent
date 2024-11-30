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
        // Launch EventListActivity since it has the drawer
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventListActivity.class);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testDrawerLayout() {
        // Open drawer first
        onView(withId(R.id.menu_button)).perform(click());

        // Verify drawer and navigation items are visible
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
        onView(withId(R.id.navigation_view)).check(matches(isDisplayed()));

        // Verify menu items are visible
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_events)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_scan_qr)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_facility)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_create_event)).check(matches(isDisplayed()));
    }
}
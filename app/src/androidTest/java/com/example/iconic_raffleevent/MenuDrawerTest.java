package com.example.iconic_raffleevent;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.iconic_raffleevent.view.EventListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MenuDrawerTest {
    @Rule
    public ActivityScenarioRule<EventListActivity> scenario = new
            ActivityScenarioRule<EventListActivity>(EventListActivity.class);

    @Test
    public void testGoToProfile() {
        // Test
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());
        // Click on profile button
        onView(withId(R.id.nav_profile)).perform(click());

        // Check to see if profile UI loaded
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()));
        onView(withId(R.id.name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.phone_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.notifications_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.save_button)).check(matches(isDisplayed()));
        onView(withId(R.id.remove_photo_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_to_hub_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToNotifications() {
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());
        // Click on notifications button
        onView(withId(R.id.nav_notifications)).perform(click());

        // Check to see if notifications UI is loaded
        onView(withId(R.id.notification_list)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_settings)).check(matches(isDisplayed()));
    }

    @Test
    public void testGoToEvents() {
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());
        // Click on notifications button
        onView(withId(R.id.nav_events)).perform(click());

        // Check if we're back on home screen
        onView(withId(R.id.eventListView)).check(matches(isDisplayed()));
    }

    /*
    @Test
    public void testGoToQRScanner() {
        // Open drawer
        onView(withId(R.id.menu_button)).perform(click());
        // Click on notifications button
        onView(withId(R.id.nav_scan_qr)).perform(click());

        // Check to see if we're in scanner activity
        onView(withId(R.id.camera_preview)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_text)).check(matches(isDisplayed()));
    }

     */
}

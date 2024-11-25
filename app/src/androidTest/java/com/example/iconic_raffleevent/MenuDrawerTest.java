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
    public ActivityScenarioRule<EventListActivity> activityRule =
            new ActivityScenarioRule<>(EventListActivity.class);

    @Test
    public void testDrawerOpenClose() {
        // Open drawer using menu button
        onView(withId(R.id.menu_button))
                .perform(click());

        // Verify navigation drawer is displayed
        onView(withId(R.id.navigation_view))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testGoToProfile() {
        // Open drawer
        onView(withId(R.id.menu_button))
                .perform(click());

        // Wait for drawer animation
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click profile menu item
        onView(withId(R.id.nav_profile))
                .perform(click());

        // Verify profile UI elements are displayed
        onView(withId(R.id.profile_image))
                .check(matches(isDisplayed()));
        onView(withId(R.id.name_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.phone_edit_text))
                .check(matches(isDisplayed()));
        onView(withId(R.id.notifications_switch))
                .check(matches(isDisplayed()));
        onView(withId(R.id.save_button))
                .check(matches(isDisplayed()));
        onView(withId(R.id.remove_photo_button))
                .check(matches(isDisplayed()));
        onView(withId(R.id.back_to_hub_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testGoToNotifications() {
        // Open drawer
        onView(withId(R.id.menu_button))
                .perform(click());

        // Wait for drawer animation
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click notifications menu item
        onView(withId(R.id.nav_notifications))
                .perform(click());

        // Verify notifications UI elements are displayed
        onView(withId(R.id.notification_list))
                .check(matches(isDisplayed()));
        onView(withId(R.id.notification_icon))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testGoToEvents() {
        // Open drawer
        onView(withId(R.id.menu_button))
                .perform(click());

        // Wait for drawer animation
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click events menu item
        onView(withId(R.id.nav_events))
                .perform(click());

        // Verify events list is displayed
        onView(withId(R.id.eventListView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testGoToQRScanner() {
        // Open drawer
        onView(withId(R.id.menu_button))
                .perform(click());

        // Wait for drawer animation
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click QR scanner menu item
        onView(withId(R.id.nav_scan_qr))
                .perform(click());

        // Verify QR scanner UI elements are displayed
        onView(withId(R.id.camera_preview))
                .check(matches(isDisplayed()));
        onView(withId(R.id.qr_code_text))
                .check(matches(isDisplayed()));
    }
}
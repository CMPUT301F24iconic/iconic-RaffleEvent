package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.iconic_raffleevent.view.EventListActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class WaitingListTest {

    @Rule
    public ActivityScenarioRule<EventListActivity> activityRule =
            new ActivityScenarioRule<>(EventListActivity.class);

    @Before
    public void setup() {
        // Navigate to first event in list to start each test
        onView(withId(R.id.eventListView))
                .perform(click());
    }

    @Test
    public void testJoinWaitingList() {
        // Verify event details are displayed
        onView(withId(R.id.eventImage))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventTitle))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription))
                .check(matches(isDisplayed()));

        // Verify join button is displayed and enabled
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(isDisplayed()));

        // Click join waiting list button
        onView(withId(R.id.joinWaitingListButton))
                .perform(click());

        // Verify success message
        onView(withText("Successfully joined waiting list"))
                .check(matches(isDisplayed()));

        // Verify join button is now hidden and leave button is shown
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLeaveWaitingList() {
        // First join the waiting list
        onView(withId(R.id.joinWaitingListButton))
                .perform(click());

        // Verify we're on the waiting list
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(isDisplayed()));

        // Click leave waiting list button
        onView(withId(R.id.leaveWaitingListButton))
                .perform(click());

        // Verify confirmation dialog appears
        onView(withText("Leave Waiting List"))
                .check(matches(isDisplayed()));
        onView(withText("Are you sure you want to leave the waiting list?"))
                .check(matches(isDisplayed()));

        // Confirm leaving
        onView(withText("Yes"))
                .perform(click());

        // Verify success message
        onView(withText("Successfully left waiting list"))
                .check(matches(isDisplayed()));

        // Verify leave button is now hidden and join button is shown
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testCancelLeaveWaitingList() {
        // First join the waiting list
        onView(withId(R.id.joinWaitingListButton))
                .perform(click());

        // Click leave waiting list button
        onView(withId(R.id.leaveWaitingListButton))
                .perform(click());

        // When confirmation dialog appears, click No
        onView(withText("No"))
                .perform(click());

        // Verify we're still on the waiting list
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void testJoinWaitingListWithGeolocation() {
        // Click join waiting list button for an event with geolocation
        onView(withId(R.id.joinWaitingListButton))
                .perform(click());

        // Verify geolocation warning appears
        onView(withText("This event requires your location"))
                .check(matches(isDisplayed()));
        onView(withText("Allow location access to join waiting list?"))
                .check(matches(isDisplayed()));

        // Accept geolocation
        onView(withText("Allow"))
                .perform(click());

        // Verify success message
        onView(withText("Successfully joined waiting list"))
                .check(matches(isDisplayed()));

        // Verify join button is now hidden and leave button is shown
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(not(isDisplayed())));
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testJoinWaitingListDeclineGeolocation() {
        // Click join waiting list button
        onView(withId(R.id.joinWaitingListButton))
                .perform(click());

        // When geolocation warning appears, click Deny
        onView(withText("Deny"))
                .perform(click());

        // Verify we're not on the waiting list
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(not(isDisplayed())));

        // Verify warning message
        onView(withText("Location access required to join this event"))
                .check(matches(isDisplayed()));
    }
}
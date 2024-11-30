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
public class EventTransitionsTest {
    @Rule
    public ActivityScenarioRule<EventListActivity> scenario =
            new ActivityScenarioRule<>(EventListActivity.class);

    @Test
    public void testViewEventDetails() {
        // Click first event in list
        onView(withId(R.id.eventListView))
                .perform(click());

        // Verify event details are displayed
        onView(withId(R.id.eventImage))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventTitle))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventLocation))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventDate))
                .check(matches(isDisplayed()));
        onView(withId(R.id.map_button))
                .check(matches(isDisplayed()));
        onView(withId(R.id.joinWaitingListButton))
                .check(matches(isDisplayed()));
        onView(withId(R.id.leaveWaitingListButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testViewEventMap() {
        // Navigate to event details first
        onView(withId(R.id.eventListView))
                .perform(click());

        // Click map button
        onView(withId(R.id.map_button))
                .perform(click());

        // Verify map view is displayed
        onView(withId(R.id.event_header))
                .check(matches(isDisplayed()));
        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }
}
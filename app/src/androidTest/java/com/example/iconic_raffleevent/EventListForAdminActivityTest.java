package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.EventDetailsForAdminActivity;
import com.example.iconic_raffleevent.view.EventListForAdminActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventListForAdminActivityTest {

    @Test
    public void testEventListDisplay() {
        // Launch the EventListForAdminActivity
        ActivityScenario.launch(EventListForAdminActivity.class);

        // Verify that the event list view is displayed
        onView(withId(R.id.eventListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickEventAndDisplayDetails() {
        // Launch the EventListForAdminActivity
        ActivityScenario<EventListForAdminActivity> scenario = ActivityScenario.launch(EventListForAdminActivity.class);

        // Click the first event in the list
        onData(anything()).inAdapterView(withId(R.id.eventListView)).atPosition(0).perform(click());

        // Verify that the EventDetailsForAdminActivity is launched
        onView(withId(R.id.eventImage)).check(matches(isDisplayed()));
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
        onView(withId(R.id.eventLocation)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDate)).check(matches(isDisplayed()));
    }
}
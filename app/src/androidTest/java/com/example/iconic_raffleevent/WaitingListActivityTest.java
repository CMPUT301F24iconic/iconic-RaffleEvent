package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.view.WaitingListActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WaitingListActivityTest {

    private static final String TEST_EVENT_ID = "test_event_id";

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WaitingListActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testInitialUIElements() {
        // Verify basic UI elements are displayed
        onView(withId(R.id.userRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.sampleAttendeesButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationButtons() {
        // Test back button
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).perform(click());
    }

    @Test
    public void testSampleAttendeesDialog() {
        // Click sample attendees button
        onView(withId(R.id.sampleAttendeesButton)).perform(click());

        // Verify dialog elements
        onView(withId(R.id.attendee_count_input)).check(matches(isDisplayed()));
        onView(withId(R.id.sample_all_checkbox)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelButton)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmButton)).check(matches(isDisplayed()));
    }
}
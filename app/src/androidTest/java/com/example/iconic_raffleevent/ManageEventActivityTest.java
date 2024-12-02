package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.ManageEventActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ManageEventActivityTest {

    private static final String EVENT_ID = "test_event_id";

    @Before
    public void setUp() {
        // Launch the ManageEventActivity with a test event ID
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.example.iconic_raffleevent", ManageEventActivity.class.getName());
        intent.putExtra("eventId", EVENT_ID);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testUIElementsDisplayed() {
        // Verify that all tiles are displayed
        onView(withId(R.id.waitingListTile)).check(matches(isDisplayed()));
        onView(withId(R.id.attendeeListTile)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelledAttendeeListTile)).check(matches(isDisplayed()));
        onView(withId(R.id.finalAttendeeListTile)).check(matches(isDisplayed()));

        // Verify event details UI is displayed
        onView(withId(R.id.eventImage)).check(matches(isDisplayed()));
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.hosterTitle)).check(matches(isDisplayed()));
    }

    @Test
    public void testWaitingListTileClick() {
        // Click on the Waiting List tile
        onView(withId(R.id.waitingListTile)).perform(click());
    }

    @Test
    public void testAttendeeListTileClick() {
        // Click on the Attendee List tile
        onView(withId(R.id.attendeeListTile)).perform(click());
    }

    @Test
    public void testCancelledAttendeeListTileClick() {
        // Click on the Cancelled Attendee List tile
        onView(withId(R.id.cancelledAttendeeListTile)).perform(click());
    }

    @Test
    public void testFinalAttendeeListTileClick() {
        // Click on the Final Attendee List tile
        onView(withId(R.id.finalAttendeeListTile)).perform(click());
    }

    @Test
    public void testFooterNavigationButtons() {
        // Click the home button and verify navigation
        onView(withId(R.id.home_button)).perform(click());

        // Click the QR button and verify navigation
        onView(withId(R.id.qr_button)).perform(click());

        // Click the profile button and verify navigation
        onView(withId(R.id.profile_button)).perform(click());
    }

    @Test
    public void testBackButtonFunctionality() {
        // Click the back button and verify the activity finishes
        onView(withId(R.id.back_button)).perform(click());
    }
}

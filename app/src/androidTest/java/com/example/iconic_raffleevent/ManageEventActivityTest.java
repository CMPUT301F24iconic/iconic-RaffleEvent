package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.ConfirmedListActivity;
import com.example.iconic_raffleevent.view.DeclinedListActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.InvitedListActivity;
import com.example.iconic_raffleevent.view.ManageEventActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.example.iconic_raffleevent.view.WaitingListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ManageEventActivityTest {

    private static final String EVENT_ID = "sample_event_id";

    @Rule
    public ActivityScenarioRule<ManageEventActivity> activityRule =
            new ActivityScenarioRule<>(ManageEventActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        // Launch the activity with a sample event ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ManageEventActivity.class);
        intent.putExtra("eventId", EVENT_ID);
        activityRule.getScenario().onActivity(activity -> {
            activity.startActivity(intent);
        });
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testWaitingListButtonClick() {
        onView(withId(R.id.waitingListButton)).perform(click());
        intended(allOf(
                hasComponent(WaitingListActivity.class.getName()),
                hasExtra("eventId", EVENT_ID)
        ));
    }

    @Test
    public void testAttendeeListButtonClick() {
        onView(withId(R.id.attendeeListButton)).perform(click());
        intended(allOf(
                hasComponent(InvitedListActivity.class.getName()),
                hasExtra("eventId", EVENT_ID)
        ));
    }

    @Test
    public void testFinalAttendeeListButtonClick() {
        onView(withId(R.id.finalAttendeeListButton)).perform(click());
        intended(allOf(
                hasComponent(ConfirmedListActivity.class.getName()),
                hasExtra("eventId", EVENT_ID)
        ));
    }

    @Test
    public void testBackButtonClick() {
        onView(withId(R.id.back_button)).perform(click());
        // Verify that the current activity finishes
        activityRule.getScenario().onActivity(activity -> {
            assert activity.isFinishing();
        });
    }

    @Test
    public void testHomeButtonClick() {
        onView(withId(R.id.home_button)).perform(click());
        intended(hasComponent(EventListActivity.class.getName()));
    }

    @Test
    public void testQRButtonClick() {
        onView(withId(R.id.qr_button)).perform(click());
        intended(hasComponent(QRScannerActivity.class.getName()));
    }

    @Test
    public void testProfileButtonClick() {
        onView(withId(R.id.profile_button)).perform(click());
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testUIElementsVisibility() {
        onView(withId(R.id.waitingListButton)).check(matches(isDisplayed()));
        onView(withId(R.id.attendeeListButton)).check(matches(isDisplayed()));
        onView(withId(R.id.cancelledAttendeeListButton)).check(matches(isDisplayed()));
        onView(withId(R.id.finalAttendeeListButton)).check(matches(isDisplayed()));
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }
}
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

import com.example.iconic_raffleevent.view.MapActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    private static final String TEST_EVENT_ID = "test_event_id";
    private static final String TEST_EVENT_TITLE = "Test Event";

    @Before
    public void setUp() {
        // Launch MapActivity with test data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);
        intent.putExtra("eventTitle", TEST_EVENT_TITLE);
        intent.putExtra("geolocation", true); // Set geolocation to true for initial tests
        ActivityScenario.launch(intent);
    }

    @Test
    public void testUIElementsDisplayed() {
        // Verify that the header text is displayed correctly
        onView(withId(R.id.event_header)).check(matches(isDisplayed()));
        onView(withId(R.id.event_header)).check(matches(withText(TEST_EVENT_TITLE + "'s Waitlist Locations")));

        // Verify that the map is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testGeolocationDisabled() {
        // Relaunch activity with geolocation disabled
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);
        intent.putExtra("eventTitle", TEST_EVENT_TITLE);
        intent.putExtra("geolocation", false); // Disable geolocation
        ActivityScenario.launch(intent);

        // Verify placeholder text for geolocation disabled
        onView(withId(R.id.empty_message)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_message)).check(matches(withText("Geolocation for event is currently disabled. Enable geolocation to gain access")));
    }

//    @Test
//    public void testMapVisibilityWithEntrants() {
//        // Simulate map data being loaded
//        // Verify that the map is displayed
//        onView(withId(R.id.map)).check(matches(isDisplayed()));
//
//        // Placeholder text should not be visible
//        onView(withId(R.id.empty_message)).check(matches(withText("")));
//    }

    @Test
    public void testFooterNavigationButtons() {
        // Test navigation buttons in the footer
        onView(withId(R.id.home_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.qr_button)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.profile_button)).check(matches(isDisplayed())).perform(click());
    }

    @Test
    public void testBackButton() {
        // Verify that the back button finishes the activity
        onView(withId(R.id.back_button)).check(matches(isDisplayed())).perform(click());
    }
}

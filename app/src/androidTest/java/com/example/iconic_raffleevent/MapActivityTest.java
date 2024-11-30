package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MapActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);
        intent.putExtra("eventTitle", TEST_EVENT_TITLE);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testMapIsDisplayed() {
        // Verify that the map fragment is displayed
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementsAreDisplayed() {
        // Verify the header and navigation buttons are displayed
        onView(withId(R.id.event_header)).check(matches(isDisplayed()));
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }
}

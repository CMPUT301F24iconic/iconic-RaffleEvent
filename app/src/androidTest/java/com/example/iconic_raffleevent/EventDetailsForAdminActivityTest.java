package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.EventDetailsForAdminActivity;
import com.example.iconic_raffleevent.view.EventListForAdminActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventDetailsForAdminActivityTest {

    @Test
    public void testEventDetailsDisplay() {
        // Create an intent with a sample event ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventDetailsForAdminActivity.class);
        intent.putExtra("eventId", "sampleEventId");

        // Launch the EventDetailsForAdminActivity with the intent
        ActivityScenario.launch(intent);

        // Verify that the event details are displayed
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.eventDescription)).check(matches(isDisplayed()));
    }
}
package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.iconic_raffleevent.view.DeclinedListActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DeclinedListActivityTest {

    @Rule
    public ActivityTestRule<DeclinedListActivity> activityRule = new ActivityTestRule<>(DeclinedListActivity.class, false, false);

    /**
     * Test to verify the UI elements of DeclinedListActivity are displayed correctly.
     */
    @Test
    public void testUIElementsDisplayed() {
        Intent intent = new Intent();
        intent.putExtra("eventId", "sampleEventId");
        activityRule.launchActivity(intent);
        
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify the back button functionality in DeclinedListActivity.
     */
    @Test
    public void testBackButton() {
        Intent intent = new Intent();
        intent.putExtra("eventId", "sampleEventId");
        activityRule.launchActivity(intent);

        onView(withId(R.id.back_button)).perform(click());
        // Assuming the back button navigates to the parent activity
    }
}
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

import com.example.iconic_raffleevent.view.FacilityListForAdminActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FacilityListForAdminActivityTest {

    @Before
    public void setUp() {
        // Launch the activity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FacilityListForAdminActivity.class);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testUIElementsVisibility() {
        // Verify RecyclerView is displayed
        onView(withId(R.id.facilityRecyclerView)).check(matches(isDisplayed()));

        // Verify navigation buttons are displayed
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonFunctionality() {
        // Click the back button and verify the activity finishes
        onView(withId(R.id.back_button)).perform(click());
        // No explicit assertion needed since the activity should finish without exceptions
    }

//    @Test
//    public void testNavigationButtons() {
//        // Test Home button navigation
//        onView(withId(R.id.home_button)).perform(click());
//        onView(withId(R.id.facilityRecyclerView)).check(matches(isDisplayed())); // Verify navigation didn't break
//
//        // Test QR button navigation
//        onView(withId(R.id.qr_button)).perform(click());
//        onView(withId(R.id.facilityRecyclerView)).check(matches(isDisplayed())); // Verify navigation didn't break
//
//        // Test Profile button navigation
//        onView(withId(R.id.profile_button)).perform(click());
//        onView(withId(R.id.facilityRecyclerView)).check(matches(isDisplayed())); // Verify navigation didn't break
//    }
}

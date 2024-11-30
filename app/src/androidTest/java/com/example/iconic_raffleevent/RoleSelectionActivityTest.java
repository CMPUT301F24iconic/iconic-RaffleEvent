package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.RoleSelectionActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RoleSelectionActivityTest {

    /**
     * Test to verify that the UI elements (buttons) are displayed properly.
     */
    @Test
    public void testUIElementsDisplayed() {
        // Launch the RoleSelectionActivity
        ActivityScenario.launch(RoleSelectionActivity.class);

        // Check that the entrant/organizer button is displayed
        onView(withId(R.id.entrant_organizer_button)).check(matches(isDisplayed()));

        // Check that the admin button is displayed
        onView(withId(R.id.admin_button)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify navigation to the EventListActivity when the entrant/organizer button is clicked.
     */
    @Test
    public void testEntrantButtonNavigation() {
        // Launch the RoleSelectionActivity
        ActivityScenario.launch(RoleSelectionActivity.class);

        // Click the entrant/organizer button
        onView(withId(R.id.entrant_organizer_button)).perform(click());
    }

    /**
     * Test to verify navigation to the AdminHubActivity when the admin button is clicked.
     */
    @Test
    public void testAdminButtonNavigation() {
        // Launch the RoleSelectionActivity
        ActivityScenario.launch(RoleSelectionActivity.class);

        // Click the admin button
        onView(withId(R.id.admin_button)).perform(click());

    }
}

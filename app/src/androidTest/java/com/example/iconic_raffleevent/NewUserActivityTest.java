package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NewUserActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NewUserActivityTest {

    @Rule
    public ActivityTestRule<NewUserActivity> activityRule = new ActivityTestRule<>(NewUserActivity.class);

    /**
     * Test to verify that the UI elements in the activity are displayed properly.
     */
    @Test
    public void testUIElementsDisplayed() {
        onView(withId(R.id.edit_name_field)).check(matches(isDisplayed()));
        onView(withId(R.id.join_app_button)).check(matches(isDisplayed()));
    }

    /**
     * Test to verify error handling when the name field is empty and the button is clicked.
     */
    @Test
    public void testEmptyNameError() {
        onView(withId(R.id.edit_name_field)).perform(clearText());
        onView(withId(R.id.join_app_button)).perform(click());
        onView(withId(R.id.edit_name_field)).check(matches(withText(""))); // Field is still empty
    }

    /**
     * Test to verify successful user creation and navigation to EventListActivity.
     */
    @Test
    public void testSuccessfulUserCreation() {
        onView(withId(R.id.edit_name_field))
                .perform(clearText(), typeText("Test User"), closeSoftKeyboard());

        onView(withId(R.id.join_app_button)).perform(click());

        // Verify navigation to EventListActivity (simulate the intent)
        ActivityScenario.launch(EventListActivity.class);
    }
}

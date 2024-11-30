package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.CreateEventActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {

    @Test
    public void testCreateEventWithValidInputs() {
        // Launch CreateEventActivity with a sample facility ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("facilityId", "sampleFacilityId");
        ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(intent);

        // Fill in the event details
        onView(withId(R.id.eventTitleEditText)).perform(typeText("Sample Event"), closeSoftKeyboard());
        onView(withId(R.id.startDateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.startTimeEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endDateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endTimeEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.eventDescriptionEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());

        // Set a sample image URI
        Uri sampleImageUri = Uri.parse("file:///sample/image/path");
        scenario.onActivity(activity -> activity.setImageUri(sampleImageUri));

        // Click the save button
        onView(withId(R.id.saveButton)).perform(click());

        // Verify that the event details were saved
        // You can add assertions here based on the expected behavior after saving the event
        // For example, you can check if the user is navigated to the EventDetailsActivity
        // or if a success message is displayed
    }

    @Test
    public void testCreateEventWithInvalidInputs() {
        // Launch CreateEventActivity with a sample facility ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("facilityId", "sampleFacilityId");
        ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(intent);

        // Click the save button without filling in any details
        onView(withId(R.id.saveButton)).perform(click());

        // Verify that input validation is triggered
        scenario.onActivity(activity -> activity.invokeValidateInputFields());

        // Verify that input errors are displayed
        assertThat(scenario.getResult().getResultData(), is(notNullValue()));
        scenario.onActivity(activity -> {
            assertThat(activity.isInputError(), is(true));
            assertThat(activity.getEventTitleError(), is("Title cannot be empty"));
            assertThat(activity.getEventDescriptionError(), is("Event description cannot be empty"));
        });
    }

    @Test
    public void testCreateEventWithMissingPoster() {
        // Launch CreateEventActivity with a sample facility ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateEventActivity.class);
        intent.putExtra("facilityId", "sampleFacilityId");
        ActivityScenario<CreateEventActivity> scenario = ActivityScenario.launch(intent);

        // Fill in the event details
        onView(withId(R.id.eventTitleEditText)).perform(typeText("Sample Event"), closeSoftKeyboard());
        onView(withId(R.id.startDateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.startTimeEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endDateEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.endTimeEditText)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.eventDescriptionEditText)).perform(typeText("Sample Description"), closeSoftKeyboard());

        // Click the save button without uploading a poster
        onView(withId(R.id.saveButton)).perform(click());

        // Verify that the user remains on the CreateEventActivity
        onView(withId(R.id.createEventTitle)).check(matches(isDisplayed()));
    }
}
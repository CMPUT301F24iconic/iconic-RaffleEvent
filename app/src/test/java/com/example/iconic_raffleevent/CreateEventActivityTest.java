package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import android.content.Intent;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.view.CreateEventActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class CreateEventActivityTest {

    @Mock
    private EventController mockEventController;

    private ActivityScenario<CreateEventActivity> scenario;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Intents.init();

        Intent intent = new Intent();
        intent.putExtra("facilityId", "mockFacilityId");
        scenario = ActivityScenario.launch(CreateEventActivity.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testValidateInputFields_withEmptyFields_shouldShowErrors() {
        scenario.onActivity(activity -> {
            // Use setters or public methods to modify private fields
            activity.setEventTitle(""); // Empty title
            activity.setStartDate("2024/11/25");
            activity.setStartTime("10:00 AM");
            activity.setEndDate("2024/11/25");
            activity.setEndTime("12:00 PM");
            activity.setEventDescription(""); // Empty description

            activity.invokeValidateInputFields(); // Access the method via a public or package-private wrapper

            // Use public getters for assertions
            assertTrue(activity.isInputError());
            assertNotNull(activity.getEventTitleError());
            assertNotNull(activity.getEventDescriptionError());
        });
    }

    @Test
    public void testIsEndTimeLaterThanStartTime_validDates_shouldReturnTrue() {
        scenario.onActivity(activity -> {
            activity.setStartDate("2024/11/25");
            activity.setStartTime("10:00 AM");
            activity.setEndDate("2024/11/25");
            activity.setEndTime("12:00 PM");

            boolean result = activity.isEndTimeLaterThanStartTime();
            assertTrue(result);
        });
    }

    @Test
    public void testIsEndTimeLaterThanStartTime_invalidDates_shouldReturnFalse() {
        scenario.onActivity(activity -> {
            activity.setStartDate("2024/11/25");
            activity.setStartTime("12:00 PM");
            activity.setEndDate("2024/11/25");
            activity.setEndTime("10:00 AM");

            boolean result = activity.isEndTimeLaterThanStartTime();
            assertFalse(result);
        });
    }

    @Test
    public void testSaveEvent_withValidInputs_shouldCallControllerSave() {
        scenario.onActivity(activity -> {
            activity.setEventController(mockEventController);

            activity.setEventTitle("Test Event");
            activity.setStartDate("2024/11/25");
            activity.setStartTime("10:00 AM");
            activity.setEndDate("2024/11/25");
            activity.setEndTime("12:00 PM");
            activity.setEventDescription("This is a test event.");
            activity.setImageUri(mock(Uri.class));

            activity.invokeValidateAndSaveEvent(); // Access method via a wrapper

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            verify(mockEventController).saveEventToDatabase(eventCaptor.capture(), any());

            Event savedEvent = eventCaptor.getValue();
            assertEquals("Test Event", savedEvent.getEventTitle());
            assertEquals("2024/11/25", savedEvent.getEventStartDate());
            assertEquals("10:00 AM", savedEvent.getEventStartTime());
        });
    }

    @Test
    public void testSaveEvent_withInvalidInputs_shouldNotCallControllerSave() {
        scenario.onActivity(activity -> {
            activity.setEventController(mockEventController);

            activity.setEventTitle(""); // Invalid title
            activity.setStartDate("2024/11/25");
            activity.setStartTime("10:00 AM");
            activity.setEndDate("2024/11/25");
            activity.setEndTime("12:00 PM");
            activity.setEventDescription("This is a test event.");
            activity.setImageUri(mock(Uri.class));

            activity.invokeValidateAndSaveEvent(); // Access method via a wrapper

            verify(mockEventController, never()).saveEventToDatabase(any(), any());
        });
    }

    @Test
    public void testNavigationBar_shouldNavigateToCorrectActivity() {
        scenario.onActivity(activity -> {
            onView(withId(R.id.home_button)).perform(click());

            // Check that the EventListActivity is launched
            onView(withId(R.id.event_list_view)).check(matches(isDisplayed()));
        });
    }
}

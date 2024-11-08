package com.example.iconic_raffleevent;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.view.AdminEventActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;

@RunWith(AndroidJUnit4.class)
public class AdminEventActivityTest {

    private FirebaseOrganizer firebaseOrganizerMock;

    @Before
    public void setup() {
        // Mock the FirebaseOrganizer class
        firebaseOrganizerMock = Mockito.mock(FirebaseOrganizer.class);
    }

    /**
     * Test that events load successfully and are displayed in the ListView.
     */
    @Test
    public void testLoadEventList_Success() {
        ArrayList<Event> mockEvents = new ArrayList<>();
        Event event = new Event();
        event.setEventTitle("Test Event");
        mockEvents.add(event);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetEventsCallback callback = invocation.getArgument(0);
            callback.onEventsFetched(mockEvents); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllEvents(any(FirebaseOrganizer.GetEventsCallback.class));

        ActivityScenario.launch(AdminEventActivity.class);

        onView(withId(R.id.event_list_view)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when loading events fails.
     */
    @Test
    public void testLoadEventList_Failure() {
        doAnswer(invocation -> {
            FirebaseOrganizer.GetEventsCallback callback = invocation.getArgument(0);
            callback.onError("Error loading events"); // Simulate a fetch error
            return null;
        }).when(firebaseOrganizerMock).getAllEvents(any(FirebaseOrganizer.GetEventsCallback.class));

        ActivityScenario.launch(AdminEventActivity.class);

        // Check that the toast message is displayed for the error
        onView(withText("Error loading events: Error loading events"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that the delete event confirmation dialog appears and that event deletion is handled.
     */
    @Test
    public void testDeleteEvent_Success() {
        ArrayList<Event> mockEvents = new ArrayList<>();
        Event event = new Event();
        event.setEventId("testEventId");
        event.setEventTitle("Test Event");
        mockEvents.add(event);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetEventsCallback callback = invocation.getArgument(0);
            callback.onEventsFetched(mockEvents); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllEvents(any(FirebaseOrganizer.GetEventsCallback.class));

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteEventCallback callback = invocation.getArgument(1);
            callback.onSuccess(); // Simulate a successful deletion
            return null;
        }).when(firebaseOrganizerMock).deleteEvent(eq("testEventId"), any(FirebaseOrganizer.DeleteEventCallback.class));

        ActivityScenario.launch(AdminEventActivity.class);

        // Simulate clicking on the event to trigger the delete dialog
        onView(withId(R.id.event_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check that a toast message for successful deletion is displayed
        onView(withText("Event deleted successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that the delete event operation handles failure and shows an error message.
     */
    @Test
    public void testDeleteEvent_Failure() {
        ArrayList<Event> mockEvents = new ArrayList<>();
        Event event = new Event();
        event.setEventId("testEventId");
        event.setEventTitle("Test Event");
        mockEvents.add(event);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetEventsCallback callback = invocation.getArgument(0);
            callback.onEventsFetched(mockEvents); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllEvents(any(FirebaseOrganizer.GetEventsCallback.class));

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteEventCallback callback = invocation.getArgument(1);
            callback.onError("Delete failed"); // Simulate a delete error
            return null;
        }).when(firebaseOrganizerMock).deleteEvent(eq("testEventId"), any(FirebaseOrganizer.DeleteEventCallback.class));

        ActivityScenario.launch(AdminEventActivity.class);

        // Simulate clicking on the event to trigger the delete dialog
        onView(withId(R.id.event_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check that a toast message for failed deletion is displayed
        onView(withText("Error deleting event: Delete failed"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}
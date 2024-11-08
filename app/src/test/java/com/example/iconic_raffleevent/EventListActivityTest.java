package com.example.iconic_raffleevent;

import android.content.Intent;
import android.provider.Settings;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.EventDetailsActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.example.iconic_raffleevent.view.UserControllerViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Mock
    private UserControllerViewModel userControllerViewModelMock;
    @Mock
    private UserController userControllerMock;
    @Mock
    private EventController eventControllerMock;

    private User mockUser;
    private ArrayList<Event> mockEventList;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Intents.init();

        // Mock user and events for testing
        mockUser = new User();
        mockUser.setUserId("testUserId");
        mockEventList = new ArrayList<>();
        Event mockEvent = new Event();
        mockEvent.setEventId("event123");
        mockEvent.setEventTitle("Test Event");
        mockEventList.add(mockEvent);

        when(userControllerViewModelMock.getUserController()).thenReturn(userControllerMock);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that EventListActivity loads user profile successfully.
     */
    @Test
    public void testLoadUserProfile_Success() {
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(mockUser);  // Mock a successful fetch
            return null;
        }).when(userControllerMock).getUserInformation(any(UserController.UserFetchCallback.class));

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            scenario.onActivity(activity -> {
                // Check if user profile loaded
                assertTrue(activity.userObj.getUserId().equals("testUserId"));
            });
        }
    }

    /**
     * Test that EventListActivity fetches and displays events correctly.
     */
    @Test
    public void testFetchEvents_Success() {
        doAnswer(invocation -> {
            EventController.EventListCallback callback = invocation.getArgument(1);
            callback.onEventsFetched(mockEventList);  // Mock events fetch
            return null;
        }).when(eventControllerMock).getAllUserEvents(any(String.class), any(EventController.EventListCallback.class));

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            scenario.onActivity(activity -> {
                // Verify that the event list is updated
                assertTrue(activity.eventList.size() > 0);
            });
        }
    }

    /**
     * Test that clicking an event navigates to EventDetailsActivity.
     */
    @Test
    public void testEventItemClick_NavigatesToEventDetails() {
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            scenario.onActivity(activity -> {
                activity.eventList.addAll(mockEventList);  // Add mock events to the list
                activity.eventAdapter.notifyDataSetChanged();
            });

            // Simulate item click on the first event in the list
            onView(withId(R.id.eventListView)).perform(androidx.test.espresso.action.ViewActions.click());

            // Verify that it navigates to EventDetailsActivity
            intended(hasComponent(EventDetailsActivity.class.getName()));
        }
    }

    /**
     * Test that clicking the QR button navigates to QRScannerActivity.
     */
    @Test
    public void testQRButtonClick_NavigatesToQRScannerActivity() {
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            scenario.onActivity(activity -> {
                // Simulate QR button click
                activity.qrButton.performClick();
            });

            // Verify navigation to QRScannerActivity
            intended(hasComponent(QRScannerActivity.class.getName()));
        }
    }

    /**
     * Test that clicking the Profile button navigates to ProfileActivity.
     */
    @Test
    public void testProfileButtonClick_NavigatesToProfileActivity() {
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            scenario.onActivity(activity -> {
                // Simulate profile button click
                activity.profileButton.performClick();
            });

            // Verify navigation to ProfileActivity
            intended(hasComponent(ProfileActivity.class.getName()));
        }
    }

    /**
     * Test that a Toast is shown if fetching events fails.
     */
    @Test
    public void testFetchEvents_FailureShowsToast() {
        doAnswer(invocation -> {
            EventController.EventListCallback callback = invocation.getArgument(1);
            callback.onError("Error fetching events");  // Mock failure
            return null;
        }).when(eventControllerMock).getAllUserEvents(any(String.class), any(EventController.EventListCallback.class));

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(EventListActivity.class)) {
            onView(withId(R.id.eventListView)).check(matches(isDisplayed()));  // Confirm activity loads
            // Verify Toast message on failure
            onView(withText("Error fetching events: Error fetching events")).inRoot(new ToastMatcher()).check(matches(isDisplayed()));
        }
    }
}

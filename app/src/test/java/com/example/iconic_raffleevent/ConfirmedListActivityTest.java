package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.ConfirmedListActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ConfirmedListActivityTest {

    private FirebaseAttendee mockFirebaseAttendee;
    private Event testEvent;
    private List<User> testUsers;
    private static final String TEST_EVENT_ID = "test-event-id";

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Intents.init();

        // Create mock FirebaseAttendee
        mockFirebaseAttendee = mock(FirebaseAttendee.class);

        // Create test event
        testEvent = new Event();
        testEvent.setEventId(TEST_EVENT_ID);
        testEvent.setEventTitle("Test Event");
        List<String> registeredAttendees = Arrays.asList("user1", "user2", "user3");
        testEvent.setRegisteredAttendees(registeredAttendees);

        // Create test users
        testUsers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setUserId("user" + (i + 1));
            user.setName("Test User " + (i + 1));
            user.setEmail("user" + (i + 1) + "@test.com");
            testUsers.add(user);
        }

        // Mock event details fetch
        doAnswer(invocation -> {
            Event testEvent = invocation.getArgument(0);
            ((EventController.EventDetailsCallback) invocation.getArgument(1))
                    .onEventDetailsFetched(testEvent);
            return null;
        }).when(mockFirebaseAttendee).getEventDetails(eq(TEST_EVENT_ID), any());

        // Mock user fetch
        for (User user : testUsers) {
            doAnswer(invocation -> {
                UserController.UserFetchCallback callback = invocation.getArgument(1);
                callback.onUserFetched(user);
                return null;
            }).when(mockFirebaseAttendee).getUser(eq(user.getUserId()), any());
        }
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private Intent createTestIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ConfirmedListActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);
        return intent;
    }

    @Test
    public void testActivityCreation() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
        //    activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Verify UI elements are displayed
        onView(withId(R.id.userRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.home_button)).check(matches(isDisplayed()));
        onView(withId(R.id.qr_button)).check(matches(isDisplayed()));
        onView(withId(R.id.profile_button)).check(matches(isDisplayed()));
        onView(withId(R.id.notification_icon)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoadEventDetails() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
        //    activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Verify event details were fetched
        verify(mockFirebaseAttendee).getEventDetails(eq(TEST_EVENT_ID), any());
    }

    @Test
    public void testLoadConfirmedList() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
         //   activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Verify each user was fetched
        for (User user : testUsers) {
            verify(mockFirebaseAttendee).getUser(eq(user.getUserId()), any());
        }
    }

    @Test
    public void testNavigationToHome() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
         //   activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Click home button
        onView(withId(R.id.home_button)).perform(click());

        // Verify navigation
        intended(hasComponent(EventListActivity.class.getName()));
    }

    @Test
    public void testNavigationToQRScanner() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
       // scenario.onActivity(activity -> {
         //   activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Click QR button
        onView(withId(R.id.qr_button)).perform(click());

        // Verify navigation
        intended(hasComponent(QRScannerActivity.class.getName()));
    }

    @Test
    public void testNavigationToProfile() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
         //   activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Click profile button
        onView(withId(R.id.profile_button)).perform(click());

        // Verify navigation
        intended(hasComponent(ProfileActivity.class.getName()));
    }

    @Test
    public void testNavigationToNotifications() {
        ActivityScenario<ConfirmedListActivity> scenario = ActivityScenario.launch(createTestIntent());
        //scenario.onActivity(activity -> {
         //   activity.setFirebaseAttendee(mockFirebaseAttendee);
        //});

        // Click notifications button
        onView(withId(R.id.notification_icon)).perform(click());

        // Verify navigation
        intended(hasComponent(NotificationsActivity.class.getName()));
    }

}

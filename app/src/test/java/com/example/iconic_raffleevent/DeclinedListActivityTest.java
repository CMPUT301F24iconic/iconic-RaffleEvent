package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.DeclinedListActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.example.iconic_raffleevent.view.UserAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class DeclinedListActivityTest {

    private DeclinedListActivity activity;
    private ActivityController<DeclinedListActivity> activityController;

    @Mock
    private FirebaseAttendee mockFirebaseAttendee;
    @Mock
    private UserAdapter mockUserAdapter;
    @Mock
    private RecyclerView mockRecyclerView;

    private static final String TEST_EVENT_ID = "test_event_id";
    private static final String TEST_USER_ID = "test_user_id";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create Intent with required event ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DeclinedListActivity.class);
        intent.putExtra("eventId", TEST_EVENT_ID);

        // Initialize activity
        activityController = Robolectric.buildActivity(DeclinedListActivity.class, intent);
        activity = activityController.create().start().resume().get();

        // Set up mock FirebaseAttendee
        activity.firebaseAttendee = mockFirebaseAttendee;
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
        assertNotNull(activity.firebaseAttendee);
    }

    @Test
    public void testLoadDeclinedList_Success() {
        // Create mock event with declined list
        Event mockEvent = new Event();
        List<String> declinedList = Arrays.asList(TEST_USER_ID);
        mockEvent.setDeclinedList(new ArrayList<>(declinedList));

        // Mock successful event details fetch
        Mockito.doAnswer(invocation -> {
            EventController.EventDetailsCallback callback = invocation.getArgument(1);
            callback.onEventDetailsFetched(mockEvent);
            return null;
        }).when(mockFirebaseAttendee).getEventDetails(ArgumentMatchers.anyString(), ArgumentMatchers.any(EventController.EventDetailsCallback.class));

        // Mock successful user fetch
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        Mockito.doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(1);
            callback.onUserFetched(mockUser);
            return null;
        }).when(mockFirebaseAttendee).getUser(ArgumentMatchers.anyString(), ArgumentMatchers.any(UserController.UserFetchCallback.class));

        // Trigger loading declined list
        activity.loadDeclinedList();

        // Verify user was added to adapter
        Mockito.verify(mockUserAdapter).addUser(mockUser);
        Mockito.verify(mockUserAdapter).notifyDataSetChanged();
    }

    @Test
    public void testLoadDeclinedList_EventFetchError() {
        // Mock event details fetch error
        Mockito.doAnswer(invocation -> {
            EventController.EventDetailsCallback callback = invocation.getArgument(1);
            callback.onError("Test error");
            return null;
        }).when(mockFirebaseAttendee).getEventDetails(ArgumentMatchers.anyString(), ArgumentMatchers.any(EventController.EventDetailsCallback.class));

        // Trigger loading declined list
        activity.loadDeclinedList();

        // Verify error toast
        assertEquals("Error: Test error", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testFetchUsersFromDeclinedList_Success() {
        List<String> userIds = Arrays.asList(TEST_USER_ID);
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);

        // Mock successful user fetch
        Mockito.doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(1);
            callback.onUserFetched(mockUser);
            return null;
        }).when(mockFirebaseAttendee).getUser(ArgumentMatchers.anyString(), ArgumentMatchers.any(UserController.UserFetchCallback.class));

        // Call method
        activity.fetchUsersFromDeclinedList(userIds);

        // Verify user was added to adapter
        Mockito.verify(mockUserAdapter).addUser(mockUser);
        Mockito.verify(mockUserAdapter).notifyDataSetChanged();
    }

    @Test
    public void testFetchUsersFromDeclinedList_Error() {
        List<String> userIds = Arrays.asList(TEST_USER_ID);

        // Mock user fetch error
        Mockito.doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(1);
            callback.onError("Test error");
            return null;
        }).when(mockFirebaseAttendee).getUser(ArgumentMatchers.anyString(), ArgumentMatchers.any(UserController.UserFetchCallback.class));

        // Call method
        activity.fetchUsersFromDeclinedList(userIds);

        // Verify error toast
        assertEquals("Error: Test error", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testNavigationButtons() {
        // Test home button
        activity.findViewById(R.id.home_button).performClick();
        Intent expectedHomeIntent = new Intent(activity, EventListActivity.class);
        assertEquals(expectedHomeIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test QR button
        activity.findViewById(R.id.qr_button).performClick();
        Intent expectedQrIntent = new Intent(activity, QRScannerActivity.class);
        assertEquals(expectedQrIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test profile button
        activity.findViewById(R.id.profile_button).performClick();
        Intent expectedProfileIntent = new Intent(activity, ProfileActivity.class);
        assertEquals(expectedProfileIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test notification button
        activity.findViewById(R.id.notification_icon).performClick();
        Intent expectedNotificationIntent = new Intent(activity, NotificationsActivity.class);
        assertEquals(expectedNotificationIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());
    }
}
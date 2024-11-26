package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings.Secure;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.DisplayQRCodeActivity;
import com.example.iconic_raffleevent.view.UserControllerViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

@RunWith(RobolectricTestRunner.class)
@Config(shadows = {ShadowContentResolver.class})
public class DisplayQRCodeActivityTest {

    private DisplayQRCodeActivity activity;
    private ActivityController<DisplayQRCodeActivity> activityController;

    @Mock
    private EventController mockEventController;
    @Mock
    private UserController mockUserController;
    @Mock
    private UserControllerViewModel mockUserControllerViewModel;
    @Mock
    private ViewModelProvider mockViewModelProvider;
    @Mock
    private EditText mockEventIdText;
    @Mock
    private ImageView mockQrCodeView;

    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_EVENT_ID = "test_event_id";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize activity
        activityController = Robolectric.buildActivity(DisplayQRCodeActivity.class);
        activity = activityController.create().start().resume().get();

        // Set up mock controllers
        activity.eventController = mockEventController;
        activity.userController = mockUserController;

        // Mock ViewModelProvider and UserControllerViewModel
        when(mockViewModelProvider.get(UserControllerViewModel.class))
                .thenReturn(mockUserControllerViewModel);
        when(mockUserControllerViewModel.getUserController())
                .thenReturn(mockUserController);
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
        assertNotNull(activity.eventController);
        assertNotNull(activity.userController);
    }

    @Test
    public void testCreateEventButton_Click() {
        // Setup test event ID
        EditText eventIdText = activity.findViewById(R.id.custom_event_id_text);
        eventIdText.setText(TEST_EVENT_ID);

        // Mock user object
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        activity.userObj = mockUser;

        // Click create event button
        Button createEventButton = activity.findViewById(R.id.create_qrcode_button);
        createEventButton.performClick();

        // Verify event creation
        verify(mockEventController).saveEventToDatabase(any(Event.class), any(User.class));
    }

    @Test
    public void testShowQRCode_Click() {
        // Setup mock event
        Event mockEvent = new Event();
        activity.event = mockEvent;

        // Click show QR code button
        Button showQRCodeButton = activity.findViewById(R.id.show_qrcode_button);
        showQRCodeButton.performClick();

        // Currently no verification as the functionality is commented out in the activity
    }

    @Test
    public void testLoadUserProfile_Success() {
        // Create mock user
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);

        // Mock successful user fetch
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(mockUser);
            return null;
        }).when(mockUserController).getUserInformation(any(UserController.UserFetchCallback.class));

        // Call load user profile
        activity.loadUserProfile();

        // Verify user was set
        assertEquals(mockUser, activity.userObj);
    }

    @Test
    public void testLoadUserProfile_Error() {
        // Mock user fetch error
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onError("Test error");
            return null;
        }).when(mockUserController).getUserInformation(any(UserController.UserFetchCallback.class));

        // Call load user profile
        activity.loadUserProfile();

        // Verify user object is null
        assertNull(activity.userObj);
    }

    @Test
    public void testGetUserID() {
        String userId = activity.getUserID();
        assertEquals(TEST_USER_ID, userId);
    }

    @Test
    public void testGetUserController() {
        // Mock ViewModelProvider
        when(mockViewModelProvider.get(UserControllerViewModel.class))
                .thenReturn(mockUserControllerViewModel);

        activity.getUserController();

        // Verify UserController was set
        assertNotNull(activity.userController);
    }

    @Test
    public void testEventCreation_ValidatesFields() {
        // Setup test event ID
        EditText eventIdText = activity.findViewById(R.id.custom_event_id_text);
        eventIdText.setText(TEST_EVENT_ID);

        // Mock user object
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);
        activity.userObj = mockUser;

        // Click create event button
        Button createEventButton = activity.findViewById(R.id.create_qrcode_button);
        createEventButton.performClick();

        // Verify event was created with correct fields
        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(mockEventController).saveEventToDatabase(eventCaptor.capture(), any(User.class));
        Event capturedEvent = eventCaptor.getValue();

        assertEquals(TEST_EVENT_ID, capturedEvent.getEventId());
        assertEquals("Custom", capturedEvent.getEventTitle());
        assertEquals("Custom event", capturedEvent.getEventDescription());
        assertEquals("Edmonton", capturedEvent.getEventLocation());
        assertEquals("10:00 PM", capturedEvent.getEventEndTime());
        assertEquals("5:00 PM", capturedEvent.getEventStartTime());
        assertEquals("2024-06-02", capturedEvent.getEventStartDate());
        assertEquals("2024-06-02", capturedEvent.getEventEndDate());
    }
}
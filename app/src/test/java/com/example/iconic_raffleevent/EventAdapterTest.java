package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.view.EventAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class EventAdapterTest {

    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private static final String CURRENT_USER_ID = "test_user_id";
    private static final String OTHER_USER_ID = "other_user_id";

    @Mock
    private Context mockContext;
    @Mock
    private ViewGroup mockParent;
    @Mock
    private View mockConvertView;
    @Mock
    private ImageView mockEventImageView;
    @Mock
    private TextView mockEventTitleTextView;
    @Mock
    private TextView mockEventDateTextView;
    @Mock
    private ImageView mockManageEventIcon;
    @Mock
    private LayoutInflater mockLayoutInflater;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize test data
        eventList = new ArrayList<>();
        mockContext = RuntimeEnvironment.getApplication();

        // Create adapter
        eventAdapter = new EventAdapter(mockContext, eventList, CURRENT_USER_ID);

        // Mock view finding
        when(mockConvertView.findViewById(R.id.event_image)).thenReturn(mockEventImageView);
        when(mockConvertView.findViewById(R.id.event_title)).thenReturn(mockEventTitleTextView);
        when(mockConvertView.findViewById(R.id.event_date)).thenReturn(mockEventDateTextView);
        when(mockConvertView.findViewById(R.id.manage_event_icon)).thenReturn(mockManageEventIcon);
    }

    @Test
    public void testAdapterCreation() {
        assertNotNull(eventAdapter);
    }

    @Test
    public void testGetView_WithNewConvertView() {
        // Create test event
        Event testEvent = createTestEvent(CURRENT_USER_ID);
        eventList.add(testEvent);

        // Mock layout inflation
        when(mockLayoutInflater.inflate(anyInt(), any(ViewGroup.class), any(Boolean.class)))
                .thenReturn(mockConvertView);

        // Get view
        View resultView = eventAdapter.getView(0, null, mockParent);

        // Verify view setup
        verify(mockEventTitleTextView).setText(testEvent.getEventTitle());
        verify(mockEventDateTextView).setText(testEvent.getEventStartDate());
        verify(mockManageEventIcon).setVisibility(View.VISIBLE);
    }

    @Test
    public void testGetView_ReuseConvertView() {
        // Create test event
        Event testEvent = createTestEvent(CURRENT_USER_ID);
        eventList.add(testEvent);

        // Get view with existing convertView
        View resultView = eventAdapter.getView(0, mockConvertView, mockParent);

        // Verify view setup
        verify(mockEventTitleTextView).setText(testEvent.getEventTitle());
        verify(mockEventDateTextView).setText(testEvent.getEventStartDate());
        verify(mockManageEventIcon).setVisibility(View.VISIBLE);
    }

    @Test
    public void testGetView_ManageIconVisibility_CurrentUser() {
        // Create test event with current user as organizer
        Event testEvent = createTestEvent(CURRENT_USER_ID);
        eventList.add(testEvent);

        // Get view
        eventAdapter.getView(0, mockConvertView, mockParent);

        // Verify manage icon is visible
        verify(mockManageEventIcon).setVisibility(View.VISIBLE);
    }

    @Test
    public void testGetView_ManageIconVisibility_OtherUser() {
        // Create test event with different user as organizer
        Event testEvent = createTestEvent(OTHER_USER_ID);
        eventList.add(testEvent);

        // Get view
        eventAdapter.getView(0, mockConvertView, mockParent);

        // Verify manage icon is gone
        verify(mockManageEventIcon).setVisibility(View.GONE);
    }

    @Test
    public void testGetView_EventFields() {
        // Create test event
        Event testEvent = createTestEvent(CURRENT_USER_ID);
        testEvent.setEventTitle("Test Event");
        testEvent.setEventStartDate("2024-01-01");
        eventList.add(testEvent);

        // Get view
        eventAdapter.getView(0, mockConvertView, mockParent);

        // Verify event fields are set correctly
        verify(mockEventTitleTextView).setText("Test Event");
        verify(mockEventDateTextView).setText("2024-01-01");
    }

    private Event createTestEvent(String organizerId) {
        Event event = new Event();
        event.setEventId("test_event_id");
        event.setEventTitle("Test Event");
        event.setEventStartDate("2024-01-01");
        event.setEventImageUrl("https://test.com/image.jpg");
        event.setOrganizerID(organizerId);
        return event;
    }
}
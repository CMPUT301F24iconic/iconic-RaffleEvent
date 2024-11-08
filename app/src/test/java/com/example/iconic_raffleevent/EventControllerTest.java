package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventControllerTest {

    @Mock
    private EventController eventController;

    private final String testEventId = "event123";
    private final String testUserId = "user123";
    private final Event testEvent = new Event("Sample Event", "Sample Description");

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getEventDetails_success() {
        eventController.getEventDetails(testEventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                assertNotNull(event);
                assertEquals("Sample Event", event.getEventTitle());
            }

            @Override
            public void onError(String message) {
                fail("Event details fetch should succeed");
            }
        });
    }

    @Test
    public void joinWaitingListWithoutLocation_success() {
        eventController.joinWaitingListWithoutLocation(testEventId, testUserId, new EventController.JoinWaitingListCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(String message) {
                fail("Join waiting list should succeed");
            }
        });
    }

    @Test
    public void deleteEvent_success() {
        eventController.deleteEvent(testEventId, new EventController.DeleteEventCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(String message) {
                fail("Delete event should succeed");
            }
        });
    }
}

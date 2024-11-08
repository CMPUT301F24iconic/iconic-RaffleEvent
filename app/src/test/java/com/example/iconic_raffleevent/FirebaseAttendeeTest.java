package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.model.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FirebaseAttendeeTest {

    @Mock
    private FirebaseAttendee firebaseAttendee;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addEvent_success() {
        Event testEvent = new Event("New Event", "This is a test event");
        firebaseAttendee.addEvent(testEvent, mockUser, new FirebaseAttendee.AddEventCallback() {
            @Override
            public void onSuccess() {
                assertTrue(true);
            }

            @Override
            public void onError(String message) {
                fail("Add event should succeed");
            }
        });
    }

    @Test
    public void getEventDetails_success() {
        firebaseAttendee.getEventDetails("event123", new FirebaseAttendee.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                assertNotNull(event);
                assertEquals("New Event", event.getEventTitle());
            }

            @Override
            public void onError(String message) {
                fail("Get event details should succeed");
            }
        });
    }
}

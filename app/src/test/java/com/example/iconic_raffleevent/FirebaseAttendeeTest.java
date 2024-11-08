package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FirebaseAttendee;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FirebaseAttendeeTest {

    @Mock
    private FirebaseAttendee firebaseAttendee;
    private User mockUser;

    @Before
    public void setUp() {
        mockUser = new User();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addEvent_success() {
        Event testEvent = new Event();
        firebaseAttendee.addEvent(testEvent, mockUser);
    }

    @Test
    public void getEventDetails_success() {
        firebaseAttendee.getEventDetails("event123", new EventController.EventDetailsCallback() {
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

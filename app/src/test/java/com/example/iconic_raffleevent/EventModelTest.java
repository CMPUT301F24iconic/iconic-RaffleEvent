package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.Event;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventModelTest {
    private Event mockEvent() {
        Event event = new Event();
        event.setEventId("E1234");
        event.setEventTitle("Annual Conference");
        event.setEventDescription("A yearly gathering for industry leaders.");
        event.setEventLocation("Edmonton Convention Centre");
        event.setEventStartDate("2024-10-01");
        event.setEventStartTime("09:00 AM");
        event.setEventEndDate("2024-10-01");
        event.setEventEndTime("05:00 PM");
        event.setEventImageUrl("conference.jpg");
        event.setMaxAttendees(200);
        event.setGeolocationRequired(Boolean.FALSE);

        return event;
    }

    @Test
    void testCheckEventID() {
        Event event = mockEvent();
        assertEquals("E1234", event.getEventId());
    }

    @Test
    void testCheckEventTitle() {
        Event event = mockEvent();
        assertEquals("Annual Conference", event.getEventTitle());
    }

    @Test
    void testCheckEventDescription() {
        Event event = mockEvent();
        assertEquals("A yearly gathering for industry leaders.", event.getEventDescription());
    }

    @Test
    void testCheckEventLocation() {
        Event event = mockEvent();
        assertEquals("Edmonton Convention Centre", event.getEventLocation());
    }

    @Test
    void testCheckEventStartDate() {
        Event event = mockEvent();
        assertEquals("2024-10-01", event.getEventStartDate());
    }

    @Test
    void testCheckEventStartTime() {
        Event event = mockEvent();
        assertEquals("09:00 AM", event.getEventStartTime());
    }

    @Test
    void testCheckEventEndDate() {
        Event event = mockEvent();
        assertEquals("2024-10-01", event.getEventEndDate());
    }

    @Test
    void testCheckEventEndTime() {
        Event event = mockEvent();
        assertEquals("05:00 PM", event.getEventEndTime());
    }

    @Test
    void testCheckEventImageURL() {
        Event event = mockEvent();
        assertEquals("conference.jpg", event.getEventImageUrl());
    }

    @Test
    void testCheckMaxAttendees() {
        Event event = mockEvent();
        assertEquals(200, event.getMaxAttendees());
    }

    @Test
    void testCheckGeolocationRequired() {
        Event event = mockEvent();
        assertFalse(event.isGeolocationRequired());
    }

    @Test
    void testCheckWaitlistSize() {
        Event event = mockEvent();
        assertEquals(0, event.getWaitingList().size());
    }

    @Test
    void testCheckRegisteredListSize() {
        Event event = mockEvent();
        assertEquals(0, event.getRegisteredAttendees().size());
    }

    @Test
    void testChangeEventID() {
        Event event = mockEvent();
        assertEquals("E1234", event.getEventId());
        event.setEventId("123");
        assertEquals("123", event.getEventId());
    }

    @Test
    void testChangeEventTitle() {
        Event event = mockEvent();
        assertEquals("Annual Conference", event.getEventTitle());
        event.setEventTitle("Title");
        assertEquals("Title", event.getEventTitle());
    }

    @Test
    void testChangeEventDescription() {
        Event event = mockEvent();
        assertEquals("A yearly gathering for industry leaders.", event.getEventDescription());
        event.setEventDescription("Test Description");
        assertEquals("Test Description", event.getEventDescription());
    }

    @Test
    void testChangeEventLocation() {
        Event event = mockEvent();
        assertEquals("Edmonton Convention Centre", event.getEventLocation());
        event.setEventLocation("UofA");
        assertEquals("UofA", event.getEventLocation());
    }

    @Test
    void testChangeEventStartDate() {
        Event event = mockEvent();
        assertEquals("2024-10-01", event.getEventStartDate());
        event.setEventStartDate("2024-11-05");
        assertEquals("2024-11-05", event.getEventStartDate());
    }

    @Test
    void testChangeEventStartTime() {
        Event event = mockEvent();
        assertEquals("09:00 AM", event.getEventStartTime());
        event.setEventStartTime("10:00 AM");
        assertEquals("10:00 AM", event.getEventStartTime());
    }

    @Test
    void testChangeEventEndDate() {
        Event event = mockEvent();
        assertEquals("2024-10-01", event.getEventEndDate());
        event.setEventEndDate("2024-11-30");
        assertEquals("2024-11-30", event.getEventEndDate());
    }

    @Test
    void testChangeEventEndTime() {
        Event event = mockEvent();
        assertEquals("05:00 PM", event.getEventEndTime());
        event.setEventEndTime("10:00 PM");
        assertEquals("10:00 PM", event.getEventEndTime());
    }

    @Test
    void testChangeEventImageURL() {
        Event event = mockEvent();
        assertEquals("conference.jpg", event.getEventImageUrl());
        event.setEventImageUrl("studio.jpg");
        assertEquals("studio.jpg", event.getEventImageUrl());
    }

    @Test
    void testChangeMaxAttendees() {
        Event event = mockEvent();
        assertEquals(200, event.getMaxAttendees());
        event.setMaxAttendees(250);
        assertEquals(250, event.getMaxAttendees());
    }

    @Test
    void testChangeGeolocationRequired() {
        Event event = mockEvent();
        assertFalse(event.isGeolocationRequired());
        event.setGeolocationRequired(Boolean.TRUE);
        assertTrue(event.isGeolocationRequired());
    }

    @Test
    void testChangeWaitlistSize() {
        Event event = mockEvent();
        assertEquals(0, event.getWaitingList().size());
        event.addWaitingListEntrant("U1");
        assertEquals(1, event.getWaitingList().size());
    }

    @Test
    void testChangeRegisteredListSize() {
        Event event = mockEvent();
        assertEquals(0, event.getRegisteredAttendees().size());
        event.addRegisteredAttendees("U1");
        assertEquals(1, event.getRegisteredAttendees().size());
    }

}

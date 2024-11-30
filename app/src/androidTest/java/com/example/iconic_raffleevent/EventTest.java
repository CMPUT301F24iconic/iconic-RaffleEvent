package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;

import com.example.iconic_raffleevent.model.Event;

import org.junit.Test;

public class EventTest {

    @Test
    public void testGetEventId() {
        Event event = new Event();
        event.setEventId("TEST123");
        assertEquals("TEST123", event.getEventId());
    }

    @Test
    public void testGetEventTitle() {
        Event event = new Event();
        event.setEventTitle("Birthday Party");
        assertEquals("Birthday Party", event.getEventTitle());
    }

    @Test
    public void testGetEventDescription() {
        Event event = new Event();
        event.setEventDescription("Fun party with games");
        assertEquals("Fun party with games", event.getEventDescription());
    }

    @Test
    public void testGetEventLocation() {
        Event event = new Event();
        event.setEventLocation("Edmonton");
        assertEquals("Edmonton", event.getEventLocation());
    }

    @Test
    public void testGetEventStartDate() {
        Event event = new Event();
        event.setEventStartDate("2024-03-01");
        assertEquals("2024-03-01", event.getEventStartDate());
    }

    @Test
    public void testGetEventEndDate() {
        Event event = new Event();
        event.setEventEndDate("2024-03-02");
        assertEquals("2024-03-02", event.getEventEndDate());
    }

    @Test
    public void testGetEventStartTime() {
        Event event = new Event();
        event.setEventStartTime("10:00 AM");
        assertEquals("10:00 AM", event.getEventStartTime());
    }

    @Test
    public void testGetEventEndTime() {
        Event event = new Event();
        event.setEventEndTime("2:00 PM");
        assertEquals("2:00 PM", event.getEventEndTime());
    }

    @Test
    public void testGetEventImageUrl() {
        Event event = new Event();
        event.setEventImageUrl("https://test.com/image.jpg");
        assertEquals("https://test.com/image.jpg", event.getEventImageUrl());
    }

    @Test
    public void testGetOrganizerId() {
        Event event = new Event();
        event.setOrganizerID("ORG123");
        assertEquals("ORG123", event.getOrganizerID());
    }
}
package com.example.iconic_raffleevent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.iconic_raffleevent.model.Notification;

public class NotificationModelTest {
    private Notification mockNotification() {
        return new Notification();
    }

    @Test
    void testGetNotificationID() {
        Notification notification = mockNotification();
        assertNull(notification.getNotificationId());
    }

    @Test
    void testGetUserID() {
        Notification notification = mockNotification();
        assertNull(notification.getUserId());
    }

    @Test
    void testGetMessage() {
        Notification notification = mockNotification();
        assertNull(notification.getMessage());
    }

    @Test
    void testGetEventID() {
        Notification notification = mockNotification();
        assertNull(notification.getEventId());
    }

    @Test
    void testGetIsRead() {
        Notification notification = mockNotification();
        assertFalse(notification.isRead());
    }

    @Test
    void testUpdateNotificationID() {
        Notification notification = mockNotification();
        assertNull(notification.getNotificationId());
        notification.setNotificationId("123");
        assertEquals("123", notification.getNotificationId());
    }

    @Test
    void testUpdateUserID() {
        Notification notification = mockNotification();
        assertNull(notification.getUserId());
        notification.setUserId("123");
        assertEquals("123", notification.getUserId());
    }

    @Test
    void testUpdateMessage() {
        Notification notification = mockNotification();
        assertNull(notification.getMessage());
        notification.setMessage("Test message");
        assertEquals("Test message", notification.getMessage());
    }

    @Test
    void testUpdateEventID() {
        Notification notification = mockNotification();
        assertNull(notification.getEventId());
        notification.setEventId("123");
        assertEquals("123", notification.getEventId());
    }

    @Test
    void testUpdateIsRead() {
        Notification notification = mockNotification();
        assertFalse(notification.isRead());
        notification.setRead(Boolean.TRUE);
        assertTrue(notification.isRead());
    }
}

package com.example.iconic_raffleevent;

import org.junit.jupiter.api.Test;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Tests Currently are not working, need to find a way to test firebase
 */
public class UserModelTest {
    private User mockUser() {
        User user = new User();
        user.setUserId("123");
        user.setName("wack");
        user.setUsername("Testname");
        user.setEmail("test123@ualberta.ca");
        user.setLocationPermission(Boolean.FALSE);
        user.setNotificationsEnabled(Boolean.FALSE);
        return user;
    }
    private Event mockEvent() {
        return new Event();
    }

    private Notification mockNotification() {
        return new Notification();
    }

    @Test
    void testCheckUsername() {
        User user = mockUser();
        assertEquals("Testname", user.getUsername());
    }

    @Test
    void testCheckName() {
        User user = mockUser();
        assertEquals("wack", user.getName());
    }

    @Test
    void testCheckID() {
        User user = mockUser();
        assertEquals("123", user.getUserId());
    }

    @Test
    void testCheckEmail() {
        User user = mockUser();
        assertEquals("test123@ualberta.ca", user.getEmail());
    }

    @Test
    void testCheckPhoneNumber() {
        User user = mockUser();
        assertNull(user.getPhoneNo());
    }

    @Test
    void testProfilePhoto() {
        User user = mockUser();
        assertNull(user.getProfileImageUrl());
    }

    @Test
    void testCheckLocationPermission() {
        User user = mockUser();
        assertFalse(user.isLocationPermission());
    }

    @Test
    void testCheckNotificationsEnabled() {
        User user = mockUser();
        assertFalse(user.isNotificationsEnabled());
    }

    @Test
    void testCheckWaitlistSize() {
        User user = mockUser();
        assertEquals(0, user.getWaitingListEventIds().size());
    }

    @Test
    void testCheckRegisteredEventsSize() {
        User user = mockUser();
        assertEquals(0, user.getRegisteredEventIds().size());
    }

    @Test
    void testSetUsername() {
        User user = mockUser();
        user.setUsername("Aiden123");
        assertEquals("Aiden123", user.getUsername());
    }

    @Test
    void testSetName() {
        User user = mockUser();
        user.setName("Aiden");
        assertEquals("Aiden", user.getName());
    }

    @Test
    void testSetID() {
        User user = mockUser();
        user.setUserId("1234");
        assertEquals("1234", user.getUserId());
    }

    @Test
    void testSetEmail() {
        User user = mockUser();
        user.setEmail("Change123@yahoo.ca");
        assertEquals("Change123@yahoo.ca", user.getEmail());
    }

    @Test
    void testSetPhoneNumber() {
        User user = mockUser();
        user.setPhoneNo("403-444-4444");
        assertEquals("403-444-4444", user.getPhoneNo());
    }

    @Test
    void testSetPhotoURL() {
        User user = mockUser();
        user.setProfileImageUrl("xyz.html");
        assertEquals("xyz.html", user.getProfileImageUrl());
    }

    @Test
    void testSetLocationPermissions() {
        User user = mockUser();
        user.setLocationPermission(Boolean.TRUE);
        assertTrue(user.isLocationPermission());
    }

    @Test
    void testSetNotificationsEnabled() {
        User user = mockUser();
        user.setNotificationsEnabled(Boolean.TRUE);
        assertTrue(user.isNotificationsEnabled());
    }

    @Test
    void testAddWaitlistId() {
        User user = mockUser();
        user.addWaitingListEvent("EventID");
        assertEquals(1, user.getWaitingListEventIds().size());
    }

    @Test
    void testAddRegisteredEventId() {
        User user = mockUser();
        user.addRegisteredEvent("EventID");
        assertEquals(1, user.getRegisteredEventIds().size());
    }

    @Test
    void testDefaultConstructorValues() {
        User user = new User();
        assertNotNull(user.getWaitingListEventIds());
        assertTrue(user.getWaitingListEventIds().isEmpty());
        assertNotNull(user.getRegisteredEventIds());
        assertTrue(user.getRegisteredEventIds().isEmpty());
        assertTrue(user.isNotificationsEnabled());
    }
}





package com.example.iconic_raffleevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserModelTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("123");
        testUser.setName("wack");
        testUser.setUsername("Testname");
        testUser.setEmail("test123@ualberta.ca");
        testUser.setLocationPermission(false);
        testUser.setGeneralNotificationPref(false);
        testUser.setProfileImageUrl("");  // Initialize with empty string instead of null
        testUser.setPhoneNo("");  // Initialize with empty string instead of null
    }

    @Test
    void testCheckUsername() {
        assertEquals("Testname", testUser.getUsername());
    }

    @Test
    void testCheckName() {
        assertEquals("wack", testUser.getName());
    }

    @Test
    void testCheckID() {
        assertEquals("123", testUser.getUserId());
    }

    @Test
    void testCheckEmail() {
        assertEquals("test123@ualberta.ca", testUser.getEmail());
    }

    @Test
    void testCheckPhoneNumber() {
        assertEquals("", testUser.getPhoneNo(),
                "Phone number should be empty string by default");
    }

    @Test
    void testProfilePhoto() {
        assertEquals("", testUser.getProfileImageUrl(),
                "Profile image URL should be empty string by default");
    }

    @Test
    void testCheckLocationPermission() {
        assertFalse(testUser.isLocationPermission());
    }

    @Test
    void testCheckNotificationsEnabled() {
        assertFalse(testUser.isGeneralNotificationPref());
    }

    @Test
    void testCheckWaitlistSize() {
        assertNotNull(testUser.getWaitingListEventIds(),
                "Waiting list should be initialized");
        assertEquals(0, testUser.getWaitingListEventIds().size());
    }

    @Test
    void testCheckRegisteredEventsSize() {
        assertNotNull(testUser.getRegisteredEventIds(),
                "Registered events list should be initialized");
        assertEquals(0, testUser.getRegisteredEventIds().size());
    }

    @Test
    void testSetUsername() {
        testUser.setUsername("Aiden123");
        assertEquals("Aiden123", testUser.getUsername());
    }

    @Test
    void testSetName() {
        testUser.setName("Aiden");
        assertEquals("Aiden", testUser.getName());
    }

    @Test
    void testSetID() {
        testUser.setUserId("1234");
        assertEquals("1234", testUser.getUserId());
    }

    @Test
    void testSetEmail() {
        testUser.setEmail("Change123@yahoo.ca");
        assertEquals("Change123@yahoo.ca", testUser.getEmail());
    }

    @Test
    void testSetPhoneNumber() {
        testUser.setPhoneNo("403-444-4444");
        assertEquals("403-444-4444", testUser.getPhoneNo());
    }

    @Test
    void testSetPhotoURL() {
        testUser.setProfileImageUrl("xyz.html");
        assertEquals("xyz.html", testUser.getProfileImageUrl());
    }

    @Test
    void testSetLocationPermissions() {
        testUser.setLocationPermission(true);
        assertTrue(testUser.isLocationPermission());
    }

    @Test
    void testSetNotificationsEnabled() {
        testUser.setGeneralNotificationPref(true);
        assertTrue(testUser.isGeneralNotificationPref());
    }

    @Test
    void testAddWaitlistId() {
        testUser.addWaitingListEvent("EventID");
        assertEquals(1, testUser.getWaitingListEventIds().size());
        assertTrue(testUser.getWaitingListEventIds().contains("EventID"));
    }

    @Test
    void testAddRegisteredEventId() {
        testUser.addRegisteredEvent("EventID");
        assertEquals(1, testUser.getRegisteredEventIds().size());
        assertTrue(testUser.getRegisteredEventIds().contains("EventID"));
    }

//    @Test
//    void testDefaultConstructor() {
//        User defaultUser = new User();
//
//        // Check initialization of lists
//        assertNotNull(defaultUser.getWaitingListEventIds());
//        assertTrue(defaultUser.getWaitingListEventIds().isEmpty());
//        assertNotNull(defaultUser.getRegisteredEventIds());
//        assertTrue(defaultUser.getRegisteredEventIds().isEmpty());
//
//        // Check default values
//        assertEquals("", defaultUser.getUserId());
//        assertEquals("", defaultUser.getUsername());
//        assertEquals("", defaultUser.getName());
//        assertEquals("", defaultUser.getEmail());
//        assertEquals("", defaultUser.getPhoneNo());
//        assertEquals("", defaultUser.getProfileImageUrl());
//
//        // Check default boolean flags
//        assertTrue(defaultUser.isGeneralNotificationPref());
//        assertFalse(defaultUser.isLocationPermission());
//    }

    @Test
    void testNotificationPreferences() {
        // Test general notification preference
        testUser.setGeneralNotificationPref(true);
        assertTrue(testUser.isGeneralNotificationPref());
        testUser.setGeneralNotificationPref(false);
        assertFalse(testUser.isGeneralNotificationPref());
    }

    @Test
    void testUserRoles() {
        // Test default role
        assertTrue(testUser.getRoles().contains("entrant"));

        // Test adding new role
        testUser.addRole("organizer");
        assertTrue(testUser.getRoles().contains("organizer"));

        // Test removing role
        testUser.removeRole("organizer");
        assertFalse(testUser.getRoles().contains("organizer"));

        // Test admin role check
        assertFalse(testUser.checkAdminRole());
        testUser.addRole("admin");
        assertTrue(testUser.checkAdminRole());
    }

    @Test
    void testFacilityId() {
        String facilityId = "facility123";
        testUser.setFacilityId(facilityId);
        assertEquals(facilityId, testUser.getFacilityId());
    }
}
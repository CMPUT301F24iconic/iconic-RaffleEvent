//package com.example.iconic_raffleevent;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.timeout;
//import static org.mockito.Mockito.verify;
//
//import android.content.Context;
//import android.net.Uri;
//
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.example.iconic_raffleevent.controller.EventController;
//import com.example.iconic_raffleevent.controller.FacilityController;
//import com.example.iconic_raffleevent.controller.UserController;
//import com.example.iconic_raffleevent.model.Event;
//import com.example.iconic_raffleevent.model.Facility;
//import com.example.iconic_raffleevent.model.User;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.GeoPoint;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//@RunWith(AndroidJUnit4.class)
//public class FirebaseControllerTest {
//
//    private static final long TIMEOUT = 5000; // 5 second timeout for async operations
//
//    @Mock
//    private FirebaseFirestore mockDb;
//
//    private Context context;
//    private UserController userController;
//    private EventController eventController;
//    private FacilityController facilityController;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        context = ApplicationProvider.getApplicationContext();
//        FirebaseApp.initializeApp(context);
//
//        // Initialize controllers with mocked Firebase instance
//        userController = new UserController("test-user-id", context);
//        eventController = new EventController();
//        facilityController = new FacilityController();
//    }
//
//    // Helper methods for creating test data
//    private User createTestUser() {
//        User user = new User();
//        user.setUserId("test-user-id");
//        user.setName("Test User");
//        user.setEmail("test@example.com");
//        user.setPhoneNo("123-456-7890");
//        return user;
//    }
//
//    private Event createTestEvent() {
//        Event event = new Event();
//        event.setEventId("test-event-id");
//        event.setEventTitle("Test Event");
//        event.setEventDescription("Test Description");
//        event.setMaxAttendees(100);
//        event.setGeolocationRequired(true);
//        return event;
//    }
//
//    private Facility createTestFacility() {
//        Facility facility = new Facility();
//        facility.setId("test-facility-id");
//        facility.setFacilityName("Test Facility");
//        facility.setFacilityLocation("Test Location");
//        facility.setCreator(createTestUser());
//        return facility;
//    }
//
//    // User Controller Tests
//    @Test
//    public void testAddUser() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        User testUser = createTestUser();
//
//        userController.addUser(testUser, new UserController.AddUserCallback() {
//            @Override
//            public void onSuccess() {
//                latch.countDown();
//            }
//
//            @Override
//            public void onError(String message) {
//                assertTrue("Failed to add user: " + message, false);
//                latch.countDown();
//            }
//        });
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    @Test
//    public void testUpdateUserProfile() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        User testUser = createTestUser();
//
//        userController.updateProfile(testUser, "Updated Name", "updated@example.com", "987-654-3210",
//                new UserController.UpdateProfileCallback() {
//                    @Override
//                    public void onProfileUpdated() {
//                        latch.countDown();
//                    }
//
//                    @Override
//                    public void onError(String message) {
//                        assertTrue("Failed to update profile: " + message, false);
//                        latch.countDown();
//                    }
//                });
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    // Event Controller Tests
//    @Test
//    public void testCreateEvent() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        Event testEvent = createTestEvent();
//        User testUser = createTestUser();
//
//        eventController.saveEventToDatabase(testEvent, testUser);
//        latch.countDown();
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    @Test
//    public void testJoinWaitingList() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        String eventId = "test-event-id";
//        String userId = "test-user-id";
//        GeoPoint location = new GeoPoint(53.5461, -113.4937); // Edmonton coordinates
//
//        eventController.joinWaitingListWithLocation(eventId, userId, location,
//                new EventController.JoinWaitingListCallback() {
//                    @Override
//                    public void onSuccess() {
//                        latch.countDown();
//                    }
//
//                    @Override
//                    public void onError(String message) {
//                        assertTrue("Failed to join waiting list: " + message, false);
//                        latch.countDown();
//                    }
//                });
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    // Facility Controller Tests
//    @Test
//    public void testCreateFacility() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        Facility testFacility = createTestFacility();
//
//        facilityController.createFacility(testFacility, new FacilityController.FacilityCreationCallback() {
//            @Override
//            public void onFacilityCreated(String facilityId) {
//                assertNotNull(facilityId);
//                latch.countDown();
//            }
//
//            @Override
//            public void onError(String message) {
//                assertTrue("Failed to create facility: " + message, false);
//                latch.countDown();
//            }
//        });
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    @Test
//    public void testGetAllFacilities() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//
//        facilityController.getAllFacilities(new FacilityController.FacilityListCallback() {
//            @Override
//            public void onFacilitiesFetched(ArrayList<Facility> facilities) {
//                assertNotNull(facilities);
//                latch.countDown();
//            }
//
//            @Override
//            public void onError(String message) {
//                assertTrue("Failed to fetch facilities: " + message, false);
//                latch.countDown();
//            }
//        });
//
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//
//    // Test cleanup - should be called after each test if needed
//    private void cleanup() throws InterruptedException {
//        CountDownLatch latch = new CountDownLatch(1);
//        // Add cleanup code here
//        latch.countDown();
//        assertTrue(latch.await(TIMEOUT, TimeUnit.MILLISECONDS));
//    }
//}
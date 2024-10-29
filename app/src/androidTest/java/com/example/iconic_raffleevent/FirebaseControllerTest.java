package com.example.iconic_raffleevent;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
//import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.controller.OnUserRetrievedListener;
import com.example.iconic_raffleevent.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/*
    Tests Currently are not working, need to find a way to test firebase
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseControllerTest {

    private User mockUser() {
        User user = new User();
        user.setName("wack");
        user.setEmail("test123@ualberta.ca");
        return user;
    }

    private Event mockEvent() {
        return new Event();
    }

    /*
    private Facility mockFacility() {
        return new Facility("testFacility", "Edmonton",
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), 300, mockUser());
    }

     */

    private UserController mockUserController(User user) {
        return new UserController(user);
    }

    private EventController mockEventController() {
        return new EventController();
    }

    @Test
    public void testAddUser() {
        User testUser = mockUser();
        UserController controllerForUser = mockUserController(testUser);
        OnUserRetrievedListener mockListener = mock(OnUserRetrievedListener.class);

        // Add the user to the database
       // controllerForUser.addUserToDatabase(testUser);

        // Fetch user from database
        EventController controllerForEvent = mockEventController();
       // controllerForEvent.getUser(testUser.getUserId());

        // Capture the callback in the listener to verify the retrieved user
        verify(mockListener, times(1)).onUserRetrieved(testUser);

        // Assert that the retrieved user is the one expected
        assertEquals(testUser.getName(), "testname"); // Replace with the expected name
    }
}


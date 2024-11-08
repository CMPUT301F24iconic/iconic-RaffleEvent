package com.example.iconic_raffleevent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for the OnUserRetrievedListener interface.
 * This test verifies the callback handling when a user is successfully retrieved or not.
 */
public class OnUserRetrievedListenerTest {

    @Mock
    private UserController.OnUserRetrievedListener listenerMock;

    private UserController userController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        userController = mock(UserController.class);  // Mocked user controller for user retrieval
    }

    /**
     * Test case for when a user is successfully retrieved.
     * It verifies that the onUserRetrieved method is called with the correct user data.
     */
    @Test
    public void testOnUserRetrieved_Success() {
        User expectedUser = new User();
        expectedUser.setUserId("testUserId");
        expectedUser.setName("John Doe");

        doAnswer(invocation -> {
            UserController.OnUserRetrievedListener callback = invocation.getArgument(0);
            callback.onUserRetrieved(expectedUser);  // Simulate success
            return null;
        }).when(userController).getUserInformation((UserController.UserFetchCallback) any(UserController.OnUserRetrievedListener.class));

        userController.getUserInformation((UserController.UserFetchCallback) listenerMock);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(listenerMock).onUserRetrieved(captor.capture());

        User retrievedUser = captor.getValue();
        assertEquals("testUserId", retrievedUser.getUserId());
        assertEquals("John Doe", retrievedUser.getName());
    }

    /**
     * Test case for when no user is found.
     * It verifies that the onUserRetrieved method is called with null when the user is not found.
     */
    @Test
    public void testOnUserRetrieved_NoUserFound() {
        doAnswer(invocation -> {
            UserController.OnUserRetrievedListener callback = invocation.getArgument(0);
            callback.onUserRetrieved(null);  // Simulate no user found
            return null;
        }).when(userController).getUserInformation((UserController.UserFetchCallback) any(UserController.OnUserRetrievedListener.class));

        userController.getUserInformation((UserController.UserFetchCallback) listenerMock);

        verify(listenerMock).onUserRetrieved(null);
    }

    /**
     * Test case for when an error occurs during user retrieval.
     * It verifies that onUserRetrieved is not called, and the error handling is managed elsewhere.
     */
    @Test
    public void testOnUserRetrieved_Error() {
        doThrow(new RuntimeException("Database error"))
                .when(userController)
                .getUserInformation((UserController.UserFetchCallback) any(UserController.OnUserRetrievedListener.class));

        try {
            userController.getUserInformation((UserController.UserFetchCallback) listenerMock);
        } catch (RuntimeException e) {
            assertEquals("Database error", e.getMessage());
        }

        verify(listenerMock, never()).onUserRetrieved(any());
    }

    /**
     * Test case for checking if multiple users can be retrieved sequentially.
     * It verifies that onUserRetrieved is called with the correct data each time.
     */
    @Test
    public void testOnUserRetrieved_MultipleUsers() {
        User firstUser = new User();
        firstUser.setUserId("user1");
        firstUser.setName("Alice");

        User secondUser = new User();
        secondUser.setUserId("user2");
        secondUser.setName("Bob");

        // Simulate retrieval of first user
        doAnswer(invocation -> {
            UserController.OnUserRetrievedListener callback = invocation.getArgument(0);
            callback.onUserRetrieved(firstUser);
            return null;
        }).when(userController).getUserInformation((UserController.UserFetchCallback) any(UserController.OnUserRetrievedListener.class));

        userController.getUserInformation((UserController.UserFetchCallback) listenerMock);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(listenerMock).onUserRetrieved(captor.capture());
        assertEquals("Alice", captor.getValue().getName());

        // Reset and simulate retrieval of second user
        reset(listenerMock);
        doAnswer(invocation -> {
            UserController.OnUserRetrievedListener callback = invocation.getArgument(0);
            callback.onUserRetrieved(secondUser);
            return null;
        }).when(userController).getUserInformation((UserController.UserFetchCallback) any(UserController.OnUserRetrievedListener.class));

        userController.getUserInformation((UserController.UserFetchCallback) listenerMock);

        verify(listenerMock).onUserRetrieved(secondUser);
        assertEquals("Bob", captor.getValue().getName());
    }
}

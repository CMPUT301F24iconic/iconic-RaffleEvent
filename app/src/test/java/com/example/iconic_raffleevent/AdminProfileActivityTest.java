package com.example.iconic_raffleevent;

import android.content.Intent;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.AdminProfileActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;

@RunWith(AndroidJUnit4.class)
public class AdminProfileActivityTest {

    private FirebaseOrganizer firebaseOrganizerMock;

    @Before
    public void setup() {
        Intents.init();
        firebaseOrganizerMock = Mockito.mock(FirebaseOrganizer.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that user profiles load successfully and are displayed in the ListView.
     */
    @Test
    public void testLoadUserList_Success() {
        ArrayList<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setUsername("TestUser");
        mockUsers.add(user);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetUsersCallback callback = invocation.getArgument(0);
            callback.onUsersFetched(mockUsers); // Simulate successful user fetch
            return null;
        }).when(firebaseOrganizerMock).getAllUsers(any(FirebaseOrganizer.GetUsersCallback.class));

        ActivityScenario.launch(AdminProfileActivity.class);

        onView(withId(R.id.profile_list_view)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when loading user profiles fails.
     */
    @Test
    public void testLoadUserList_Failure() {
        doAnswer(invocation -> {
            FirebaseOrganizer.GetUsersCallback callback = invocation.getArgument(0);
            callback.onError("Failed to load profiles"); // Simulate fetch error
            return null;
        }).when(firebaseOrganizerMock).getAllUsers(any(FirebaseOrganizer.GetUsersCallback.class));

        ActivityScenario.launch(AdminProfileActivity.class);

        onView(withText("Error loading profiles: Failed to load profiles"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that deleting a user profile shows a success message and refreshes the list.
     */
    @Test
    public void testDeleteUser_Success() {
        ArrayList<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setUserId("user1");
        user.setUsername("TestUser");
        mockUsers.add(user);

        // Simulate successful user fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetUsersCallback callback = invocation.getArgument(0);
            callback.onUsersFetched(mockUsers);
            return null;
        }).when(firebaseOrganizerMock).getAllUsers(any(FirebaseOrganizer.GetUsersCallback.class));

        // Simulate successful deletion
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteUserCallback callback = invocation.getArgument(1);
            callback.onSuccess();
            return null;
        }).when(firebaseOrganizerMock).deleteUser(eq("user1"), any(FirebaseOrganizer.DeleteUserCallback.class));

        ActivityScenario.launch(AdminProfileActivity.class);

        // Click on the user profile to delete
        onView(withId(R.id.profile_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for successful deletion toast
        onView(withText("Profile deleted successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when user profile deletion fails.
     */
    @Test
    public void testDeleteUser_Failure() {
        ArrayList<User> mockUsers = new ArrayList<>();
        User user = new User();
        user.setUserId("user1");
        user.setUsername("TestUser");
        mockUsers.add(user);

        // Simulate successful user fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetUsersCallback callback = invocation.getArgument(0);
            callback.onUsersFetched(mockUsers);
            return null;
        }).when(firebaseOrganizerMock).getAllUsers(any(FirebaseOrganizer.GetUsersCallback.class));

        // Simulate deletion error
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteUserCallback callback = invocation.getArgument(1);
            callback.onError("Delete failed");
            return null;
        }).when(firebaseOrganizerMock).deleteUser(eq("user1"), any(FirebaseOrganizer.DeleteUserCallback.class));

        ActivityScenario.launch(AdminProfileActivity.class);

        // Click on the user profile to delete
        onView(withId(R.id.profile_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for deletion error toast
        onView(withText("Error deleting profile: Delete failed"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

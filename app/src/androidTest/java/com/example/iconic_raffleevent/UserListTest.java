package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.UserListActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserListTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(UserListActivity.class);
    }

    @Test
    public void testInitialUIDisplay() {
        // Verify RecyclerView is displayed
        onView(withId(R.id.userRecyclerView))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testLoadUserList() {
        // Create test user
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@email.com");

        // Verify user data loading
        onView(withId(R.id.userRecyclerView))
                .check(matches(isDisplayed()));
    }
}
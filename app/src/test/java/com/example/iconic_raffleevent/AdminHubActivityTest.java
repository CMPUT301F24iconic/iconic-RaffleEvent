package com.example.iconic_raffleevent;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.view.AdminHubActivity;
import com.example.iconic_raffleevent.view.EventListForAdminActivity;
import com.example.iconic_raffleevent.view.ImageManagementActivity;
import com.example.iconic_raffleevent.view.UserListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AdminHubActivityTest {

    private static final String CORRECT_PASSWORD = "adminPass123";
    private static final String INCORRECT_PASSWORD = "wrongPass";

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test for successful admin authentication.
     * Verifies that providing the correct password grants access and shows a toast.
     */
    @Test
    public void testAdminAuthenticationSuccess() {
        ActivityScenario.launch(AdminHubActivity.class);

        // Enter correct password in dialog
        onView(withText("Admin Password")).check(matches(isDisplayed()));
        onView(withId(android.R.id.input)).perform(typeText(CORRECT_PASSWORD));
        onView(withText("OK")).perform(click());

        // Verify that a toast message appears for successful authentication
        onView(withText("Admin access granted"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test for failed admin authentication.
     * Verifies that an incorrect password closes the activity.
     */
    @Test
    public void testAdminAuthenticationFailure() {
        ActivityScenario<AdminHubActivity> scenario = ActivityScenario.launch(AdminHubActivity.class);

        // Enter incorrect password in dialog
        onView(withText("Admin Password")).check(matches(isDisplayed()));
        onView(withId(android.R.id.input)).perform(typeText(INCORRECT_PASSWORD));
        onView(withText("OK")).perform(click());

        // Verify that a toast message appears for incorrect password and activity is closed
        onView(withText("Incorrect password"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        scenario.onActivity(activity -> assertTrue(activity.isFinishing()));
    }

    /**
     * Test button navigation when authenticated.
     * Verifies that clicking buttons redirects to the correct activities if authenticated.
     */
    @Test
    public void testButtonNavigationWhenAuthenticated() {
        ActivityScenario.launch(AdminHubActivity.class);

        // Authenticate as admin
        onView(withText("Admin Password")).check(matches(isDisplayed()));
        onView(withId(android.R.id.input)).perform(typeText(CORRECT_PASSWORD));
        onView(withText("OK")).perform(click());

        // Click "Manage Users" button
        onView(withId(R.id.manage_users_button)).perform(click());
        Intents.intended(hasComponent(UserListActivity.class.getName()));

        // Click "Manage Events" button
        onView(withId(R.id.manage_events_button)).perform(click());
        Intents.intended(hasComponent(EventListForAdminActivity.class.getName()));

        // Click "Manage Images" button
        //onView(withId(R.id.manage_images_button)).perform(click());
        //Intents.intended(hasComponent(ImageManagementActivity.class.getName()));

        // Click "Manage QR Code" button
        //onView(withId(R.id.manage_qr_code_button)).perform(click());
        //Intents.intended(hasComponent(QRCodeManagementActivity.class.getName()));
    }

    /**
     * Test button clicks without authentication.
     * Verifies that clicking buttons prompts for the password dialog if not authenticated.
     */
    @Test
    public void testButtonNavigationWithoutAuthentication() {
        ActivityScenario.launch(AdminHubActivity.class);

        // Ensure the admin password dialog is displayed
        onView(withText("Admin Password")).check(matches(isDisplayed()));

        // Dismiss the dialog by clicking "Cancel"
        onView(withText("Cancel")).perform(click());

        // Click "Manage Users" button and check if password dialog reappears
        onView(withId(R.id.manage_users_button)).perform(click());
        onView(withText("Admin Password")).check(matches(isDisplayed()));

        // Cancel the dialog again
        onView(withText("Cancel")).perform(click());

        // Click "Manage Events" button and check if password dialog reappears
        onView(withId(R.id.manage_events_button)).perform(click());
        onView(withText("Admin Password")).check(matches(isDisplayed()));
    }
}

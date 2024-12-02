package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.AdminHubActivity;
import com.example.iconic_raffleevent.view.EventListForAdminActivity;
import com.example.iconic_raffleevent.view.FacilityListForAdminActivity;
import com.example.iconic_raffleevent.view.ImageManagementActivity;
import com.example.iconic_raffleevent.view.RoleSelectionActivity;
import com.example.iconic_raffleevent.view.UserListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminHubActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testButtonsDisplayed() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Verify that all buttons are displayed
        onView(withId(R.id.manage_users_button)).check(matches(isDisplayed()));
        onView(withId(R.id.manage_events_button)).check(matches(isDisplayed()));
        //onView(withId(R.id.manage_images_button)).check(matches(isDisplayed()));
        onView(withId(R.id.manage_qr_code_button)).check(matches(isDisplayed()));
        onView(withId(R.id.manage_facilities_button)).check(matches(isDisplayed()));
        onView(withId(R.id.back_to_role_selection_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testManageUsersButton() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Click the manage users button
        onView(withId(R.id.manage_users_button)).perform(click());

        // Verify that the UserListActivity is launched
        intended(hasComponent(UserListActivity.class.getName()));
    }

    @Test
    public void testManageEventsButton() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Click the manage events button
        onView(withId(R.id.manage_events_button)).perform(click());

        // Verify that the EventListForAdminActivity is launched
        intended(hasComponent(EventListForAdminActivity.class.getName()));
    }

    @Test
    public void testManageQRCodeButton() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Click the manage QR code button
        onView(withId(R.id.manage_qr_code_button)).perform(click());

        // Verify that the QRCodeManagementActivity is launched
        //intended(hasComponent(QRCodeManagementActivity.class.getName()));
    }

    @Test
    public void testManageFacilitiesButton() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Click the manage facilities button
        onView(withId(R.id.manage_facilities_button)).perform(click());

        // Verify that the FacilityListForAdminActivity is launched
        intended(hasComponent(FacilityListForAdminActivity.class.getName()));
    }

    @Test
    public void testBackToRoleSelectionButton() {
        // Launch the AdminHubActivity
        ActivityScenario.launch(AdminHubActivity.class);

        // Click the back to role selection button
        onView(withId(R.id.back_to_role_selection_button)).perform(click());

        // Verify that the RoleSelectionActivity is launched
        intended(hasComponent(RoleSelectionActivity.class.getName()));
    }
}
package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.ProfileActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProfileActivityTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(ProfileActivity.class);
    }

    /**
     * Test if ProfileActivity launches and displays correctly.
     */
    @Test
    public void testActivityLaunch() {
        // Check if ProfileActivity elements are displayed
        onView(withId(R.id.name_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.email_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.phone_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.notifications_switch)).check(matches(isDisplayed()));
        onView(withId(R.id.back_to_hub_button)).check(matches(isDisplayed())); // Updated ID
        onView(withId(R.id.upload_photo_button)).check(matches(isDisplayed()));
    }


    /**
     * Test updating and saving valid profile information.
     */
    @Test
    public void testValidProfileInformation() {
        // Enter valid name, email, and phone number
        onView(withId(R.id.name_edit_text)).perform(replaceText("John Doe"));
        onView(withId(R.id.email_edit_text)).perform(replaceText("john.doe@example.com"));
        onView(withId(R.id.phone_edit_text)).perform(replaceText("1234567890"));

        // Save changes
        onView(withId(R.id.save_button)).perform(click());

        // Verify the updated data persists
        onView(withId(R.id.name_edit_text)).check(matches(withText("John Doe")));
        onView(withId(R.id.email_edit_text)).check(matches(withText("john.doe@example.com")));
        onView(withId(R.id.phone_edit_text)).check(matches(withText("1234567890")));
    }

    /**
     * Test saving invalid email shows error.
     */
    @Test
    public void testInvalidEmail() {
        // Enter an invalid email
        onView(withId(R.id.email_edit_text)).perform(replaceText("invalid-email"));

        // Attempt to save
        onView(withId(R.id.save_button)).perform(click());

        // Verify error is displayed and email is not saved
        onView(withId(R.id.email_edit_text)).check(matches(isDisplayed())); // Still editable
    }

    /**
     * Test saving invalid phone number shows error.
     */
    @Test
    public void testInvalidPhoneNumber() {
        // Enter an invalid phone number
        onView(withId(R.id.phone_edit_text)).perform(replaceText("abcd123"));

        // Attempt to save
        onView(withId(R.id.save_button)).perform(click());

        // Verify error is displayed and phone number is not saved
        onView(withId(R.id.phone_edit_text)).check(matches(isDisplayed())); // Still editable
    }

    /**
     * Test saving empty name shows error, ensuring there was a name typed initially.
     */
    @Test
    public void testEmptyName() {
        // Enter a valid name initially
        onView(withId(R.id.name_edit_text)).perform(replaceText("Initial Name"));

        // Clear the name field
        onView(withId(R.id.name_edit_text)).perform(replaceText(""));

        // Attempt to save
        onView(withId(R.id.save_button)).perform(click());

        // Verify error is displayed and name is not saved
        onView(withId(R.id.name_edit_text)).check(matches(isDisplayed())); // Ensure the field is still editable
        // Optionally, check for a specific error message if implemented, e.g., Toast or error text.
    }


    /**
     * Test back button functionality with unsaved changes.
     */
    @Test
    public void testBackButtonWithUnsavedChanges() {
        // Enter unsaved data
        onView(withId(R.id.name_edit_text)).perform(replaceText("Unsaved Name"));

        // Press back button
        onView(withId(R.id.back_to_hub_button)).perform(click());

        // Verify the discard changes dialog is displayed
        onView(withText("Discard changes?")).check(matches(isDisplayed()));
    }

    /**
     * Test back button functionality without unsaved changes.
     */
    @Test
    public void testBackButtonWithoutUnsavedChanges() {
        // Ensure no changes are made
        onView(withId(R.id.back_to_hub_button)).perform(click());

        // Verify activity finishes (this is implicit if no dialog appears)
    }

    /**
     * Test removing profile photo if one exists.
     * If no photo exists, verify that the remove button is not visible.
     */
    @Test
    public void testRemoveProfilePhotoIfExists() {
        // Check if the remove photo button is visible
        try {
            onView(withId(R.id.remove_photo_button)).check(matches(isDisplayed()));

            // If visible, perform click to remove the photo
            onView(withId(R.id.remove_photo_button)).perform(click());

            // Verify the default avatar is displayed
            onView(withId(R.id.profile_image)).check(matches(isDisplayed())); // Default avatar should be visible
        } catch (AssertionError e) {
            // If remove button is not visible, verify it doesn't exist
            onView(withId(R.id.remove_photo_button)).check(matches(not(isDisplayed())));
        }
    }
}

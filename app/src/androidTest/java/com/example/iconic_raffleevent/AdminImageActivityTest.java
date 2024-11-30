package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.AdminImageActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminImageActivityTest {

    @Before
    public void setUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminImageActivity.class);
        ActivityScenario.launch(intent);
    }

    @Test
    public void testInitialUIElements() {
        // Verify that the ListView is displayed
        onView(withId(R.id.image_list_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testImageListClick() {
        // Simulate a click on the first item in the list
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.image_list_view))
                .atPosition(0)
                .perform(click());

        // Verify that the delete dialog is displayed
        onView(withText("Delete Image")).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteImageDialogButtons() {
        // Simulate a click on the first item in the list
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.image_list_view))
                .atPosition(0)
                .perform(click());

        // Check that the "Delete" button is displayed
        onView(withText("Delete")).check(matches(isDisplayed()));

        // Check that the "Cancel" button is displayed
        onView(withText("Cancel")).check(matches(isDisplayed()));

        // Simulate clicking the "Cancel" button
        onView(withText("Cancel")).perform(click());
    }
}

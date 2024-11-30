package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.view.AdminEventActivity;
import com.example.iconic_raffleevent.view.EventDetailsForAdminActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminEventTest {

    @Test
    public void testEventListDisplay() {
        // Launch activity
        ActivityScenario.launch(AdminEventActivity.class);

        // Verify list view is displayed
        onView(withId(R.id.event_list_view)).check(matches(isDisplayed()));
    }
}
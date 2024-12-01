package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.MainActivity;
import com.example.iconic_raffleevent.view.NewUserActivity;
import com.example.iconic_raffleevent.view.RoleSelectionActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    /**
     * Test for regular user navigation to EventListActivity.
     */
    @Test
    public void testNavigateToEventListActivity() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
    }
}

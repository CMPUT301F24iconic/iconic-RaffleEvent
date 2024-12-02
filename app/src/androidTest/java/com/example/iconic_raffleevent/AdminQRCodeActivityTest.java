package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.AdminQRCodeActivity;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminQRCodeActivityTest {

    @Rule
    public ActivityScenarioRule<AdminQRCodeActivity> activityRule =
            new ActivityScenarioRule<>(AdminQRCodeActivity.class);

    @Before
    public void setUp() {
        // Launch the activity with the default intent
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AdminQRCodeActivity.class);
        activityRule.getScenario().onActivity(activity -> activity.startActivity(intent));
    }

    @Test
    public void testQRCodeListViewDisplayed() {
        // Check if the ListView is displayed
        onView(withId(R.id.qrcode_list_view)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testQRCodeListItemClick() {
//        // Simulate clicking on the first item in the ListView
//        onData(Matchers.anything())
//                .inAdapterView(withId(R.id.qrcode_list_view))
//                .atPosition(0)
//                .perform(click());
//
//        // Verify the delete confirmation dialog is displayed
//        onView(withId(android.R.id.message)).check(matches(isDisplayed()));
//    }
}
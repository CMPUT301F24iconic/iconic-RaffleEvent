package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.view.AdminFacilityActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminFacilityActivityTest {

    @Test
    public void testFacilityListDisplay() {
        // Launch the AdminFacilityActivity
        ActivityScenario.launch(AdminFacilityActivity.class);

        // Verify that the facility list view is displayed
        onView(withId(R.id.facility_list_view)).check(matches(isDisplayed()));
    }
}
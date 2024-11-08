package com.example.iconic_raffleevent;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.view.AdminFacilityActivity;

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
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class AdminFacilityActivityTest {

    private FirebaseOrganizer firebaseOrganizerMock;

    @Before
    public void setup() {
        // Mock the FirebaseOrganizer class
        firebaseOrganizerMock = Mockito.mock(FirebaseOrganizer.class);
    }

    /**
     * Test that facilities load successfully and are displayed in the ListView.
     */
    @Test
    public void testLoadFacilityList_Success() {
        ArrayList<Facility> mockFacilities = new ArrayList<>();
        Facility facility = new Facility();
        facility.setFacilityName("Test Facility");
        mockFacilities.add(facility);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback callback = invocation.getArgument(0);
            callback.onFacilitiesFetched(mockFacilities); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllFacilities(any(FirebaseOrganizer.GetFacilitiesCallback.class));

        ActivityScenario.launch(AdminFacilityActivity.class);

        onView(withId(R.id.facility_list_view)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when loading facilities fails.
     */
    @Test
    public void testLoadFacilityList_Failure() {
        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback callback = invocation.getArgument(0);
            callback.onError("Error loading facilities"); // Simulate a fetch error
            return null;
        }).when(firebaseOrganizerMock).getAllFacilities(any(FirebaseOrganizer.GetFacilitiesCallback.class));

        ActivityScenario.launch(AdminFacilityActivity.class);

        // Check that the toast message is displayed for the error
        onView(withText("Error loading facilities: Error loading facilities"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that the delete facility confirmation dialog appears and that facility deletion is handled.
     */
    @Test
    public void testDeleteFacility_Success() {
        ArrayList<Facility> mockFacilities = new ArrayList<>();
        Facility facility = new Facility();
        facility.setId("testFacilityId");
        facility.setFacilityName("Test Facility");
        mockFacilities.add(facility);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback callback = invocation.getArgument(0);
            callback.onFacilitiesFetched(mockFacilities); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllFacilities(any(FirebaseOrganizer.GetFacilitiesCallback.class));

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteFacilityCallback callback = invocation.getArgument(1);
            callback.onSuccess(); // Simulate a successful deletion
            return null;
        }).when(firebaseOrganizerMock).deleteFacility(eq("testFacilityId"), any(FirebaseOrganizer.DeleteFacilityCallback.class));

        ActivityScenario.launch(AdminFacilityActivity.class);

        // Simulate clicking on the facility to trigger the delete dialog
        onView(withId(R.id.facility_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check that a toast message for successful deletion is displayed
        onView(withText("Facility deleted successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that the delete facility operation handles failure and shows an error message.
     */
    @Test
    public void testDeleteFacility_Failure() {
        ArrayList<Facility> mockFacilities = new ArrayList<>();
        Facility facility = new Facility();
        facility.setId("testFacilityId");
        facility.setFacilityName("Test Facility");
        mockFacilities.add(facility);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback callback = invocation.getArgument(0);
            callback.onFacilitiesFetched(mockFacilities); // Simulate a successful fetch
            return null;
        }).when(firebaseOrganizerMock).getAllFacilities(any(FirebaseOrganizer.GetFacilitiesCallback.class));

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteFacilityCallback callback = invocation.getArgument(1);
            callback.onError("Delete failed"); // Simulate a delete error
            return null;
        }).when(firebaseOrganizerMock).deleteFacility(eq("testFacilityId"), any(FirebaseOrganizer.DeleteFacilityCallback.class));

        ActivityScenario.launch(AdminFacilityActivity.class);

        // Simulate clicking on the facility to trigger the delete dialog
        onView(withId(R.id.facility_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check that a toast message for failed deletion is displayed
        onView(withText("Error deleting facility: Delete failed"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

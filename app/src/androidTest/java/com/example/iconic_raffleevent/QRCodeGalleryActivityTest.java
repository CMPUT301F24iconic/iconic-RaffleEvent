package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.iconic_raffleevent.view.QRCodeGalleryActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class QRCodeGalleryActivityTest {

    @Rule
    public ActivityTestRule<QRCodeGalleryActivity> activityRule =
            new ActivityTestRule<>(QRCodeGalleryActivity.class, true, false);

    @Before
    public void setUp() {
        // Launch the activity
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), QRCodeGalleryActivity.class);
        activityRule.launchActivity(intent);
    }

    @Test
    public void testRecyclerViewDisplayed() {
        // Check if the RecyclerView is displayed
        onView(withId(R.id.qr_code_recycler_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testQRCodeGalleryInteraction() {
        // Assume we have some data loaded; click on the first QR code item
        onView(withId(R.id.qr_code_recycler_view))
                .perform(click()); // Clicks the first item

        // Check if the dialog for deleting a QR code appears
        onView(withText("Delete QR Code"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteQRCodeConfirmation() {
        // Simulate clicking on a QR code
        onView(withId(R.id.qr_code_recycler_view))
                .perform(click());

        // Check if the delete confirmation dialog is displayed
        onView(withText("Are you sure you want to delete the QR code for event:"))
                .check(matches(isDisplayed()));

        // Confirm deletion
        onView(withText("Yes")).perform(click());

        // Check if a toast message is displayed (This is a general interaction check; visual toast verification isn't always feasible)
        // Log output or further assertions can validate behavior here.
    }

    @Test
    public void testEmptyQRCodeListMessage() {
        // Simulate the RecyclerView being empty
        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().qrCodeGalleryAdapter.updateQRCodeList(new ArrayList<>());
        });

        // Check for the toast message indicating no QR codes
        // A mock interaction to check the user feedback behavior
    }
}

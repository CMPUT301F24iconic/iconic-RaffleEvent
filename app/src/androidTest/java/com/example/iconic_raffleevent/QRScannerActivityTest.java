package com.example.iconic_raffleevent;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.example.iconic_raffleevent.view.QRScannerActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QRScannerActivityTest {

    /**
     * Test to verify navigation to EventListActivity when cancel button is clicked.
     */
    @Test
    public void testCancelButtonNavigation() {
        ActivityScenario.launch(QRScannerActivity.class);

        handleCameraPermission();

        // Click the cancel button
        onView(withId(R.id.cancel_button)).perform(click());
    }

    /**
     * Test to verify gallery button opens the image picker.
     */
    @Test
    public void testGalleryButtonOpensImagePicker() {
        ActivityScenario.launch(QRScannerActivity.class);

        handleCameraPermission();

        // Click the gallery button
        onView(withId(R.id.gallery_button)).perform(click());

        // Verify that the gallery intent is triggered
        Intent expectedIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    /**
     * Handles the camera permission dialog by clicking "While using the app".
     */
    private void handleCameraPermission() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Handle the permission dialog if it appears
        UiObject allowPermission = device.findObject(
                new UiSelector().text("While using the app").className("android.widget.Button"));
        try {
            if (allowPermission.exists() && allowPermission.isClickable()) {
                allowPermission.click();
            }
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
    }
}
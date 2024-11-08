package com.example.iconic_raffleevent;

import android.content.Intent;
import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.QRCodeData;
import com.example.iconic_raffleevent.view.AdminQRCodeActivity;

import org.junit.After;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;

@RunWith(AndroidJUnit4.class)
public class AdminQRCodeActivityTest {

    private FirebaseOrganizer firebaseOrganizerMock;

    @Before
    public void setup() {
        Intents.init();
        firebaseOrganizerMock = Mockito.mock(FirebaseOrganizer.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that QR codes load successfully and are displayed in the ListView.
     */
    @Test
    public void testLoadQRCodeList_Success() {
        ArrayList<QRCodeData> mockQRCodes = new ArrayList<>();
        QRCodeData qrCode = new QRCodeData();
        qrCode.setQrCodeName("Test QR Code");
        mockQRCodes.add(qrCode);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetQRCodesCallback callback = invocation.getArgument(0);
            callback.onQRCodesFetched(mockQRCodes); // Simulate successful QR code fetch
            return null;
        }).when(firebaseOrganizerMock).getAllQRCodes(any(FirebaseOrganizer.GetQRCodesCallback.class));

        ActivityScenario.launch(AdminQRCodeActivity.class);

        onView(withId(R.id.qrcode_list_view)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when loading QR codes fails.
     */
    @Test
    public void testLoadQRCodeList_Failure() {
        doAnswer(invocation -> {
            FirebaseOrganizer.GetQRCodesCallback callback = invocation.getArgument(0);
            callback.onError("Failed to load QR codes"); // Simulate fetch error
            return null;
        }).when(firebaseOrganizerMock).getAllQRCodes(any(FirebaseOrganizer.GetQRCodesCallback.class));

        ActivityScenario.launch(AdminQRCodeActivity.class);

        onView(withText("Error loading QR codes: Failed to load QR codes"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that deleting a QR code shows a success message and refreshes the list.
     */
    @Test
    public void testDeleteQRCode_Success() {
        ArrayList<QRCodeData> mockQRCodes = new ArrayList<>();
        QRCodeData qrCode = new QRCodeData();
        qrCode.setQrCodeId("qrCode1");
        qrCode.setQrCodeName("Test QR Code");
        mockQRCodes.add(qrCode);

        // Simulate successful QR code fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetQRCodesCallback callback = invocation.getArgument(0);
            callback.onQRCodesFetched(mockQRCodes);
            return null;
        }).when(firebaseOrganizerMock).getAllQRCodes(any(FirebaseOrganizer.GetQRCodesCallback.class));

        // Simulate successful deletion
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteQRCodeCallback callback = invocation.getArgument(1);
            callback.onSuccess();
            return null;
        }).when(firebaseOrganizerMock).deleteQRCode(eq("qrCode1"), any(FirebaseOrganizer.DeleteQRCodeCallback.class));

        ActivityScenario.launch(AdminQRCodeActivity.class);

        // Click on the QR code to delete
        onView(withId(R.id.qrcode_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for successful deletion toast
        onView(withText("QR code deleted successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when QR code deletion fails.
     */
    @Test
    public void testDeleteQRCode_Failure() {
        ArrayList<QRCodeData> mockQRCodes = new ArrayList<>();
        QRCodeData qrCode = new QRCodeData();
        qrCode.setQrCodeId("qrCode1");
        qrCode.setQrCodeName("Test QR Code");
        mockQRCodes.add(qrCode);

        // Simulate successful QR code fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetQRCodesCallback callback = invocation.getArgument(0);
            callback.onQRCodesFetched(mockQRCodes);
            return null;
        }).when(firebaseOrganizerMock).getAllQRCodes(any(FirebaseOrganizer.GetQRCodesCallback.class));

        // Simulate deletion error
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteQRCodeCallback callback = invocation.getArgument(1);
            callback.onError("Delete failed");
            return null;
        }).when(firebaseOrganizerMock).deleteQRCode(eq("qrCode1"), any(FirebaseOrganizer.DeleteQRCodeCallback.class));

        ActivityScenario.launch(AdminQRCodeActivity.class);

        // Click on the QR code to delete
        onView(withId(R.id.qrcode_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for deletion error toast
        onView(withText("Error deleting QR code: Delete failed"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

package com.example.iconic_raffleevent;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.view.EventDetailsActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(AndroidJUnit4.class)
@Config(sdk = 28)
public class QRScannerActivityTest {

    @Mock
    private EventController mockEventController;

    @Mock
    private BarcodeDetector mockBarcodeDetector;

    @Mock
    private CameraSource mockCameraSource;

    private QRScannerActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize mocks
        when(mockBarcodeDetector.isOperational()).thenReturn(true);

        // Launch activity
        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);
        scenario.onActivity(activity -> {
            this.activity = activity;
            activity.eventController = mockEventController;
        });
    }

    @Test
    public void testInitialization() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            // Verify views are initialized
            assertNotNull(activity.findViewById(R.id.camera_preview));
            assertNotNull(activity.findViewById(R.id.qr_code_text));
            assertNotNull(activity.findViewById(R.id.scanning_wave));
            assertNotNull(activity.findViewById(R.id.message_text));
            assertNotNull(activity.findViewById(R.id.cancel_button));
            assertNotNull(activity.findViewById(R.id.gallery_button));
            assertNotNull(activity.findViewById(R.id.flashlight_button));
        });
    }

    @Test
    public void testCancelButton() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            Button cancelButton = activity.findViewById(R.id.cancel_button);
            cancelButton.performClick();

            // Verify intent to EventListActivity
            Intent expectedIntent = new Intent(activity, EventListActivity.class);
            Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
        });
    }

    @Test
    public void testGalleryButton() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            Button galleryButton = activity.findViewById(R.id.gallery_button);
            galleryButton.performClick();

            // Verify gallery intent
            Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(Intent.ACTION_PICK, actualIntent.getAction());
            assertEquals(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, actualIntent.getData());
        });
    }

    @Test
    public void testFlashlightButton() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            ImageButton flashlightButton = activity.findViewById(R.id.flashlight_button);

            // Initial state
            assertEquals(R.drawable.ic_flashlight_off,
                    Shadows.shadowOf(flashlightButton.getBackground()).getCreatedFromResId());

            // Test toggle
            flashlightButton.performClick();
            assertEquals(R.drawable.ic_flashlight_on,
                    Shadows.shadowOf(flashlightButton.getBackground()).getCreatedFromResId());

            flashlightButton.performClick();
            assertEquals(R.drawable.ic_flashlight_off,
                    Shadows.shadowOf(flashlightButton.getBackground()).getCreatedFromResId());
        });
    }

    @Test
    public void testQRCodeProcessing_Success() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            String testQrData = "test_event_123";

            // Mock successful QR code scan
            doAnswer(invocation -> {
                EventController.ScanQRCodeCallback callback = invocation.getArgument(1);
                callback.onEventFound("event123");
                return null;
            }).when(mockEventController).scanQRCode(anyString(), any());

            // Simulate QR code detection
            activity.processQRCodeData(testQrData);

            // Verify event details activity launch
            Intent actualIntent = Shadows.shadowOf(activity).getNextStartedActivity();
            assertEquals(EventDetailsActivity.class.getName(),
                    actualIntent.getComponent().getClassName());
            assertEquals("event123", actualIntent.getStringExtra("eventId"));
        });
    }

    @Test
    public void testQRCodeProcessing_Error() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            String testQrData = "invalid_qr_code";
            String errorMessage = "Invalid QR code";

            // Mock QR code scan error
            doAnswer(invocation -> {
                EventController.ScanQRCodeCallback callback = invocation.getArgument(1);
                callback.onError(errorMessage);
                return null;
            }).when(mockEventController).scanQRCode(anyString(), any());

            // Simulate QR code detection
            activity.processQRCodeData(testQrData);

            // Verify error handling
            TextView qrCodeText = activity.findViewById(R.id.qr_code_text);
            assertEquals(testQrData, qrCodeText.getText().toString());
        });
    }

    @Test
    public void testPermissionResult() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            // Simulate permission result
            activity.onRequestPermissionsResult(
                    100, // Using public constant for camera permission
                    new String[]{android.Manifest.permission.CAMERA},
                    new int[]{PackageManager.PERMISSION_GRANTED}
            );

            // Verify camera preview is visible
            View cameraPreview = activity.findViewById(R.id.camera_preview);
            assertEquals(View.VISIBLE, cameraPreview.getVisibility());
        });
    }

    @Test
    public void testActivityLifecycle() {
        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);

        // Test onPause
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            View scanningWave = activity.findViewById(R.id.scanning_wave);
            assertEquals(View.GONE, scanningWave.getVisibility());
        });

        // Test onResume
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED);
        scenario.onActivity(activity -> {
            View scanningWave = activity.findViewById(R.id.scanning_wave);
            assertEquals(View.VISIBLE, scanningWave.getVisibility());
        });

        // Test onDestroy
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED);
    }

    @Test
    public void testBarcodeDetection() {
        ActivityScenario.launch(QRScannerActivity.class).onActivity(activity -> {
            when(mockBarcodeDetector.isOperational()).thenReturn(true);

            TextView qrCodeText = activity.findViewById(R.id.qr_code_text);
            assertNotNull(qrCodeText);
            assertEquals("", qrCodeText.getText().toString());
        });
    }
}
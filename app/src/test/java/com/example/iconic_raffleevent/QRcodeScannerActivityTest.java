//package com.example.iconic_raffleevent;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.provider.Settings;
//import android.util.SparseArray;
//import android.widget.Toast;
//
//import androidx.core.content.ContextCompat;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.example.iconic_raffleevent.controller.EventController;
//import com.example.iconic_raffleevent.controller.UserController;
//import com.example.iconic_raffleevent.model.User;
//import com.example.iconic_raffleevent.view.QRScannerActivity;
//import com.google.android.gms.vision.barcode.Barcode;
//import com.google.firebase.firestore.GeoPoint;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.mockito.Mockito.*;
//
//@RunWith(AndroidJUnit4.class)
//public class QRcodeScannerActivityTest {
//
//    private Context context;
//
//    @Mock
//    private UserController mockUserController;
//
//    @Mock
//    private EventController mockEventController;
//
//    private User mockUser;
//    private GeoPoint mockLocation;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        context = ApplicationProvider.getApplicationContext();
//
//        mockUser = new User();
//        mockUser.setUserId("testUserId");
//
//        mockLocation = new GeoPoint(37.7749, -122.4194); // Mock location
//    }
//
//    /**
//     * Test that the camera permission is requested if it is not already granted.
//     */
//    @Test
//    public void testRequestCameraPermission() {
//        // Launch the activity
//        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);
//
//        scenario.onActivity(activity -> {
//            // Simulate permission is not granted
//            when(ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA))
//                    .thenReturn(PackageManager.PERMISSION_DENIED);
//
//            activity.startCamera();
//
//            // Verify that permission request is triggered
//            verify(activity, times(1)).requestPermissions(
//                    new String[]{Manifest.permission.CAMERA},
//                    QRScannerActivity.CAMERA_PERMISSION_REQUEST_CODE);
//        });
//    }
//
//    /**
//     * Test that the QR code scanner processes a valid QR code correctly.
//     */
//    @Test
//    public void testProcessQRCodeData() {
//        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);
//
//        scenario.onActivity(activity -> {
//            // Inject mocks
//            activity.userController = mockUserController;
//            activity.eventController = mockEventController;
//
//            // Simulate a QR code scan
//            String mockQRCodeData = "mockEventId";
//            SparseArray<Barcode> mockBarcodes = new SparseArray<>();
//            Barcode barcode = new Barcode();
//            barcode.displayValue = mockQRCodeData;
//            mockBarcodes.put(0, barcode);
//
//            // Mock user and location retrieval success
//            when(mockUserController.getUserInformation(any())).thenAnswer(invocation -> {
//                UserController.UserFetchCallback callback = invocation.getArgument(0);
//                callback.onUserFetched(mockUser);
//                return null;
//            });
//
//            when(mockUserController.retrieveUserLocation(any(), any())).thenAnswer(invocation -> {
//                UserController.OnLocationReceivedCallback callback = invocation.getArgument(1);
//                callback.onLocationReceived(mockLocation);
//                return null;
//            });
//
//            // Process QR code data
//            activity.processQRCodeData(mockQRCodeData);
//
//            // Verify EventController interaction
//            verify(mockEventController).scanQRCode(
//                    eq(mockQRCodeData), eq("testUserId"), eq(mockLocation), any(EventController.ScanQRCodeCallback.class));
//        });
//    }
//
//    /**
//     * Test that location permissions are requested when required.
//     */
//    @Test
//    public void testRequestLocationPermissions() {
//        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);
//
//        scenario.onActivity(activity -> {
//            // Simulate location permission is not granted
//            when(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION))
//                    .thenReturn(PackageManager.PERMISSION_DENIED);
//
//            activity.getUserLocation("mockQRCodeData");
//
//            // Verify location permissions request
//            verify(activity).requestPermissions(
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
//                    QRScannerActivity.LOCATION_PERMISSION_REQUEST_CODE);
//        });
//    }
//
//    /**
//     * Test that the onError method is triggered if location retrieval fails.
//     */
//    @Test
//    public void testLocationRetrievalFailure() {
//        ActivityScenario<QRScannerActivity> scenario = ActivityScenario.launch(QRScannerActivity.class);
//
//        scenario.onActivity(activity -> {
//            activity.userController = mockUserController;
//
//            doAnswer(invocation -> {
//                UserController.OnLocationReceivedCallback callback = invocation.getArgument(1);
//                callback.onError("Failed to retrieve location");
//                return null;
//            }).when(mockUserController).retrieveUserLocation(any(), any());
//
//            // Attempt to retrieve location with a sample QR code data
//            activity.getUserLocation("mockQRCodeData");
//
//            // Verify that the failure message is shown as a toast
//            onView(withText("Error getting location: Failed to retrieve location"))
//                    .inRoot(new ToastMatcher())
//                    .check(matches(withText("Error getting location: Failed to retrieve location")));
//        });
//    }
//}

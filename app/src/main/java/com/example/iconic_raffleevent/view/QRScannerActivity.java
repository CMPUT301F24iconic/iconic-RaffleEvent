package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;

/**
 * QRScannerActivity is responsible for scanning QR codes using the device's camera,
 * retrieving user information, fetching the user's location, and processing the QR code data.
 * It interacts with the EventController and UserController to handle event-related and user-related actions.
 */
public class QRScannerActivity extends AppCompatActivity {
    private static final String TAG = "QRScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private SurfaceView cameraPreview;
    private TextView qrCodeTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private UserController userController;
    private EventController eventController;
    private User userObj;
    private GeoPoint userLocation;
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Initializes the activity, sets up views, controllers, barcode scanner, and user data.
     *
     * @param savedInstanceState the saved instance state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        initializeViews();
        initializeControllers();
        initializeBarcodeScanner();
        getCurrentUser();
    }

    /**
     * Initializes the views for the QR scanner activity.
     */
    private void initializeViews() {
        cameraPreview = findViewById(R.id.camera_preview);
        qrCodeTextView = findViewById(R.id.qr_code_text);
    }

    /**
     * Initializes controllers for handling user and event-related actions.
     */
    private void initializeControllers() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        eventController = new EventController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Initializes the barcode scanner with a QR code detector and camera source.
     */
    private void initializeBarcodeScanner() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();

        setupCameraPreview();
        setupBarcodeProcessor();
    }

    /**
     * Sets up the camera preview for the QR code scanning.
     */
    private void setupCameraPreview() {
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            }
        });
    }

    /**
     * Sets up the barcode processor for detecting and processing QR codes.
     */
    private void setupBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            private boolean isProcessing = false;

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !isProcessing) {
                    isProcessing = true;
                    String qrCodeData = barcodes.valueAt(0).displayValue;
                    runOnUiThread(() -> {
                        qrCodeTextView.setText(qrCodeData);
                        getUserLocation(qrCodeData);
                    });
                }
            }
        });
    }

    /**
     * Starts the camera for QR code scanning if the camera permission is granted.
     */
    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraSource.start(cameraPreview.getHolder());
            } catch (IOException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
                Toast.makeText(this, "Error starting camera", Toast.LENGTH_SHORT).show();
            }
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Requests permission to access the camera.
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * Processes the QR code data by scanning the event using the EventController.
     *
     * @param qrCodeData the data from the scanned QR code.
     */
    private void processQRCodeData(String qrCodeData) {
        if (userObj == null || userLocation == null) {
            Toast.makeText(this, "User data or location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.scanQRCode(qrCodeData, userObj.getUserId(), userLocation,
                new EventController.ScanQRCodeCallback() {
                    @Override
                    public void onEventFound(String eventId) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(QRScannerActivity.this, EventDetailsActivity.class);
                            intent.putExtra("eventId", eventId);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() ->
                                Toast.makeText(QRScannerActivity.this,
                                        "Error: " + message, Toast.LENGTH_SHORT).show()
                        );
                    }
        });
    }

    /**
     * Fetches the current user's information from the UserController.
     */
    private void getCurrentUser() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(QRScannerActivity.this,
                                    "Unable to load user profile", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(QRScannerActivity.this,
                                "Error loading user profile: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Retrieves the user's current location.
     *
     * @param qrCodeData the QR code data to process after the location is retrieved.
     */
    private void getUserLocation(String qrCodeData) {
        if (!checkLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        userController.retrieveUserLocation(fusedLocationClient,
                new UserController.OnLocationReceivedCallback() {
                    @Override
                    public void onLocationReceived(GeoPoint location) {
                        userLocation = location;
                        processQRCodeData(qrCodeData);
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() ->
                                Toast.makeText(QRScannerActivity.this,
                                        "Error getting location: " + message, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
    }

    /**
     * Checks if the app has permission to access the user's location.
     *
     * @return true if location permissions are granted, false otherwise.
     */
    private boolean checkLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests permission to access the user's location.
     */
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Handles the result of permission requests for camera and location permissions.
     *
     * @param requestCode  the request code.
     * @param permissions  the requested permissions.
     * @param grantResults the results of the permissions request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean permissionGranted = grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (permissionGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (!permissionGranted) {
                    Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * Retrieves the unique user ID based on the device's Android ID.
     *
     * @return a string representing the unique Android ID of the device.
     */
    @SuppressLint("HardwareIds")
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Releases resources associated with the camera source and barcode detector
     * when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
        if (barcodeDetector != null) {
            barcodeDetector.release();
        }
    }
}

package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.SparseArray;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class QRScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private SurfaceView cameraPreview;
    private TextView qrCodeTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private UserController userController;
    private EventController eventController;
    private User currentUser;
    private GeoPoint userLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        initializeViews();
        initializeControllers();
        initializeBarcodeScanner();
        loadUserProfile();
    }

    private void initializeViews() {
        cameraPreview = findViewById(R.id.camera_preview);
        qrCodeTextView = findViewById(R.id.qr_code_text);
    }

    private void initializeControllers() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        eventController = new EventController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
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
                                "Error loading profile: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

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

    private void setupCameraPreview() {
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
    }

    private void setupBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            private boolean isProcessing = false;

            @Override
            public void release() { }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !isProcessing && currentUser != null) {
                    isProcessing = true;
                    runOnUiThread(() -> {
                        String qrCodeData = barcodes.valueAt(0).displayValue;
                        qrCodeTextView.setText(qrCodeData);
                        getUserLocation(() -> processQRCodeData(qrCodeData));
                    });
                }
            }
        });
    }

    private void startCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraSource.start(cameraPreview.getHolder());
            } catch (IOException e) {
                Toast.makeText(this, "Error starting camera: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void processQRCodeData(String qrCodeData) {
        if (currentUser == null || userLocation == null) {
            Toast.makeText(this, "Unable to process QR code: Missing user data or location",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.scanQRCode(qrCodeData, currentUser.getUserId(), userLocation,
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

    private void getUserLocation(Runnable onLocationReceived) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userLocation = new GeoPoint(task.getResult().getLatitude(),
                        task.getResult().getLongitude());
                onLocationReceived.run();
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                startCamera();
                break;
            case LOCATION_PERMISSION_REQUEST_CODE:
                getUserLocation(() -> {});
                break;
        }
    }

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
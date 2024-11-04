package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;

public class QRScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private SurfaceView cameraPreview;
    private TextView qrCodeTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private UserController userController;
    private EventController eventController;
    private User currentUser;
    private User userObj;
    private GeoPoint userLocation;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        cameraPreview = findViewById(R.id.camera_preview);
        qrCodeTextView = findViewById(R.id.qr_code_text);

        // Aiden Teal
        userController = getUserController();
        eventController = new EventController();
        getCurrentUser();
        //currentUser = getCurrentUser();

        /*
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true)
                .build();

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
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    qrCodeTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            String qrCodeData = barcodes.valueAt(0).displayValue;
                            qrCodeTextView.setText(qrCodeData);
                            getUserLocation();
                            processQRCodeData(qrCodeData);
                        }
                    });
                }
            }
        });

         */
    }

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraSource.start(cameraPreview.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void processQRCodeData(String qrCodeData) {
        eventController.scanQRCode(qrCodeData, userObj.getUserId(), userLocation, new EventController.ScanQRCodeCallback() {
            @Override
            public void onEventFound(String eventId) {
                // Navigate to the event details screen
                Intent intent = new Intent(QRScannerActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", eventId);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                // Handle the error
                Toast.makeText(QRScannerActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }

    private void getCurrentUser() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;

                    // "Scan" QR code and see if it works
                    processQRCodeData("event_event1");
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Cannot fetch user information");
            }
        });
    }

    private void getUserLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                userLocation = new GeoPoint(latitude, longitude);
            }
        });
    }
}
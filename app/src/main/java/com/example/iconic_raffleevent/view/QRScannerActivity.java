package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.lang.reflect.Field;

/**
 * QRScannerActivity is responsible for scanning QR codes using the device's camera,
 * retrieving user information, fetching the user's location, and processing the QR code data.
 * It interacts with the EventController and UserController to handle event-related and user-related actions.
 */
public class QRScannerActivity extends AppCompatActivity {
    private static final String TAG = "QRScannerActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private SurfaceView cameraPreview;
    private TextView qrCodeTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private EventController eventController;

    private Button cancelButton;
    private Button galleryButton;
    private ImageButton flashlightButton;
    private boolean isFlashlightOn = false;

    private static final int PICK_IMAGE_REQUEST = 1;

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

        cancelButton = findViewById(R.id.cancel_button);
        galleryButton = findViewById(R.id.gallery_button);
        flashlightButton = findViewById(R.id.flashlight_button)
        ;
        cancelButton.setOnClickListener(v -> {
            // Return to the event list page
            startActivity(new Intent(QRScannerActivity.this, EventListActivity.class));
            finish();
        });

        galleryButton.setOnClickListener(v -> {
            // Open the gallery to select an image
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        flashlightButton.setOnClickListener(v -> {
            toggleFlashlight();
        });
    }

    private void toggleFlashlight() {
        if (cameraSource != null) {
            if (isFlashlightOn) {
                Camera camera = getCamera(cameraSource);
                if (camera != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    flashlightButton.setImageResource(R.drawable.ic_flashlight_off);
                    isFlashlightOn = false;
                }
            } else {
                Camera camera = getCamera(cameraSource);
                if (camera != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    flashlightButton.setImageResource(R.drawable.ic_flashlight_on);
                    isFlashlightOn = true;
                }
            }
        }
    }

    private Camera getCamera(CameraSource cameraSource) {
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    return (Camera) field.get(cameraSource);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return null;
    }

    /**
     * Initializes the views for the QR scanner activity.
     */
    private void initializeViews() {
        cameraPreview = findViewById(R.id.camera_preview);
        qrCodeTextView = findViewById(R.id.qr_code_text);
    }

    /**
     * Initializes controller for handling event-related actions.
     */
    private void initializeControllers() {
        eventController = new EventController();
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
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                startCamera();
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
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
        barcodeDetector.setProcessor(new Detector.Processor<>() {
            private boolean isProcessing = false;

            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !isProcessing) {
                    isProcessing = true;
                    String qrCodeData = barcodes.valueAt(0).displayValue;
                    runOnUiThread(() -> {
                        qrCodeTextView.setText(qrCodeData);
                        processQRCodeData(qrCodeData);
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
        eventController.scanQRCode(qrCodeData,
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

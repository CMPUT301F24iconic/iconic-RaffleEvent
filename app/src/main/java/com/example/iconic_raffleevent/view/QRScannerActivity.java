package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
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

import com.example.iconic_raffleevent.BitmapLuminanceSource;
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
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.io.InputStream;

public class QRScannerActivity extends AppCompatActivity {
    private static final String TAG = "QRScannerActivity";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final int PICK_IMAGE_REQUEST = 102;

    private SurfaceView cameraPreview;
    private TextView qrCodeTextView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;

    private UserController userController;
    private EventController eventController;
    private User userObj;
    private GeoPoint userLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private ImageButton flashlightButton;
    private Button galleryButton, cancelButton;
    private boolean isFlashOn = false;
    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        initializeViews();
        initializeControllers();
        initializeBarcodeScanner();
        fetchCurrentUser(); // Fetch current user on creation
    }

    private void initializeViews() {
        cameraPreview = findViewById(R.id.camera_preview);
        qrCodeTextView = findViewById(R.id.qr_code_text);
        flashlightButton = findViewById(R.id.flashlight_button);
        galleryButton = findViewById(R.id.gallery_button);
        cancelButton = findViewById(R.id.cancel_button);

        flashlightButton.setOnClickListener(v -> toggleFlashlight());
        galleryButton.setOnClickListener(v -> openGallery());
        cancelButton.setOnClickListener(v -> finish());

        // Initialize camera manager for flashlight control
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (hasFlash != null && hasFlash) {
                    cameraId = id;
                    break;
                }
            }
            if (cameraId == null) {
                Toast.makeText(this, "Flashlight not supported on this device", Toast.LENGTH_SHORT).show();
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "Camera ID access error: " + e.getMessage());
        }
    }

    private void initializeControllers() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        eventController = new EventController();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (cameraSource != null) {
                    cameraSource.stop();
                }
            }
        });
    }

    private void setupBarcodeProcessor() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            private boolean isProcessing = false;

            @Override
            public void release() {}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !isProcessing) {
                    isProcessing = true;
                    String qrCodeData = barcodes.valueAt(0).displayValue;
                    runOnUiThread(() -> {
                        qrCodeTextView.setText(qrCodeData);
                        fetchUserLocation(qrCodeData);
                    });
                }
            }
        });
    }

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    private void toggleFlashlight() {
        if (cameraManager != null && cameraId != null) {
            try {
                isFlashOn = !isFlashOn;
                cameraManager.setTorchMode(cameraId, isFlashOn);
                flashlightButton.setImageResource(isFlashOn ? R.drawable.ic_flashlight_on : R.drawable.ic_flashlight_off);
            } catch (CameraAccessException e) {
                Toast.makeText(this, "Error accessing flashlight", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Flashlight toggle error: " + e.getMessage());
            }
        } else {
            Toast.makeText(this, "Flashlight not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                decodeQRCodeFromImage(selectedImageUri);
            }
        }
    }

    private void decodeQRCodeFromImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(bitmap)));
            Result result = new MultiFormatReader().decode(binaryBitmap);
            qrCodeTextView.setText(result.getText());
            processQRCodeData(result.getText());
        } catch (Exception e) {
            Toast.makeText(this, "QR code not found in image", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error decoding QR code from image: " + e.getMessage());
        }
    }

    private void fetchCurrentUser() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                } else {
                    runOnUiThread(() -> Toast.makeText(QRScannerActivity.this, "Unable to load user profile", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(QRScannerActivity.this, "Error loading user profile: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void fetchUserLocation(String qrCodeData) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        processQRCodeData(qrCodeData);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error getting location", Toast.LENGTH_SHORT).show());
    }

    private void processQRCodeData(String qrCodeData) {
        if (userObj == null || userLocation == null) {
            Toast.makeText(this, "User data or location not available", Toast.LENGTH_SHORT).show();
            return;
        }

        eventController.scanQRCode(qrCodeData, userObj.getUserId(), userLocation, new EventController.ScanQRCodeCallback() {
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
                runOnUiThread(() -> Toast.makeText(QRScannerActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

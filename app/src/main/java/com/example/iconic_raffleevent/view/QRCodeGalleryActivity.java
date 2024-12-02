package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.QRCodeController;
import com.example.iconic_raffleevent.model.QRCodeData;

import java.util.ArrayList;

/**
 * Activity to display a gallery of QR codes.
 */
public class QRCodeGalleryActivity extends AppCompatActivity {

    private RecyclerView qrCodeRecyclerView;
    public QRCodeGalleryAdapter qrCodeGalleryAdapter;
    private QRCodeController qrCodeController;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_gallery);

        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        // Initialize RecyclerView and QRCodeController
        qrCodeRecyclerView = findViewById(R.id.qr_code_recycler_view);
        qrCodeController = new QRCodeController();

        // Setup RecyclerView
        qrCodeRecyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns grid
        qrCodeGalleryAdapter = new QRCodeGalleryAdapter(this, new ArrayList<>(), this::onQRCodeClick);
        qrCodeRecyclerView.setAdapter(qrCodeGalleryAdapter);

        // Fetch QR codes and populate RecyclerView
        fetchQRCodeData();

        backButton.setOnClickListener(v -> finish());

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(QRCodeGalleryActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(QRCodeGalleryActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(QRCodeGalleryActivity.this, ProfileActivity.class));
        });
    }

    /**
     * Fetch QR code data from Firestore and populate the RecyclerView.
     */
    private void fetchQRCodeData() {
        qrCodeController.getAllQRCodeData(new QRCodeController.GetQRCodeDataCallback() {
            @Override
            public void onQRCodeDataFetched(ArrayList<QRCodeData> qrCodes) {
                if (!qrCodes.isEmpty()) {
                    qrCodeGalleryAdapter.updateQRCodeList(qrCodes);
                } else {
                    Toast.makeText(QRCodeGalleryActivity.this, "No QR Codes found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(QRCodeGalleryActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Handle QR code item clicks.
     *
     * @param qrCodeData The clicked QR code data.
     */
    private void onQRCodeClick(QRCodeData qrCodeData) {
        // Directly show the delete confirmation dialog
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete QR Code")
                .setMessage("Are you sure you want to delete the QR code for event: " + qrCodeData.getQrCodeName() + "? This action cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> {
                    qrCodeController.deleteQRCodeFromEvent(qrCodeData.getQrCodeId(), new QRCodeController.FirestoreUpdateCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(QRCodeGalleryActivity.this, "QR Code deleted successfully.", Toast.LENGTH_SHORT).show();
                            fetchQRCodeData(); // Refresh the list
                        }

                        @Override
                        public void onError(String message) {
                            Toast.makeText(QRCodeGalleryActivity.this, "Failed to delete QR Code: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

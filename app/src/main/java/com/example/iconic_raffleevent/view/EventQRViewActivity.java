package com.example.iconic_raffleevent.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.model.Event;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EventQRViewActivity extends AppCompatActivity {
    private TextView eventTitle;
    private TextView emptyMessage;
    private ImageView eventQrCodeView;
    private Button shareButton;
    private Button generateButton;

    private ImageButton backButton;

    // Controllers and data related to objects
    private EventController eventController;
    private String eventId;
    private Event eventObj;

    private Bitmap QrBitmap;

    /**
     * Called when the activity is starting. Sets up the layout, initializes components, and
     * begins loading event details and the event qr code image
     *
     * @param savedInstanceState the saved state of the activity if it was previously terminated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qr);

        // Link UI
        eventTitle = findViewById(R.id.qrcode_title);
        emptyMessage = findViewById(R.id.empty_message);
        eventQrCodeView = findViewById(R.id.qrcode_view);
        backButton = findViewById(R.id.back_button);
        shareButton = findViewById(R.id.share_button);
        generateButton = findViewById(R.id.generate_button);

        eventController = new EventController();
        eventId = getIntent().getStringExtra("eventId");

        // Fetch event details and update UI
        fetchEventDetails();

        // Set click listeners for back and share button
        backButton.setOnClickListener(v -> finish());

        shareButton.setOnClickListener(v -> {
            // Allow user to share qr code externally
            shareQrCode();
        });

        generateButton.setOnClickListener(v -> {
            generateNewQRCode();
        });
    }

    /**
     * Fetches the event details from the server.
     * Updates the UI with event information once the event details are successfully fetched.
     */
    private void fetchEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                eventObj = event;
                // Load UI
                updateUI(eventObj);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventQRViewActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the UI with the provided event details.
     * Adjusts button visibility based on the user's role (organizer or participant).
     *
     * @param event The event whose details will be displayed.
     */
    private void updateUI(Event event) {
        if (event != null) {
            String qrUrl = event.getEventQrUrl();

            if (qrUrl != null && !qrUrl.isEmpty()) {
                Glide.with(this)
                        .load(qrUrl)
                        .into(eventQrCodeView);
                shareButton.setVisibility(View.VISIBLE);
                generateButton.setVisibility(View.INVISIBLE);
                emptyMessage.setVisibility(View.INVISIBLE);
                eventQrCodeView.setVisibility(View.VISIBLE);
            } else {
                // Set a placeholder text
                eventQrCodeView.setVisibility(View.INVISIBLE);
                shareButton.setVisibility(View.INVISIBLE);
                emptyMessage.setVisibility(View.VISIBLE);
                generateButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "QR code not available for this event.", Toast.LENGTH_SHORT).show();
            }

            String eventTitleText = event.getEventTitle() + "'s QR Code";
            eventTitle.setText(eventTitleText);
        }
    }

    /**
     * Generates a new QR code for an event. Refreshes activity after creating QR code so page
     * reflects updated information
     */
    private void generateNewQRCode() {
        // Create hashed qr code
        String hashedQrData = "event_" + eventObj.getEventId();
        eventObj.setQrCode(hashedQrData);

        eventController.uploadEventQRCode(eventObj, new EventController.UploadEventQRCodeCallback() {
            @Override
            public void onSuccessfulQRUpload(String qrUrl) {
                eventObj.setEventQrUrl(qrUrl);
                if (qrUrl != null && !qrUrl.isEmpty()) {
                    eventController.updateEventDetails(eventObj);
                    recreate();
                } else {
                    Toast.makeText(EventQRViewActivity.this, "QR Code URL is missing!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(EventQRViewActivity.this, "QR Code generation failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Creates a bitmap of the event qr code that can be shared locally from the user's device.
     * Stores bitmap of QR code on local device so it can be shared.
     * Various methods of sharing but main one is through gmail.
     */
    private void shareQrCode() {
        Bitmap qrCodeBitmap = generateBitmap();
        // Create local file path for bitmap to share
        // Reference: Stack Overflow, Author: Ajay URL: https://stackoverflow.com/questions/8295773/how-can-i-transform-a-bitmap-into-a-uri
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String qrPath = MediaStore.Images.Media.insertImage(this.getContentResolver(), qrCodeBitmap, "QR Code", null);
        Uri qrUri = Uri.parse(qrPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, qrUri);
        intent.putExtra(Intent.EXTRA_TEXT, "Scan this QR code to join the event waitlist. Feel free to share with others!");
        intent.putExtra(Intent.EXTRA_SUBJECT, "QR Code");
        intent.setType("image/jpeg");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    /**
     * Generates a bitmap of event qr code
     * @return Bitmap The bitmap of the event qr code
     */
    private Bitmap generateBitmap() {
        // Create a new Bitmap for the QR code so it can be shared
        QrBitmap = Bitmap.createBitmap(eventQrCodeView.getWidth(), eventQrCodeView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(QrBitmap);
        eventQrCodeView.draw(canvas);

        return QrBitmap;
    }
}

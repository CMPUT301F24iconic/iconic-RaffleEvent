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
    private ImageView eventQrCodeView;
    private Button shareButton;

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
        eventQrCodeView = findViewById(R.id.qrcode_view);
        backButton = findViewById(R.id.back_button);
        shareButton = findViewById(R.id.share_button);

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

            if (qrUrl != null) {
                Glide.with(this)
                        .load(qrUrl)
                        .into(eventQrCodeView);
                shareButton.setVisibility(View.VISIBLE); // Show the share button
            } else {
                // Set a placeholder image when QR code is deleted
                eventQrCodeView.setImageResource(R.drawable.placeholder_image); // Use your placeholder image
                shareButton.setVisibility(View.GONE); // Hide the share button
                Toast.makeText(this, "QR code not available for this event.", Toast.LENGTH_SHORT).show();
            }

            String eventTitleText = event.getEventTitle() + "'s QR Code";
            eventTitle.setText(eventTitleText);
        }
    }

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
        intent.putExtra(Intent.EXTRA_TEXT, "Share this QR code to get people interested in your event");
        intent.putExtra(Intent.EXTRA_SUBJECT, "QR Code");
        intent.setType("image/jpeg");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share via"));
    }

    private Bitmap generateBitmap() {
        // Create a new Bitmap object with the desired width and height
        QrBitmap = Bitmap.createBitmap(eventQrCodeView.getWidth(), eventQrCodeView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(QrBitmap);
        eventQrCodeView.draw(canvas);

        return QrBitmap;
    }
}

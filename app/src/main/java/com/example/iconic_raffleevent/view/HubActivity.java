package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;


public class HubActivity extends AppCompatActivity {
    private Button profileButton;
    private Button eventsButton;
    private Button notificationsButton;
    private Button scanQRCodeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);

        profileButton = findViewById(R.id.profile_button);
        eventsButton = findViewById(R.id.events_button);
        notificationsButton = findViewById(R.id.notifications_button);
        scanQRCodeButton = findViewById(R.id.scan_qr_code_button);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HubActivity.this, ProfileActivity.class));
            }
        });

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HubActivity.this, EventListActivity.class));
            }
        });

        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HubActivity.this, NotificationsActivity.class));
            }
        });

        scanQRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HubActivity.this, QRScannerActivity.class));
            }
        });
    }
}

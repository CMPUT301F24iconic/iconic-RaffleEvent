package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class CreateFacilityActivity extends AppCompatActivity {

    private TextInputEditText facilityNameEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText facilityDetailsEditText;
    private Button saveButton;
    private FacilityController facilityController;
    private User currentUser;  // Assume this is passed in as the organizer user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_facility);

        // Initialize views
        facilityNameEditText = findViewById(R.id.facilityNameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        facilityDetailsEditText = findViewById(R.id.facilityDetailsEditText);
        saveButton = findViewById(R.id.saveButton);

        // Initialize FacilityController
        facilityController = new FacilityController();

        // Set up save button listener
        saveButton.setOnClickListener(v -> saveFacility());
    }

    private void saveFacility() {
        String facilityName = facilityNameEditText.getText().toString().trim();
        String facilityLocation = locationEditText.getText().toString().trim();
        String additionalInfo = facilityDetailsEditText.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(facilityName) || TextUtils.isEmpty(facilityLocation)) {
            Toast.makeText(this, "Facility Name and Location are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Facility object
        Facility facility = new Facility(facilityName, facilityLocation, currentUser);
        facility.setAdditionalInfo(additionalInfo);

        // Use FacilityController to create facility
        facilityController.createFacility(facility, new FacilityController.FacilityCreationCallback() {
            @Override
            public void onFacilityCreated(String facilityId) {
                Toast.makeText(CreateFacilityActivity.this, "Facility created successfully", Toast.LENGTH_SHORT).show();
                // Redirect to event creation page or another activity as needed
                finish();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CreateFacilityActivity.this, "Error creating facility: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.example.iconic_raffleevent.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.model.Facility;
import java.util.ArrayList;

/**
 * Activity that displays a list of facilities for the admin to manage.
 */
public class FacilityListForAdminActivity extends AppCompatActivity {

    private RecyclerView facilityRecyclerView;
    private FacilityAdapter facilityAdapter;
    private FacilityController facilityController;

    /**
     * Initializes the activity, sets up the layout, and loads facilities.
     *
     * @param savedInstanceState Saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_list_for_admin);

        // Initialize RecyclerView and FacilityAdapter
        facilityRecyclerView = findViewById(R.id.facilityRecyclerView);
        facilityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        facilityAdapter = new FacilityAdapter(new ArrayList<>());

        facilityRecyclerView.setAdapter(facilityAdapter);

        // Initialize FacilityController
        facilityController = new FacilityController();

        facilityAdapter.setOnItemClickListener(facility -> showFacilityDetailsDialog(facility));

        // Load facilities
        loadFacilityList();
    }

    /**
     * Fetches the list of facilities and updates the RecyclerView.
     */
    private void loadFacilityList() {
        facilityController.getAllFacilities(new FacilityController.FacilityListCallback() {
            @Override
            public void onFacilitiesFetched(ArrayList<Facility> facilities) {
                facilityAdapter.updateFacilities(facilities);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(FacilityListForAdminActivity.this, "Error loading facilities: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFacilityDetailsDialog(Facility facility) {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_facility_details, null);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Bind data to the dialog views
        TextView facilityNameTextView = dialogView.findViewById(R.id.facilityNameTextView);
        TextView facilityLocationTextView = dialogView.findViewById(R.id.facilityLocationTextView);
        TextView facilityAdditionalInfoTextView = dialogView.findViewById(R.id.facilityAdditionalInfoTextView);
        TextView facilityCreatorTextView = dialogView.findViewById(R.id.facilityCreatorTextView);

        facilityNameTextView.setText(facility.getFacilityName());
        facilityLocationTextView.setText("Location: " + facility.getFacilityLocation());
        facilityAdditionalInfoTextView.setText("Additional Info: " + facility.getAdditionalInfo());
        facilityCreatorTextView.setText("Created by: " + facility.getCreator().getName());

        // Handle Cancel button click
        Button cancelButton = dialogView.findViewById(R.id.cancelFacilityButton);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Handle Delete button click
        Button deleteButton = dialogView.findViewById(R.id.deleteFacilityButton);
        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteFacility(facility.getId());
        });

        dialog.show();
    }

    private void deleteFacility(String facilityId) {
        facilityController.deleteFacility(facilityId, new FacilityController.DeleteFacilityCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(FacilityListForAdminActivity.this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                loadFacilityList(); // Refresh the list after deletion
            }

            @Override
            public void onError(String message) {
                Toast.makeText(FacilityListForAdminActivity.this, "Error deleting facility: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

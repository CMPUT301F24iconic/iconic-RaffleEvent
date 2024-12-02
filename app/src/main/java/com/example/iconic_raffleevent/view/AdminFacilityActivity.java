package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Facility;

import java.util.ArrayList;

/**
 * AdminFacilityActivity provides an interface for administrators to view and manage facilities.
 * This activity displays a list of facilities and allows the admin to delete facilities through
 * Firebase.
 */
public class AdminFacilityActivity extends AppCompatActivity {
    private ListView facilityListView;
    private ArrayAdapter<String> facilityAdapter;
    private ArrayList<Facility> facilityList;
    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Called when the activity is first created. Initializes views and loads the list of facilities.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_facility);

        facilityListView = findViewById(R.id.facility_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadFacilityList();
    }

    /**
     * Loads the list of facilities from Firebase and displays them in the ListView.
     * Sets an item click listener on each facility to allow the admin to delete facilities.
     */
    private void loadFacilityList() {
        firebaseOrganizer.getAllFacilities(new FirebaseOrganizer.GetFacilitiesCallback() {
            @Override
            public void onFacilitiesFetched(ArrayList<Facility> facilities) {
                facilityList = facilities;
                ArrayList<String> facilityNames = new ArrayList<>();
                for (Facility facility : facilities) {
                    facilityNames.add(facility.getFacilityName());
                }
                facilityAdapter = new ArrayAdapter<>(AdminFacilityActivity.this, android.R.layout.simple_list_item_1, facilityNames);
                facilityListView.setAdapter(facilityAdapter);
                facilityListView.setOnItemClickListener((adapterView, view, i, l) -> showDeleteDialog(facilityList.get(i)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminFacilityActivity.this, "Error loading facilities: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog to delete a facility.
     *
     * @param facility The Facility object to be deleted.
     */
    private void showDeleteDialog(Facility facility) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Facility")
                .setMessage("Deleting this facility will also delete all associated events and their media (posters and QR codes). This action cannot be undone.\n\nAre you sure you want to proceed?")
                .setPositiveButton("Delete", (dialog, which) -> deleteFacility(facility))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the specified facility from Firebase and refreshes the facility list upon success.
     *
     * @param facility The Facility object to be deleted.
     */
    private void deleteFacility(Facility facility) {
        firebaseOrganizer.deleteFacility(facility.getId(), new FirebaseOrganizer.DeleteFacilityCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminFacilityActivity.this, "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                loadFacilityList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminFacilityActivity.this, "Error deleting facility: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

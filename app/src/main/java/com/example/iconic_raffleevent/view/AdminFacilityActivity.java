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

public class AdminFacilityActivity extends AppCompatActivity {

    private ListView facilityListView;
    private ArrayAdapter<String> facilityAdapter;
    private ArrayList<Facility> facilityList;
    private FirebaseOrganizer firebaseOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_facility);

        facilityListView = findViewById(R.id.facility_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadFacilityList();
    }

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

    private void showDeleteDialog(Facility facility) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Facility")
                .setMessage("Are you sure you want to delete this facility?")
                .setPositiveButton("Delete", (dialog, which) -> deleteFacility(facility))
                .setNegativeButton("Cancel", null)
                .show();
    }

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

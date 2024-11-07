package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseOrganizer {

    private FirebaseFirestore db;
    private CollectionReference facilitiesCollection;
    private CollectionReference usersCollection;

    public FirebaseOrganizer() {
        db = FirebaseFirestore.getInstance();
        facilitiesCollection = db.collection("Facility");
        usersCollection = db.collection("User");
    }

    // Method to create a facility, add it to Firestore, and update the User with facility reference
    public void createFacility(Facility facility, FacilityCreationCallback callback) {
        DocumentReference facilityRef = facilitiesCollection.document();
        String facilityId = facilityRef.getId();
        facility.setId(facilityId);  // Set the generated ID in the Facility object

        // Save the facility to Firestore
        facilityRef.set(facility)
                .addOnSuccessListener(aVoid -> {
                    // Update the user's facility reference in Firestore after facility creation
                    DocumentReference userRef = usersCollection.document(facility.getCreator().getUserId());
                    userRef.update("facilityId", facilityId)
                            .addOnSuccessListener(aVoid2 -> callback.onFacilityCreated(facilityId))
                            .addOnFailureListener(e -> callback.onError("Failed to update user with facility ID"));
                })
                .addOnFailureListener(e -> callback.onError("Failed to create facility"));
    }

    // Method to check if a user already has a facility
    public void checkUserFacility(String userId, FacilityCheckCallback callback) {
        usersCollection.document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String facilityId = task.getResult().getString("facilityId");
                if (facilityId != null) {
                    callback.onFacilityExists(facilityId);
                } else {
                    callback.onFacilityNotExists();
                }
            } else {
                callback.onError("Failed to check user's facility.");
            }
        });
    }

    // Method to retrieve facility details for an organizer
    public void getFacilityDetails(String facilityId, FacilityFetchCallback callback) {
        facilitiesCollection.document(facilityId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Facility facility = task.getResult().toObject(Facility.class);
                callback.onFacilityFetched(facility);
            } else {
                callback.onError("Failed to fetch facility details.");
            }
        });
    }

    // Callbacks for asynchronous operations
    public interface FacilityCreationCallback {
        void onFacilityCreated(String facilityId);
        void onError(String message);
    }

    public interface FacilityFetchCallback {
        void onFacilityFetched(Facility facility);
        void onError(String message);
    }

    public interface FacilityCheckCallback {
        void onFacilityExists(String facilityId);
        void onFacilityNotExists();
        void onError(String message);
    }
}

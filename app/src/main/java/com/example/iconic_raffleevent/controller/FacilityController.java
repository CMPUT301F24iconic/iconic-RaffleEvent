package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;

public class FacilityController {

    private FirebaseOrganizer firebaseOrganizer;

    public FacilityController() {
        this.firebaseOrganizer = new FirebaseOrganizer();
    }

    // Create a new facility and link it to the creator's user profile
    public void createFacility(Facility facility, FacilityCreationCallback callback) {
        firebaseOrganizer.createFacility(facility, new FirebaseOrganizer.FacilityCreationCallback() {
            @Override
            public void onFacilityCreated(String facilityId) {
                callback.onFacilityCreated(facilityId);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // Check if the user already has a facility
    public void checkUserFacility(String userId, FacilityCheckCallback callback) {
        firebaseOrganizer.checkUserFacility(userId, new FirebaseOrganizer.FacilityCheckCallback() {
            @Override
            public void onFacilityExists(String facilityId) {
                callback.onFacilityExists(facilityId);
            }

            @Override
            public void onFacilityNotExists() {
                callback.onFacilityNotExists();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // Retrieve facility details based on facility ID
    public void getFacilityDetails(String facilityId, FacilityFetchCallback callback) {
        firebaseOrganizer.getFacilityDetails(facilityId, new FirebaseOrganizer.FacilityFetchCallback() {
            @Override
            public void onFacilityFetched(Facility facility) {
                callback.onFacilityFetched(facility);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // Callbacks for facility operations
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

package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

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

    // Retrieve all facilities
    public void getAllFacilities(FacilityListCallback callback) {
        firebaseOrganizer.getAllFacilities(new FirebaseOrganizer.GetFacilitiesCallback() {
            @Override
            public void onFacilitiesFetched(ArrayList<Facility> facilities) {
                callback.onFacilitiesFetched(facilities);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // Delete facility by facility ID
    public void deleteFacility(String facilityId, DeleteFacilityCallback callback) {
        firebaseOrganizer.deleteFacility(facilityId, new FirebaseOrganizer.DeleteFacilityCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    // Callback interfaces for facility operations
    public interface FacilityListCallback {
        void onFacilitiesFetched(ArrayList<Facility> facilities);
        void onError(String message);
    }

    public interface DeleteFacilityCallback {
        void onSuccess();
        void onError(String message);
    }

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

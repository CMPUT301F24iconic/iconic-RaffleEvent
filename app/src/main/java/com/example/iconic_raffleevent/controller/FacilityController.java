package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

/**
 * FacilityController handles the business logic related to facilities.
 * It facilitates operations like creating, fetching, checking, and deleting facilities.
 */
public class FacilityController {

    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Constructs a new FacilityController and initializes FirebaseOrganizer.
     */
    public FacilityController() {
        this.firebaseOrganizer = new FirebaseOrganizer();
    }

    /**
     * Creates a new facility and links it to the creator's user profile.
     *
     * @param facility The facility to be created.
     * @param callback The callback to handle the result of the facility creation.
     */
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

    /**
     * Checks if the user already has a facility linked to their profile.
     *
     * @param userId The ID of the user to check.
     * @param callback The callback to handle the result of the check.
     */
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

    /**
     * Retrieves all available facilities.
     *
     * @param callback The callback to handle the list of fetched facilities.
     */
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

    /**
     * Deletes a facility by its ID.
     *
     * @param facilityId The ID of the facility to be deleted.
     * @param callback The callback to handle the result of the deletion.
     */
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

    /**
     * Callback interface for retrieving a list of facilities.
     */
    public interface FacilityListCallback {
        void onFacilitiesFetched(ArrayList<Facility> facilities);
        void onError(String message);
    }

    /**
     * Callback interface for deleting a facility.
     */
    public interface DeleteFacilityCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Callback interface for facility creation.
     */
    public interface FacilityCreationCallback {
        void onFacilityCreated(String facilityId);
        void onError(String message);
    }

    /**
     * Callback interface for fetching a specific facility.
     */
    public interface FacilityFetchCallback {
        void onFacilityFetched(Facility facility);
        void onError(String message);
    }

    /**
     * Callback interface for checking if a facility exists for a user.
     */
    public interface FacilityCheckCallback {
        void onFacilityExists(String facilityId);
        void onFacilityNotExists();
        void onError(String message);
    }
}


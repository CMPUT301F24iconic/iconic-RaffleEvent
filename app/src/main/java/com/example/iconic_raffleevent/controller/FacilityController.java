package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

/**
 * FacilityController handles the business logic related to facilities.
 * It facilitates operations like creating, fetching, checking, and deleting facilities.
 */
public class FacilityController {

    public FirebaseOrganizer firebaseOrganizer;

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
        firebaseOrganizer.checkUserFacility(facility.getCreator().getUserId(), new FirebaseOrganizer.FacilityCheckCallback() {
            @Override
            public void onFacilityExists(String facilityId) {
                // Update the existing facility
                firebaseOrganizer.updateFacility(facilityId, facility, new FirebaseOrganizer.FacilityUpdateCallback() {
                    @Override
                    public void onFacilityUpdated() {
                        // Update user's facilityId after updating the facility
                        firebaseOrganizer.updateUserFacilityId(facility.getCreator().getUserId(), facilityId, new FirebaseOrganizer.UserUpdateCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onFacilityCreated(facilityId);
                            }

                            @Override
                            public void onError(String message) {
                                callback.onError("Failed to update user: " + message);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError("Failed to update facility: " + message);
                    }
                });
            }

            @Override
            public void onFacilityNotExists() {
                // Ensure creator field is updated properly before creation
                facility.getCreator().setFacilityId(facility.getId());

                // Create a new facility
                firebaseOrganizer.createFacility(facility, new FirebaseOrganizer.FacilityCreationCallback() {
                    @Override
                    public void onFacilityCreated(String facilityId) {
                        // Update user's facilityId after creating the facility
                        firebaseOrganizer.updateUserFacilityId(facility.getCreator().getUserId(), facilityId, new FirebaseOrganizer.UserUpdateCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onFacilityCreated(facilityId);
                            }

                            @Override
                            public void onError(String message) {
                                callback.onError("Facility created, but failed to update user: " + message);
                            }
                        });
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                callback.onError("Error checking user facility: " + message);
            }
        });
    }

    /**
     * Updates an existing facility in the database.
     *
     * @param facility The facility object with updated details.
     * @param callback The callback to handle the result of the update operation.
     */
    public void updateFacility(Facility facility, FacilityUpdateCallback callback) {
        firebaseOrganizer.updateFacility(facility.getId(), facility, new FirebaseOrganizer.FacilityUpdateCallback() {
            @Override
            public void onFacilityUpdated() {
                callback.onFacilityUpdated();
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
     * Retrieves the facility details linked to the user ID.
     *
     * @param userId The ID of the user whose facility needs to be fetched.
     * @param callback The callback to handle the fetched facility or errors.
     */
    public void getFacilityByUserId(String userId, FacilityFetchCallback callback) {
        firebaseOrganizer.getFacilityByUserId(userId, new FirebaseOrganizer.FacilityFetchCallback() {
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

    /**
     * Callback interface for retrieving a list of facilities.
     */
    public interface FacilityListCallback {
        /**
         * Callback which contains a list of Facility objects
         * @param facilities List of facilities
         */
        void onFacilitiesFetched(ArrayList<Facility> facilities);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting a facility.
     */
    public interface DeleteFacilityCallback {
        /**
         * Callback which is called upon successful delete facility operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for facility creation.
     */
    public interface FacilityCreationCallback {
        /**
         * Callback which contains the id of a created facility
         * @param facilityId String representing facilityId
         */
        void onFacilityCreated(String facilityId);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching a specific facility.
     */
    public interface FacilityFetchCallback {
        /**
         * Callback which contains a Facility object upon successful fetch facility operation
         * @param facility
         */
        void onFacilityFetched(Facility facility);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for facility updates.
     */
    public interface FacilityUpdateCallback {
        /**
         * Callback which is called after a successful update facility operation
         */
        void onFacilityUpdated();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for checking if a facility exists for a user.
     */
    public interface FacilityCheckCallback {
        /**
         * Callback which contains the id of a facility upon successful facility exists operation
         * @param facilityId
         */
        void onFacilityExists(String facilityId);

        /**
         * Callback which is called when a facility does not exist
         */
        void onFacilityNotExists();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }
}


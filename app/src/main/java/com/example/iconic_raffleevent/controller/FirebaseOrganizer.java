package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.ImageData;
import com.example.iconic_raffleevent.model.QRCodeData;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * FirebaseOrganizer is a controller class responsible for handling operations related to the firebase database,
 * such as creating, deleting, and fetching data related to users, facilities, events, images, and QR codes.
 */
public class FirebaseOrganizer {
    private FirebaseFirestore db;

    /**
     * Initializes the FirebaseFirestore instance for database operations.
     */
    public FirebaseOrganizer() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Creates a new facility in the Firestore database.
     *
     * @param facility The Facility object containing the details of the facility to be created.
     * @param callback The callback interface for handling success or error responses.
     */
    public void createFacility(Facility facility, FacilityCreationCallback callback) {
        // Ensure the creator is not null before saving
        if (facility.getCreator() == null || facility.getCreator().getUserId() == null) {
            callback.onError("Facility must be linked to a valid creator.");
            return;
        }

        String facilityId = db.collection("Facility").document().getId(); // Pre-generate ID
        facility.setId(facilityId); // Set the ID in the facility object

        if (facility.getCreator() != null) {
            facility.getCreator().setFacilityId(facilityId); // Update facilityId in the creator object
        }

        db.collection("Facility").document(facilityId).set(facility)
                .addOnSuccessListener(aVoid -> callback.onFacilityCreated(facilityId))
                .addOnFailureListener(e -> callback.onError("Failed to create facility: " + e.getMessage()));
    }

    /**
     * Updates an existing facility in the Firestore database.
     *
     * @param facilityId The ID of the facility to update.
     * @param facility   The Facility object containing the updated details.
     * @param callback   The callback interface for handling success or error responses.
     */
    public void updateFacility(String facilityId, Facility facility, FacilityUpdateCallback callback) {
        if (facility == null || facilityId == null || facilityId.isEmpty()) {
            callback.onError("Invalid facility data for update.");
            return;
        }

        db.collection("Facility").document(facilityId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        db.collection("Facility").document(facilityId).set(facility)
                                .addOnSuccessListener(aVoid -> {
                                    db.collection("Event").whereEqualTo("facilityId", facilityId)
                                            .get().addOnSuccessListener(querySnapshot -> {
                                                if (!querySnapshot.isEmpty()) {
                                                    for (DocumentSnapshot event : querySnapshot.getDocuments()) {
                                                        event.getReference().update("eventLocation", facility.getFacilityName())
                                                                .addOnFailureListener(e ->
                                                                        callback.onError("Failed to update event with new facility ID"));
                                                    }
                                                    callback.onFacilityUpdated();
                                                } else {
                                                    callback.onFacilityUpdated();
                                                }
                                            });
                                })
                                .addOnFailureListener(e -> callback.onError("Failed to update facility: " + e.getMessage()));
                    } else {
                        callback.onError("Facility does not exist for update.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error validating facility existence: " + e.getMessage()));
    }

    /**
     * Fetches the facility associated with the given user ID.
     *
     * @param userId  The ID of the user whose facility is to be fetched.
     * @param callback The callback to handle the fetched facility or errors.
     */
    public void getFacilityByUserId(String userId, FacilityFetchCallback callback) {
        db.collection("Facility")
                .whereEqualTo("creator.userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        if (queryDocumentSnapshots.size() > 1) {
                            // Log a warning for unexpected multiple facilities
                            System.out.println("Warning: Multiple facilities found for user ID: " + userId);
                        }
                        Facility facility = queryDocumentSnapshots.getDocuments().get(0).toObject(Facility.class);
                        facility.setId(queryDocumentSnapshots.getDocuments().get(0).getId());
                        callback.onFacilityFetched(facility);
                    } else {
                        callback.onError("No facility found for this user.");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error fetching facility: " + e.getMessage()));
    }

    /**
     * Checks if a user already has a facility.
     *
     * @param userId  The user ID to be checked.
     * @param callback The callback interface for handling success or error responses.
     */
    public void checkUserFacility(String userId, FacilityCheckCallback callback) {
        db.collection("Facility")
                .whereEqualTo("creator.userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Retrieve the facility object and its ID
                        String facilityId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        callback.onFacilityExists(facilityId);
                    } else {
                        callback.onFacilityNotExists();
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error checking facility: " + e.getMessage()));
    }

    /**
     * Retrieves all users from the Firestore database.
     *
     * @param callback The callback interface for handling fetched users or errors.
     */
    public void getAllUsers(GetUsersCallback callback) {
        db.collection("User").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<User> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        user.setUserId(document.getId());
                        users.add(user);
                    }
                    callback.onUsersFetched(users);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching users: " + e.getMessage()));
    }

    /**
     * Retrieves all QR codes from the Firestore database.
     *
     * @param callback The callback interface for handling fetched QR codes or errors.
     */
    public void getAllQRCodes(GetQRCodesCallback callback) {
        db.collection("qrcodes").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<QRCodeData> qrCodes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        QRCodeData qrCode = document.toObject(QRCodeData.class);
                        qrCode.setQrCodeId(document.getId());
                        qrCodes.add(qrCode);
                    }
                    callback.onQRCodesFetched(qrCodes);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching QR codes: " + e.getMessage()));
    }

    /**
     * Retrieves all facilities from the Firestore database.
     *
     * @param callback The callback interface for handling fetched facilities or errors.
     */
    public void getAllFacilities(GetFacilitiesCallback callback) {
        db.collection("Facility").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Facility> facilities = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Facility facility = document.toObject(Facility.class);
                        facility.setId(document.getId());
                        facilities.add(facility);
                    }
                    callback.onFacilitiesFetched(facilities);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching facilities: " + e.getMessage()));
    }

    /**
     * Deletes a facility from Firestore along with all associated events,
     * their media (posters and QR codes) stored in Firebase Storage, and
     * removes references from user documents. Updates the creator's facilityId
     * in the User collection.
     *
     * @param facilityId The ID of the facility to be deleted.
     * @param callback   The callback interface for handling success or error responses.
     */
    public void deleteFacility(String facilityId, DeleteFacilityCallback callback) {
        // Fetch the facility to get the creator's user ID
        DocumentReference facilityRef = db.collection("Facility").document(facilityId);

        facilityRef.get()
                .addOnSuccessListener(facilitySnapshot -> {
                    if (facilitySnapshot.exists()) {
                        Facility facility = facilitySnapshot.toObject(Facility.class);
                        String creatorUserId = facility.getCreator().getUserId();

                        // Step 1: Fetch all events associated with the facility
                        db.collection("Event").whereEqualTo("facilityId", facilityId)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    List<Task<Void>> deletionTasks = new ArrayList<>();

                                    // Delete each event and associated data
                                    for (DocumentSnapshot eventDoc : querySnapshot.getDocuments()) {
                                        String eventId = eventDoc.getId();
                                        Event event = eventDoc.toObject(Event.class);

                                        // Delete the event document
                                        Task<Void> deleteEventTask = db.collection("Event").document(eventId).delete();
                                        deletionTasks.add(deleteEventTask);

                                        // Delete associated poster from Firebase Storage
                                        String posterPath = "event_posters/" + eventId;
                                        StorageReference posterRef = FirebaseStorage.getInstance().getReference().child(posterPath);
                                        Task<Void> deletePosterTask = posterRef.delete().addOnFailureListener(e -> {
                                            // Handle file not found gracefully
                                            if (!(e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND)) {
                                                // Log the error
                                                System.err.println("Error deleting poster: " + e.getMessage());
                                            }
                                        });
                                        deletionTasks.add(deletePosterTask);

                                        // Delete associated QR code from Firebase Storage
                                        if (event.getQrCode() != null && !event.getQrCode().isEmpty()) {
                                            String qrCodePath = "event_qrcodes/" + event.getQrCode();
                                            StorageReference qrCodeRef = FirebaseStorage.getInstance().getReference().child(qrCodePath);
                                            Task<Void> deleteQrCodeTask = qrCodeRef.delete().addOnFailureListener(e -> {
                                                // Handle file not found gracefully
                                                if (!(e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND)) {
                                                    // Log the error
                                                    System.err.println("Error deleting QR code: " + e.getMessage());
                                                }
                                            });
                                            deletionTasks.add(deleteQrCodeTask);
                                        }

                                        // Remove event references from users
                                        Task<Void> removeUserReferencesTask = removeEventReferencesFromUsers(eventId, event);
                                        deletionTasks.add(removeUserReferencesTask);
                                    }

                                    // Delete the facility document
                                    Task<Void> deleteFacilityTask = facilityRef.delete();
                                    deletionTasks.add(deleteFacilityTask);

                                    // Update creator's facilityId
                                    Task<Void> updateUserTask = db.collection("User").document(creatorUserId)
                                            .update("facilityId", FieldValue.delete());
                                    deletionTasks.add(updateUserTask);

                                    // Wait for all tasks to complete successfully
                                    Tasks.whenAllSuccess(deletionTasks)
                                            .addOnSuccessListener(tasks -> {
                                                callback.onSuccess();
                                            })
                                            .addOnFailureListener(e -> {
                                                callback.onError("Failed to delete facility and associated data: " + e.getMessage());
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    callback.onError("Failed to fetch events for facility deletion: " + e.getMessage());
                                });
                    } else {
                        callback.onError("Facility not found.");
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to fetch facility: " + e.getMessage());
                });
    }

    /**
     * NOT BEING USED SINCE WE ARE NOT UPDATING THE LIST FIELDS IN A USER OBJECT
     * Removes references to an event from all relevant user documents in Firestore.
     *
     * @param eventId The ID of the event to remove references for.
     * @param event   The Event object containing lists of associated user IDs.
     * @return A Task<Void> that completes when all user references are removed.
     */
    private Task<Void> removeEventReferencesFromUsers(String eventId, Event event) {
        Set<String> userIds = new HashSet<>();

        if (event.getWaitingList() != null) {
            userIds.addAll(event.getWaitingList());
        }
        if (event.getRegisteredAttendees() != null) {
            userIds.addAll(event.getRegisteredAttendees());
        }
        if (event.getInvitedList() != null) {
            userIds.addAll(event.getInvitedList());
        }

        List<Task<Void>> userUpdateTasks = new ArrayList<>();

//        for (String userId : userIds) {
//            DocumentReference userRef = db.collection("User").document(userId);
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("waitingListEventIds", FieldValue.arrayRemove(eventId));
//            updates.put("registeredEventIds", FieldValue.arrayRemove(eventId));
//            updates.put("invitedEventIds", FieldValue.arrayRemove(eventId));
//            Task<Void> updateTask = userRef.update(updates)
//                    .addOnFailureListener(e -> {
//                        // Log the error but continue with other updates
//                        System.err.println("Error updating user " + userId + ": " + e.getMessage());
//                    });
//            userUpdateTasks.add(updateTask);
//        }

        return Tasks.forResult(null);
    }


    /**
     * Updates the facility ID linked to a user
     * @param userId ID of the related user
     * @param facilityId ID of the related facility
     * @param callback The callback interface for handling success or error responses.
     */
    public void updateUserFacilityId(String userId, String facilityId, UserUpdateCallback callback) {
        db.collection("User").document(userId).update("facilityId", facilityId)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update facilityId in user: " + e.getMessage()));
    }

    /**
     * Retrieves all events from the Firestore database.
     *
     * @param callback The callback interface for handling fetched events or errors.
     */
    public void getAllEvents(GetEventsCallback callback) {
        db.collection("Event").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Event> events = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Event event = document.toObject(Event.class);
                        event.setEventId(document.getId());
                        events.add(event);
                    }
                    callback.onEventsFetched(events);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching events: " + e.getMessage()));
    }

    /**
     * Deletes a user by its ID from the Firestore database.
     *
     * @param userId The ID of the user to be deleted.
     * @param callback The callback interface for handling success or error responses.
     */
    public void deleteUser(String userId, DeleteUserCallback callback) {
        db.collection("User").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete user: " + e.getMessage()));
    }

    /**
     * Deletes an event by its ID from the Firestore database.
     *
     * @param eventId The ID of the event to be deleted.
     * @param callback The callback interface for handling success or error responses.
     */
    public void deleteEvent(String eventId, DeleteEventCallback callback) {
        db.collection("Event").document(eventId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete event: " + e.getMessage()));
    }

    /**
     * Retrieves all images from the Firestore database.
     *
     * @param callback The callback interface for handling fetched images or errors.
     */
    public void getAllImages(GetImagesCallback callback) {
        db.collection("images").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<ImageData> images = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ImageData imageData = document.toObject(ImageData.class);
                        imageData.setImageId(document.getId());
                        images.add(imageData);
                    }
                    callback.onImagesFetched(images);
                })
                .addOnFailureListener(e -> callback.onError("Error fetching images: " + e.getMessage()));
    }

    /**
     * Deletes an image by its ID from the Firestore database.
     *
     * @param imageId The ID of the image to be deleted.
     * @param callback The callback interface for handling success or error responses.
     */
    public void deleteImage(String imageId, DeleteImageCallback callback) {
        db.collection("images").document(imageId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete image: " + e.getMessage()));
    }

    /**
     * Deletes a QR code by its ID from the Firestore database.
     *
     * @param qrCodeId The ID of the QR code to be deleted.
     * @param callback The callback interface for handling success or error responses.
     */
    public void deleteQRCode(String qrCodeId, DeleteQRCodeCallback callback) {
        db.collection("qrcodes").document(qrCodeId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete QR code: " + e.getMessage()));
    }

    /**
     * Callback interface for handling the result of facility creation.
     */
    public interface FacilityCreationCallback {
        /**
         * Callback which is called upon successful creation of a facility.
         * @param facilityId The ID of the created facility.
         */
        void onFacilityCreated(String facilityId);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for handling the result of updating a facility.
     */
    public interface FacilityUpdateCallback {
        /**
         * Callback which is called upon successful update of a facility.
         */
        void onFacilityUpdated();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for checking the existence of a facility.
     */
    public interface FacilityCheckCallback {
        /**
         * Callback which is called when a facility exists.
         * @param facilityId The ID of the existing facility.
         */
        void onFacilityExists(String facilityId);

        /**
         * Callback which is called when a facility does not exist.
         */
        void onFacilityNotExists();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching a facility.
     */
    public interface FacilityFetchCallback {
        /**
         * Callback which contains a Facility object.
         * @param facility The fetched facility.
         */
        void onFacilityFetched(Facility facility);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching multiple facilities.
     */
    public interface GetFacilitiesCallback {
        /**
         * Callback which contains a list of Facility objects.
         * @param facilities The list of fetched facilities.
         */
        void onFacilitiesFetched(ArrayList<Facility> facilities);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting a facility.
     */
    public interface DeleteFacilityCallback {
        /**
         * Callback which is called upon successful deletion of a facility.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for updating user details.
     */
    public interface UserUpdateCallback {
        /**
         * Callback which is called upon successful user update.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching events.
     */
    public interface GetEventsCallback {
        /**
         * Callback which contains a list of Event objects.
         * @param events The list of fetched events.
         */
        void onEventsFetched(ArrayList<Event> events);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting an event.
     */
    public interface DeleteEventCallback {
        /**
         * Callback which is called upon successful deletion of an event.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching images.
     */
    public interface GetImagesCallback {
        /**
         * Callback which contains a list of ImageData objects.
         * @param images The list of fetched images.
         */
        void onImagesFetched(ArrayList<ImageData> images);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting an image.
     */
    public interface DeleteImageCallback {
        /**
         * Callback which is called upon successful deletion of an image.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching QR codes.
     */
    public interface GetQRCodesCallback {
        /**
         * Callback which contains a list of QRCodeData objects.
         * @param qrCodes The list of fetched QR codes.
         */
        void onQRCodesFetched(ArrayList<QRCodeData> qrCodes);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting a QR code.
     */
    public interface DeleteQRCodeCallback {
        /**
         * Callback which is called upon successful deletion of a QR code.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching users.
     */
    public interface GetUsersCallback {
        /**
         * Callback which contains a list of User objects.
         * @param users The list of fetched users.
         */
        void onUsersFetched(ArrayList<User> users);

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting a user.
     */
    public interface DeleteUserCallback {
        /**
         * Callback which is called upon successful deletion of a user.
         */
        void onSuccess();

        /**
         * Callback which contains an error message.
         * @param message Description of the error.
         */
        void onError(String message);
    }

}

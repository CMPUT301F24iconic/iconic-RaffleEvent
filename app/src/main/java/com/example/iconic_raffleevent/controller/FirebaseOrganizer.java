package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.ImageData;
import com.example.iconic_raffleevent.model.QRCodeData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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
     * Deletes a facility by its ID from the Firestore database.
     *
     * @param facilityId The ID of the facility to be deleted.
     * @param callback The callback interface for handling success or error responses.
     */
    public void deleteFacility(String facilityId, DeleteFacilityCallback callback) {
        db.collection("Facility").document(facilityId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete facility: " + e.getMessage()));
    }

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

    // Callback interfaces for facility operations
    public interface FacilityCreationCallback {
        void onFacilityCreated(String facilityId);
        void onError(String message);
    }

    public interface FacilityUpdateCallback {
        void onFacilityUpdated();
        void onError(String message);
    }

    public interface FacilityCheckCallback {
        void onFacilityExists(String facilityId);
        void onFacilityNotExists();
        void onError(String message);
    }

    public interface FacilityFetchCallback {
        void onFacilityFetched(Facility facility);
        void onError(String message);
    }

    public interface GetFacilitiesCallback {
        void onFacilitiesFetched(ArrayList<Facility> facilities);
        void onError(String message);
    }

    public interface DeleteFacilityCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface UserUpdateCallback {
        void onSuccess();
        void onError(String message);
    }

    // Callback interfaces for event operations
    public interface GetEventsCallback {
        void onEventsFetched(ArrayList<Event> events);
        void onError(String message);
    }

    public interface DeleteEventCallback {
        void onSuccess();
        void onError(String message);
    }

    // Callback interfaces for image operations
    public interface GetImagesCallback {
        void onImagesFetched(ArrayList<ImageData> images);
        void onError(String message);
    }

    public interface DeleteImageCallback {
        void onSuccess();
        void onError(String message);
    }

    // Callback interfaces for QR code operations
    public interface GetQRCodesCallback {
        void onQRCodesFetched(ArrayList<QRCodeData> qrCodes);
        void onError(String message);
    }

    public interface DeleteQRCodeCallback {
        void onSuccess();
        void onError(String message);
    }

    // Callback interfaces for user operations
    public interface GetUsersCallback {
        void onUsersFetched(ArrayList<User> users);
        void onError(String message);
    }

    public interface DeleteUserCallback {
        void onSuccess();
        void onError(String message);
    }
}

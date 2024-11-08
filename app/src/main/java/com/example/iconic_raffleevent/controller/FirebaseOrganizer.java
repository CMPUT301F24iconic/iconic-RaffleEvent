package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.ImageData;
import com.example.iconic_raffleevent.model.QRCodeData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FirebaseOrganizer {

    private FirebaseFirestore db;

    public FirebaseOrganizer() {
        db = FirebaseFirestore.getInstance();
    }

    // Method to create a new facility
    public void createFacility(Facility facility, FacilityCreationCallback callback) {
        db.collection("facilities").add(facility)
                .addOnSuccessListener(documentReference -> callback.onFacilityCreated(documentReference.getId()))
                .addOnFailureListener(e -> callback.onError("Failed to create facility: " + e.getMessage()));
    }

    // Method to check if a user already has a facility
    public void checkUserFacility(String userId, FacilityCheckCallback callback) {
        db.collection("facilities")
                .whereEqualTo("creator.userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String facilityId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        callback.onFacilityExists(facilityId);
                    } else {
                        callback.onFacilityNotExists();
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error checking facility: " + e.getMessage()));
    }

    public void getAllUsers(GetUsersCallback callback) {
        db.collection("users").get()
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
    // Method to retrieve all QR codes
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

    // Method to retrieve all facilities
    public void getAllFacilities(GetFacilitiesCallback callback) {
        db.collection("facilities").get()
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

    // Method to delete a facility by ID
    public void deleteFacility(String facilityId, DeleteFacilityCallback callback) {
        db.collection("facilities").document(facilityId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete facility: " + e.getMessage()));
    }

    // Method to retrieve all events
    public void getAllEvents(GetEventsCallback callback) {
        db.collection("events").get()
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

    // Method to delete a user by ID
    public void deleteUser(String userId, DeleteUserCallback callback) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete user: " + e.getMessage()));
    }

    // Method to delete an event by ID
    public void deleteEvent(String eventId, DeleteEventCallback callback) {
        db.collection("events").document(eventId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete event: " + e.getMessage()));
    }

    // Method to retrieve all images
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

    // Method to delete an image by ID
    public void deleteImage(String imageId, DeleteImageCallback callback) {
        db.collection("images").document(imageId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete image: " + e.getMessage()));
    }

    // Method to delete a QR code by ID
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

    public interface FacilityCheckCallback {
        void onFacilityExists(String facilityId);
        void onFacilityNotExists();
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


    // Callback interfaces for user operations
    public interface GetUsersCallback {
        void onUsersFetched(ArrayList<User> users);
        void onError(String message);
    }

    public interface DeleteUserCallback {
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
}

package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.ImageData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ImageController {

    private FirebaseFirestore db;

    public ImageController() {
        db = FirebaseFirestore.getInstance();
    }

    // Method to retrieve all images from the Firestore database
    public void getAllImages(ImageListCallback callback) {
        db.collection("images").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<ImageData> images = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ImageData image = document.toObject(ImageData.class);
                        image.setImageId(document.getId()); // Set the image ID based on the document ID
                        images.add(image); // Add image data to the list
                    }
                    callback.onImagesFetched(images); // Callback with fetched images
                })
                .addOnFailureListener(e -> callback.onError("Error fetching images: " + e.getMessage())); // Callback with error message
    }

    // Method to delete a specific image by its ID
    public void deleteImage(String imageId, DeleteImageCallback callback) {
        db.collection("images").document(imageId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess()) // Callback on successful deletion
                .addOnFailureListener(e -> callback.onError("Failed to delete image: " + e.getMessage())); // Callback with error message
    }

    // Callback interface for fetching images
    public interface ImageListCallback {
        void onImagesFetched(ArrayList<ImageData> images); // Callback method for successful image retrieval
        void onError(String message); // Callback method for error handling
    }

    // Callback interface for deleting images
    public interface DeleteImageCallback {
        void onSuccess(); // Callback method for successful deletion
        void onError(String message); // Callback method for error handling
    }
}

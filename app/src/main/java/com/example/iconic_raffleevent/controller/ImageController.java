package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.ImageData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Controller class responsible for managing image data stored in Firestore.
 * Provides methods to retrieve all images and delete a specific image by its ID.
 */
public class ImageController {

    private FirebaseFirestore db;

    /**
     * Constructs a new ImageController and initializes the Firebase Firestore instance.
     * This instance is used to interact with the Firestore database.
     */
    public ImageController() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves all images from the Firestore database.
     * This method fetches all the image documents stored in the "images" collection,
     * converts them into ImageData objects, and passes them to the callback.
     *
     * @param callback The callback interface to handle the retrieved images or error.
     */
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

    /**
     * Deletes a specific image by its ID from the Firestore database.
     *
     * @param imageId  The ID of the image to be deleted.
     * @param callback The callback interface to handle the success or failure of the deletion.
     */
    public void deleteImage(String imageId, DeleteImageCallback callback) {
        db.collection("images").document(imageId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess()) // Callback on successful deletion
                .addOnFailureListener(e -> callback.onError("Failed to delete image: " + e.getMessage())); // Callback with error message
    }

    /**
     * Callback interface for retrieving a list of images.
     */
    public interface ImageListCallback {
        void onImagesFetched(ArrayList<ImageData> images);
        void onError(String message);
    }

    /**
     * Callback interface for deleting an image.
     */
    public interface DeleteImageCallback {
        void onSuccess();
        void onError(String message);
    }
}

package com.example.iconic_raffleevent.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Controller class responsible for managing QR code data and interacting with Firebase Firestore.
 * Provides methods to fetch and delete QR code data.
 */
public class QRCodeController {

    public FirebaseFirestore db;

    /**
     * Constructs a new QRCodeController and initializes the Firebase Firestore instance.
     */
    public QRCodeController() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Retrieves all QR code data from the Firestore collection.
     *
     * @param callback The callback interface to handle the fetched QR code data or error.
     */
    public void getAllQRCodeData(GetQRCodeDataCallback callback) {
        db.collection("qr_codes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ArrayList<String> qrCodes = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String qrData = document.getString("data");
                    if (qrData != null) {
                        qrCodes.add(qrData);
                    }
                }
                callback.onQRCodeDataFetched(qrCodes);
            } else {
                callback.onError("Failed to fetch QR code data: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    /**
     * Deletes QR code data from Firestore based on the provided QR code data.
     *
     * @param qrCodeData The QR code data to be deleted.
     * @param callback   The callback interface to handle success or error responses.
     */
    public void deleteQRCodeData(String qrCodeData, DeleteQRCodeDataCallback callback) {
        db.collection("qr_codes").whereEqualTo("data", qrCodeData).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        ArrayList<String> deleteFailures = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            db.collection("qr_codes").document(doc.getId()).delete()
                                    .addOnFailureListener(e -> deleteFailures.add(doc.getId()));
                        }
                        if (deleteFailures.isEmpty()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Failed to delete some QR codes: " + deleteFailures.toString());
                        }
                    } else {
                        callback.onError("QR code data not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error deleting QR code data: " + e.getMessage()));
    }

    /**
     * Callback interface for fetching QR code data.
     */
    public interface GetQRCodeDataCallback {
        void onQRCodeDataFetched(ArrayList<String> qrCodes);
        void onError(String message);
    }

    /**
     * Callback interface for deleting QR code data.
     */
    public interface DeleteQRCodeDataCallback {
        void onSuccess();
        void onError(String message);
    }
}

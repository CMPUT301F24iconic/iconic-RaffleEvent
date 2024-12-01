package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.QRCodeData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * Controller class to manage QR code data from the Event collection.
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
     * Retrieves all QR code data from the Event collection in Firestore.
     *
     * @param callback The callback interface to handle the fetched QR code data or error.
     */
    public void getAllQRCodeData(GetQRCodeDataCallback callback) {
        db.collection("Event").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ArrayList<QRCodeData> qrCodeDataList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Fetch the QR code URL and name
                    String qrCodeUrl = document.getString("eventQrUrl");
                    String qrCodeName = document.getString("eventTitle");
                    String eventId = document.getId();

                    if (qrCodeUrl != null && qrCodeName != null) {
                        qrCodeDataList.add(new QRCodeData(eventId, qrCodeName, qrCodeUrl));
                    } else {
                        System.out.println("Missing QR Code data for event: " + eventId);
                    }
                }

                if (qrCodeDataList.isEmpty()) {
                    callback.onError("No QR codes found.");
                } else {
                    callback.onQRCodeDataFetched(qrCodeDataList);
                }
            } else {
                callback.onError("Failed to fetch QR code data: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    /**
     * Deletes the QR code data from the specified event in Firestore.
     *
     * @param eventId  The ID of the event to update.
     * @param callback The callback interface to handle success or error responses.
     */
    public void deleteQRCodeFromEvent(String eventId, FirestoreUpdateCallback callback) {
        db.collection("Event").document(eventId)
                .update("eventQrUrl", null, "qrCode", null) // Clear the QR code fields
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onError("Failed to delete QR code: " + e.getMessage());
                });
    }

    /**
     * Callback interface for Firestore updates.
     */
    public interface FirestoreUpdateCallback {
        void onSuccess();

        void onError(String message);
    }

    /**
     * Callback interface for fetching QR code data.
     */
    public interface GetQRCodeDataCallback {
        void onQRCodeDataFetched(ArrayList<QRCodeData> qrCodes);
        void onError(String message);
    }
}

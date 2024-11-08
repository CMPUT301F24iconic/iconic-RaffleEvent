package com.example.iconic_raffleevent.controller;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class QRCodeController {

    private final FirebaseFirestore db;

    public QRCodeController() {
        this.db = FirebaseFirestore.getInstance();
    }

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


    // Callback interface for fetching QR codes
    public interface GetQRCodeDataCallback {
        void onQRCodeDataFetched(ArrayList<String> qrCodes);
        void onError(String message);
    }

    public interface DeleteQRCodeDataCallback {
        void onSuccess();
        void onError(String message);
    }
}

package com.example.iconic_raffleevent.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.core.app.ActivityCompat;

import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class UserController {
    private static final String TAG = "UserController";
    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;
    private final String currentUserID;
    private final Context context;

    public UserController(String userID, Context context) {
        this.currentUserID = userID;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
    }

    // Regex pattern for validating phone numbers (US/international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    public void addUser(User user, AddUserCallback callback) {
        firestore.collection("User").document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to add user: " + e.getMessage()));
    }

    // Method to validate and update the user profile with a valid phone number
    public void updateProfile(User user, String name, String email, String phoneNo) {
        if (!isValidPhoneNumber(phoneNo)) {
            Log.e(TAG, "Invalid phone number format");
            return;
        }

        user.setName(name);
        user.setEmail(email);
        user.setPhoneNo(phoneNo);
        updateUser(user); // Call the simplified version without callback
    }

    // Helper method to validate phone number format
    private boolean isValidPhoneNumber(String phoneNo) {
        return phoneNo != null && PHONE_PATTERN.matcher(phoneNo).matches();
    }

    public void uploadProfileImage(User user, Uri imageUri, ProfileImageUploadCallback callback) {
        if (imageUri == null) {
            callback.onError("Image URI is null");
            return;
        }

        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            callback.onError("Invalid user data");
            return;
        }

        try {
            Log.d(TAG, "Starting image upload for user: " + user.getUserId());
            String filename = "profile_" + System.currentTimeMillis() + ".jpg";
            String storagePath = String.format("profile_images/%s/%s", user.getUserId(), filename);
            StorageReference imageRef = storageReference.child(storagePath);

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            UploadTask uploadTask = imageRef.putFile(imageUri, metadata);
            uploadTask.addOnSuccessListener(taskSnapshot ->
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                Log.d(TAG, "Upload successful. URL: " + downloadUrl);

                                user.setProfileImageUrl(downloadUrl);
                                updateUser(user, new UpdateUserCallback() {
                                    @Override
                                    public void onSuccess() {
                                        callback.onProfileImageUploaded(downloadUrl);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        callback.onError("Failed to update user profile with image URL: " + message);
                                    }
                                });
                            }).addOnFailureListener(e -> callback.onError("Failed to get download URL: " + e.getMessage())))
                    .addOnFailureListener(e -> callback.onError("Upload failed: " + e.getMessage()));
        } catch (Exception e) {
            callback.onError("Error during upload: " + e.getMessage());
        }
    }

    public void removeProfileImage(User user, ProfileImageRemovalCallback callback) {
        String imageUrl = user.getProfileImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference photoRef = firebaseStorage.getReferenceFromUrl(imageUrl);
            photoRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        user.setProfileImageUrl(null);
                        updateUser(user, new UpdateUserCallback() {
                            @Override
                            public void onSuccess() {
                                callback.onProfileImageRemoved();
                            }

                            @Override
                            public void onError(String message) {
                                callback.onError("Failed to update user profile after image removal: " + message);
                            }
                        });
                    })
                    .addOnFailureListener(e -> callback.onError("Failed to remove image: " + e.getMessage()));
        } else {
            callback.onError("No profile image to remove");
        }
    }

    public void setNotificationsEnabled(User user, boolean enabled) {
        user.setNotificationsEnabled(enabled);
        updateUser(user);
    }

    public void setWinNotificationsEnabled(User user, boolean enabled) {
        user.setWinNotificationPref(enabled);
        updateUser(user);
    }

    public void setLoseNotificationsEnabled(User user, boolean enabled) {
        user.setLoseNotificationPref(enabled);
        updateUser(user);
    }

    public void getUserInformation(UserFetchCallback callback) {
        if (currentUserID == null || currentUserID.isEmpty()) {
            callback.onError("User ID is missing");
            return;
        }
        firestore.collection("User").document(currentUserID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            callback.onUserFetched(user);
                        } else {
                            callback.onError("Failed to parse user data.");
                        }
                    } else {
                        callback.onError("User not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error fetching user: " + e.getMessage()));
    }

    public void retrieveUserLocation(FusedLocationProviderClient fusedLocationClient, OnLocationReceivedCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            GeoPoint userLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                            callback.onLocationReceived(userLocation);
                        } else {
                            callback.onError("Location unavailable");
                        }
                    })
                    .addOnFailureListener(e -> callback.onError("Error retrieving location: " + e.getMessage()));
        } else {
            callback.onError("Location permissions not granted");
        }
    }

    public void getAllUsers(UserListCallback callback) {
        firestore.collection("User").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ArrayList<User> users = new ArrayList<>();
                task.getResult().forEach(document -> users.add(document.toObject(User.class)));
                callback.onUsersFetched(users);
            } else {
                callback.onError("Failed to fetch user list");
            }
        });
    }

    public void deleteUser(String userId, DeleteUserCallback callback) {
        firestore.collection("User").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete user: " + e.getMessage()));
    }

    private void updateUser(User user) {
        firestore.collection("User").document(user.getUserId()).set(user)
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update user: " + e.getMessage()));
    }

    private void updateUser(User user, UpdateUserCallback callback) {
        firestore.collection("User").document(user.getUserId()).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update user: " + e.getMessage()));
    }

    // Callback interfaces
    public interface UserListCallback {
        void onUsersFetched(ArrayList<User> users);
        void onError(String message);
    }

    public interface ProfileImageUploadCallback {
        void onProfileImageUploaded(String imageUrl);
        void onError(String message);
    }

    public interface ProfileImageRemovalCallback {
        void onProfileImageRemoved();
        void onError(String message);
    }

    public interface UserFetchCallback {
        void onUserFetched(User user);
        void onError(String message);
    }

    public interface OnLocationReceivedCallback {
        void onLocationReceived(GeoPoint location);
        void onError(String message);
    }

    public interface DeleteUserCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface AddUserCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface UpdateUserCallback {
        void onSuccess();
        void onError(String message);
    }

    private String getMimeType(Uri uri) {
        if (context == null) return "image/jpeg";
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = resolver.getType(uri);
        return type != null ? type : "image/jpeg";
    }
}

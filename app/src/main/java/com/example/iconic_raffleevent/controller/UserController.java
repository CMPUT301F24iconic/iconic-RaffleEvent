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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * UserController is a controller class that manages user-related functionalities
 * such as adding a user, updating user profiles, uploading/removing profile images,
 * enabling/disabling notifications, and fetching user data from Firebase.
 */
public class UserController {
    private static final String TAG = "UserController";
    private final FirebaseFirestore firestore;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;
    private final String currentUserID;
    private final Context context;
    private FirebaseAttendee firebaseAttendee;

    /**
     * Constructor for UserController.
     *
     * @param userID The current user ID.
     * @param context The context of the calling activity.
     */
    public UserController(String userID, Context context) {
        this.currentUserID = userID;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
        this.firebaseAttendee = new FirebaseAttendee();
    }

    // Regex pattern for validating phone numbers (US/international format)
    // private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?1?-?)?\\d{3}-\\d{3}-\\d{4}$");

    /**
     * Adds a new user to the Firebase Firestore database.
     *
     * @param user The User object containing user information.
     * @param callback The callback to notify on success or failure.
     */
    public void addUser(User user, AddUserCallback callback) {
        firestore.collection("User").document(user.getUserId())
                .set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to add user: " + e.getMessage()));
    }

    /**
     * Updates the profile information of a user.
     *
     * @param user The User object to update.
     * @param name The new name of the user.
     * @param email The new email of the user.
     * @param phoneNo The new phone number of the user.
     * @param callback The callback to notify the result of the update operation.
     */
    public void updateProfile(User user, String name, String email, String phoneNo, UpdateProfileCallback callback) {
        user.setName(name);
        user.setEmail(email);
        user.setPhoneNo(phoneNo);
        updateUser(user, new UpdateUserCallback() {
            @Override
            public void onSuccess() {
                callback.onProfileUpdated();
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

//    /**
//     * Validates the format of the phone number.
//     *
//     * @param phoneNo The phone number to validate.
//     * @return true if the phone number is valid, false otherwise.
//     */
//    private boolean isValidPhoneNumber(String phoneNo) {
//        return phoneNo != null && PHONE_PATTERN.matcher(phoneNo).matches();
//    }

    /**
     * Uploads the user's profile image to Firebase Storage.
     *
     * @param user The user whose profile image is being uploaded.
     * @param imageUri The URI of the image to upload.
     * @param callback The callback to notify on success or failure.
     */
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

    /**
     * Removes the user's profile image from Firebase Storage.
     *
     * @param user The user whose profile image is being removed.
     * @param callback The callback to notify on success or failure.
     */
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

    /**
     * Sets the notification preference for the user.
     *
     * @param user The user whose notification preference is being updated.
     * @param enabled True to enable notifications, false to disable.
     */
    public void setNotificationsEnabled(User user, boolean enabled) {
        user.setNotificationsEnabled(enabled);
        updateUser(user);
    }

    /**
     * Sets the win notification preference for the user.
     *
     * @param user The user whose win notification preference is being updated.
     * @param enabled True to enable win notifications, false to disable.
     */
    public void setWinNotificationsEnabled(User user, boolean enabled) {
        user.setWinNotificationPref(enabled);
        updateUser(user);
    }

    /**
     * Sets the lose notification preference for the user.
     *
     * @param user The user whose lose notification preference is being updated.
     * @param enabled True to enable lose notifications, false to disable.
     */
    public void setLoseNotificationsEnabled(User user, boolean enabled) {
        user.setLoseNotificationPref(enabled);
        updateUser(user);
    }

    /**
     * Fetches the user data from Firebase Firestore.
     *
     * @param callback The callback to notify with the fetched user or an error message.
     */
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
                            System.out.println("User" + user.getUsername());
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

    /**
     * Retrieves the user's current location using the FusedLocationProviderClient.
     *
     * @param fusedLocationClient The location client to fetch the user's location.
     * @param callback The callback to notify with the retrieved location or an error message.
     */
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

    /**
     * Fetches the list of all users from the Firestore database.
     * This method retrieves all documents from the "User" collection in Firestore.
     * @param callback The callback to notify on the result of the fetch operation.
     *                 The callback will be invoked with either the list of users
     *                 or an error message.
     */
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

    /**
     * Deletes a user from the Firestore database.
     * This method deletes the user document from the "User" collection in Firestore.
     * @param userId The unique identifier of the user to be deleted.
     * @param callback The callback to notify on the result of the delete operation.
     *                 The callback will be invoked with either a success message
     *                 or an error message.
     */
    public void deleteUser(String userId, DeleteUserCallback callback) {
        firestore.collection("User").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to delete user: " + e.getMessage()));
    }

    /**
     * Updates the user's data in Firebase Firestore.
     *
     * @param user The user whose data is being updated.
     * @param callback callback passed back to UI to indicate if user update was successful or a failure
     */
    private void updateUser(User user, UpdateUserCallback callback) {
        firestore.collection("User").document(user.getUserId()).set(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update user: " + e.getMessage()));
    }

    /**
     * Updates the user's data through firebase attendee without needed context or callback
     *
     * @param user The user whose data is being updated.
     */
    private void updateUser(User user) {
        System.out.println("Here 2");
        firebaseAttendee.updateUser(user);
    }

    /**
     * Retrieves the MIME type of a file based on its URI.
     * This method determines the MIME type of the file referenced by the provided URI.
     * @param uri The URI of the file whose MIME type is to be determined.
     * @return The MIME type of the file, or "image/jpeg" if the MIME type cannot be determined.
     */
    private String getMimeType(Uri uri) {
        if (context == null) return "image/jpeg";
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = resolver.getType(uri);
        return type != null ? type : "image/jpeg";
    }

    /**
     * Callback interface for fetching a list of users from the Firestore database.
     */
    public interface UserListCallback {
        /**
         * Callback which contains a list of User objects
         * @param users List of users
         */
        void onUsersFetched(ArrayList<User> users);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for profile image upload success or failure.
     */
    public interface ProfileImageUploadCallback {
        /**
         * Callback which contains the imageUrl for a users profile photo
         * @param imageUrl Url of profile image
         */
        void onProfileImageUploaded(String imageUrl);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for profile image removal success or failure.
     */
    public interface ProfileImageRemovalCallback {
        /**
         * Callback which is called upon successful remove profile image operation
         */
        void onProfileImageRemoved();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for fetching user information.
     */
    public interface UserFetchCallback {
        /**
         * Callback which contains a User object
         * @param user User that is fetched
         */
        void onUserFetched(User user);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for receiving location data.
     */
    public interface OnLocationReceivedCallback {
        /**
         * Callback which contains a geopoint locations
         * @param location Geopoint coordinates of a user's location
         */
        void onLocationReceived(GeoPoint location);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for deleting a user from the Firestore database.
     */
    public interface DeleteUserCallback {
        /**
         * Callback which is called upon successful delete user operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for adding a user.
     */
    public interface AddUserCallback {
        /**
         * Callback which is called upon successful add user operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for updating a user.
     */
    public interface UpdateUserCallback {
        /**
         * Callback which is called upon successful update user operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Interface for listening to user retrieval callbacks.
     * This interface should be implemented by any class that wants to receive
     * notifications when a user has been retrieved from a data source.
     * Implementing classes must define the behavior of the
     * onUserRetrieved method to handle the retrieved user.
     */
    public interface OnUserRetrievedListener {
        /**
         * Called when a user has been successfully retrieved.
         *
         * @param user The retrieved User object. If no user is found,
         *             this parameter will be null
         */
        void onUserRetrieved(User user);
    }

    /**
     * Callback interface for updating a user's profile.
     */
    public interface UpdateProfileCallback {
        void onProfileUpdated();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }
}

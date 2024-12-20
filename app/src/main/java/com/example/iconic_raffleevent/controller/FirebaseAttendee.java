package com.example.iconic_raffleevent.controller;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Notification;
import com.example.iconic_raffleevent.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Zhiyuan Li - upload image error checking
import android.util.Log;

/**
 * FirebaseAttendee class interacts with Firebase Firestore and Firebase Storage to manage user profiles, events, and notifications.
 * This class provides methods for managing users, events, event registration, waiting lists, notifications, and event media.
 */
public class FirebaseAttendee {
    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;
    private CollectionReference notificationsCollection;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseMessaging firebaseMessaging;

    /**
     * Constructs a FirebaseAttendee instance and initializes references to Firebase Firestore and Firebase Storage.
     */
    public FirebaseAttendee() {
        this.db = FirebaseFirestore.getInstance();
        this.usersCollection = db.collection("User");
        this.eventsCollection = db.collection("Event");
        this.notificationsCollection = db.collection("Notification");
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = firebaseStorage.getReference();
        this.firebaseMessaging = FirebaseMessaging.getInstance();
    }

    // User-related methods

    /**
     * Updates a user's profile in Firebase Firestore.
     *
     * @param user The user whose profile is to be updated.
     */
    public void updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            return;
        }
        System.out.println("Here 3");
        //Zhiyuan - ensure that updateUser(User user) actually updates the profileImageUrl in Firestore.
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user)  // This will update the user document with all current fields
                .addOnSuccessListener(aVoid -> Log.d("FirebaseAttendee", "User profile updated."))
                .addOnFailureListener(e -> Log.e("FirebaseAttendee", "Error updating profile", e));
    }

    /**
     * Retrieves a user from Firebase Firestore.
     *
     * @param userID   The ID of the user to retrieve.
     * @param callback The callback to handle the result of the operation.
     */
    public void getUser(String userID, UserController.UserFetchCallback callback) {
        DocumentReference userRef = usersCollection.document(userID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                callback.onUserFetched(user);
            } else {
                callback.onUserFetched(null);
            }
        });
    }

    /**
     * Retrieves all users from the Firebase database.
     * This method fetches the list of all users and passes it to the provided callback.
     *
     * @param callback The callback to handle the response with the list of users.
     */
    public void getAllUsers(UserController.UserListCallback callback) {
        // Fetch all users from Firebase and pass to callback
    }

    /**
     * Deletes a user profile from Firebase Firestore.
     *
     * @param userId  The ID of the user to delete.
     * @param callback Callback to handle the result of the operation.
     */
    public void deleteUser(String userId, DeleteUserCallback callback) {
        db.collection("User").document(userId).delete()
                .addOnSuccessListener(aVoid -> callback.onUserDeleted(true))
                .addOnFailureListener(e -> callback.onUserDeleted(false));
    }

    /**
     * Updates the waiting list for a user in Firebase Firestore.
     *
     * @param user The user whose waiting list is to be updated.
     */
    public void updateWaitingList(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("waitingListEventIds", user.getWaitingListEventIds());
    }

    /**
     * Updates the registered events for a user in Firebase Firestore.
     *
     * @param user The user whose registered events are to be updated.
     */
    public void updateRegisteredEvents(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("registeredEventIds", user.getRegisteredEventIds());
    }

    /**
     * Updates the user's win notification preference in the Firebase database.
     * The preference is stored in the user's document under the field "winNotificationPref".
     *
     * @param user The user whose win notification preference needs to be updated.
     */
    public void updateGeneralNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("generalNotificationPref", user.isGeneralNotificationPref());
    }

    // Event-related methods

    /**
     * Retrieves the details of a specific event.
     *
     * @param eventId  The ID of the event to retrieve.
     * @param callback The callback to handle the result of the operation.
     */
    public void getEventDetails(String eventId, EventController.EventDetailsCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                callback.onEventDetailsFetched(event);
            } else {
                callback.onError("Failed to fetch event details");
            }
        });
    }

    /**
     * Retrieves all events from Firebase Firestore.
     *
     * @param callback The callback to handle the result of the operation.
     */
    public void getAllEvents(EventController.EventListCallback callback) {
        eventsCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Event> events = new ArrayList<>(task.getResult().toObjects(Event.class));
                callback.onEventsFetched(events);
            } else {
                callback.onError("Failed to fetch events");
            }
        });
    }

    /**
     * Adds a new event and generates a QR code for the event.
     *
     * @param event The event to add.
     * @param user The user organizing the event.
     */
    public void addEvent(Event event, User user) {
        DocumentReference eventRef = eventsCollection.document();
        String eventId = eventRef.getId();

        // Set the ID in the notification object
        event.setEventId(eventId);
        event.setOrganizerID(user.getUserId());
        eventRef.set(event);
    }

    /**
     * Retrieves the map of entrant locations for a specific event.
     *
     * @param eventId  The ID of the event.
     * @param callback The callback to handle the result of the operation.
     */
    public void getEventMap(String eventId, EventController.EventMapCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null) {
                    Map<String, Object> locations = event.getLocations();
                    callback.onEventMapFetched(locations);
                }
            } else {
                callback.onError("Failed to fetch event and entrant locations");
            }
        });
    }

    /**
     * Deletes an event along with its associated media (poster and QR code), and cleans up user references.
     *
     * @param eventId  The ID of the event to be deleted.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void deleteEventWithMedia(String eventId, DeleteEventCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);

        eventRef.get().addOnSuccessListener(eventSnapshot -> {
            if (eventSnapshot.exists()) {
                Event event = eventSnapshot.toObject(Event.class);

                List<Task<Void>> deletionTasks = new ArrayList<>();

                // Delete the event document
                Task<Void> deleteEventTask = eventRef.delete();
                deletionTasks.add(deleteEventTask);

                // Delete poster from Firebase Storage
                String posterPath = "event_posters/" + eventId;
                StorageReference posterRef = storageReference.child(posterPath);
                Task<Void> deletePosterTask = posterRef.delete().addOnFailureListener(e -> {
                    // Handle file not found gracefully
                    if (!(e instanceof StorageException && ((StorageException) e).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND)) {
                        // Log the error
                        System.err.println("Error deleting poster: " + e.getMessage());
                    }
                });
                deletionTasks.add(deletePosterTask);

                // Delete QR code from Firebase Storage
                if (event.getQrCode() != null && !event.getQrCode().isEmpty()) {
                    String qrCodePath = "event_qrcodes/" + event.getQrCode();
                    StorageReference qrCodeRef = storageReference.child(qrCodePath);
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
//                Task<Void> removeUserReferencesTask = removeEventReferencesFromUsers(eventId, event);
//                deletionTasks.add(removeUserReferencesTask);

                // Wait for all tasks to complete successfully
                Tasks.whenAllSuccess(deletionTasks)
                        .addOnSuccessListener(tasks -> {
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            callback.onError("Failed to delete event and associated data: " + e.getMessage());
                        });
            } else {
                callback.onError("Event not found.");
            }
        }).addOnFailureListener(e -> {
            callback.onError("Failed to fetch event: " + e.getMessage());
        });
    }

    /**
     * NOT BEING USED SINCE WE ARE NOT UPDATING THE LIST FIELDS IN A USER OBJECT
     * Removes references to the event from user documents.
     *
     * @param eventId The ID of the event.
     * @param event   The event object.
     * @return A Task representing the completion of all user updates.
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
//            DocumentReference userRef = usersCollection.document(userId);
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("waitingListEventIds", FieldValue.arrayRemove(eventId));
//            updates.put("registeredEventIds", FieldValue.arrayRemove(eventId));
//            updates.put("invitedEventIds", FieldValue.arrayRemove(eventId));
//            Task<Void> updateTask = userRef.update(updates);
//            userUpdateTasks.add(updateTask);
//        }

        return Tasks.forResult(null);
    }

    /**
     * Deletes an event with the specified event ID from Firestore.
     *
     * @param eventId  The ID of the event to delete.
     * @param callback Callback interface to notify the success or failure of the deletion.
     */
    public void deleteEvent(String eventId, DeleteEventCallback callback) {
        db.collection("Event").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /**
     * Updates the event details in the Firebase database.
     * This method updates the entire event document with the provided event object.
     *
     * @param event The event object containing the updated event details.
     */
    public void updateEventDetails(Event event) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        eventRef.set(event);
    }

    /**
     * Updates the waiting list limit for a specific event.
     * Validates and sets the new limit, ensuring it is not less than the current waiting list size.
     *
     * @param eventId          The ID of the event.
     * @param waitingListLimit The new waiting list limit.
     * @param callback         Callback to notify success or failure.
     */
    public void setWaitingListLimit(String eventId, int waitingListLimit, EventController.UpdateEventCallback callback) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Event event = task.getResult().toObject(Event.class);
                        if (event != null) {
                            // Validate waiting list limit
                            int minimumLimit = event.getWaitingList() != null ? event.getWaitingList().size() : 0;

                            if (waitingListLimit >= minimumLimit) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("waitingListLimit", waitingListLimit);

                                eventsCollection.document(eventId).update(updates)
                                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                                        .addOnFailureListener(e -> callback.onError("Failed to update waiting list limit: " + e.getMessage()));
                            } else {
                                callback.onError("Limit must be at least " + minimumLimit + ".");
                            }
                        } else {
                            callback.onError("Event not found");
                        }
                    } else {
                        callback.onError("Failed to fetch event details");
                    }
                });
    }

    /**
     * Adds a user to the event's waiting list with their location.
     * Ensures the waiting list limit has not been reached before adding the user.
     *
     * @param eventId      The ID of the event.
     * @param user         The user joining the event.
     * @param userLocation The location of the user.
     * @param callback     The callback to notify success or failure.
     */
    public void joinWaitingListWithLocation(String eventId, User user, GeoPoint userLocation, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Event event = task.getResult().toObject(Event.class);
                if (event != null && !event.isWaitingListLimitReached()) {
                    WriteBatch writeBatch = db.batch();

                    // Add user ID to waitingList
                    writeBatch.update(eventRef, "waitingList", FieldValue.arrayUnion(user.getUserId()));

                    // Prepare location data with key as "userId-userName"
                    Map<String, Object> userPoint = new HashMap<>();
                    userPoint.put("locations." + user.getUserId() + "-" + user.getName(), userLocation);
                    writeBatch.update(eventRef, userPoint);

                    // Commit the batch
                    writeBatch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError("Failed to join waiting list: " + e.getMessage()));
                } else {
                    callback.onError("Waiting list limit reached or event not found");
                }
            } else {
                callback.onError("Failed to fetch event details");
            }
        });
    }

    /**
     * Adds a user to the event's waiting list without including their location.
     * Ensures the waiting list limit has not been reached before adding the user.
     *
     * @param eventId The ID of the event to which the user is joining the waiting list.
     * @param userId The ID of the user joining the waiting list.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void joinWaitingListWithoutLocation(String eventId, String userId, EventController.JoinWaitingListCallback callback) {
        eventsCollection.document(eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        Event event = task.getResult().toObject(Event.class);
                        if (event != null && !event.isWaitingListLimitReached()) {
                            eventsCollection.document(eventId)
                                    .update("waitingList", FieldValue.arrayUnion(userId))
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onError("Failed to join waiting list: " + e.getMessage()));
                        } else {
                            callback.onError("Waiting list limit reached or event not found");
                        }
                    } else {
                        callback.onError("Failed to fetch event details");
                    }
                });
    }

    /**
     * Removes a user from the event's waiting list.
     * This method updates the "waitingList" field of the event document by removing the user ID.
     *
     * @param event The event we are removing the user from
     * @param user The user leaving the waiting list.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void leaveWaitingList(Event event, User user, EventController.LeaveWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        writeBatch.update(eventRef, "waitingList", FieldValue.arrayRemove(user.getUserId()));

        if (event.isGeolocationRequired() == Boolean.TRUE) {
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> locations = (Map<String, Object>) snapshot.get("locations");

                    if (locations != null) {
                        // Find the matching key based on userId
                        String matchingKey = null;
                        for (String key : locations.keySet()) {
                            if (key.startsWith(user.getUserId() + "-")) {
                                matchingKey = key;
                                break;
                            }
                        }

                        if (matchingKey != null) {
                            // Remove location from the event object (if applicable)
                            event.deleteLocation(matchingKey);

                            // Remove the key from Firestore
                            writeBatch.update(eventRef, "locations." + matchingKey, FieldValue.delete());
                        }
                    }

                    // Commit the batch write
                    writeBatch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
                } else {
                    callback.onError("Failed to retrieve event data");
                }
            });
        } else {
            // Commit the batch write when geolocation is not required
            writeBatch.commit()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
        }
    }

    /**
     * Removes a user from the event's locations map.
     * This method updates the "locations" field of the event document by removing the user's location.
     *
     * @param event The event we are removing the user from
     * @param userId The id of the user we are removing from the waiting list.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void leaveLocationsList(Event event, String userId, EventController.LeaveLocationsListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(event.getEventId());
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        // Remove from locations map
        if (event.isGeolocationRequired() == Boolean.TRUE) {
            eventRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> locations = (Map<String, Object>) snapshot.get("locations");

                    if (locations != null) {
                        // Find the matching key based on userId
                        String matchingKey = null;
                        for (String key : locations.keySet()) {
                            if (key.startsWith(userId + "-")) {
                                matchingKey = key;
                                break;
                            }
                        }

                        if (matchingKey != null) {
                            // Remove location from the event object (if applicable)
                            event.deleteLocation(matchingKey);

                            // Remove the key from Firestore
                            writeBatch.update(eventRef, "locations." + matchingKey, FieldValue.delete());
                        }
                    }

                    // Commit the batch write
                    writeBatch.commit()
                            .addOnSuccessListener(aVoid -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
                } else {
                    callback.onError("Failed to retrieve event data");
                }
            });
        } else {
            // Commit the batch write when geolocation is not required
            writeBatch.commit()
                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
        }
    }

    /**
     * Accepts an event invitation for a user by adding them to the registered attendees list
     * and removing them from the invited list. Validates the event capacity to ensure the
     * maxAttendees limit is not exceeded.
     *
     * @param eventId The ID of the event to which the user is accepting the invitation.
     * @param userId The ID of the user accepting the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void acceptEventInvitation(String eventId, String userId, EventController.AcceptInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);

        db.runTransaction(transaction -> {
            Event event = transaction.get(eventRef).toObject(Event.class);
            if (event != null) {
                if (!event.isMaxAttendeesLimitReached()) {
                    // Add user to registeredAttendees
                    transaction.update(eventRef, "registeredAttendees", FieldValue.arrayUnion(userId));
                    // Remove user from invitedList
                    transaction.update(eventRef, "invitedList", FieldValue.arrayRemove(userId));
                } else {
                    throw new FirebaseFirestoreException("Event is full. Cannot accept invitation.", FirebaseFirestoreException.Code.ABORTED);
                }
            } else {
                throw new FirebaseFirestoreException("Event not found.", FirebaseFirestoreException.Code.NOT_FOUND);
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            callback.onSuccess();
        }).addOnFailureListener(e -> {
            callback.onError(e.getMessage());
        });
    }

    /**
     * Declines an event invitation for a user, removing them from the invited list.
     * This method updates the "invitedList" field of the event document by removing the user ID.
     *
     * @param eventId The ID of the event to which the user is declining the invitation.
     * @param userId The ID of the user declining the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void declineEventInvitation(String eventId, String userId, EventController.DeclineInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("declinedList", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to decline invitation"));

        eventRef.update("invitedList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to decline invitation"));
    }

    /**
     * Scans a QR code to find an event and attempts to add the user to the waiting list.
     * This method retrieves the event associated with the QR code and joins the waiting list.
     *
     * @param qrCodeData The data encoded in the QR code (event identifier).
     * @param callback   The callback interface to notify the success or failure of the operation.
     */
    public void scanQRCode(String qrCodeData, EventController.ScanQRCodeCallback callback) {
        eventsCollection.whereEqualTo("qrCode", qrCodeData)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            String eventId = task.getResult().getDocuments().get(0).getId();
                            callback.onEventFound(eventId);
                        } else {
                            callback.onError("Event not found");
                        }
                    } else {
                        if (task.getException() != null) {
                            callback.onError(task.getException().getMessage());
                        } else {
                            callback.onError("Failed to scan QR code");
                        }
                    }
                });
    }


    // Notification-related methods

    /**
     * Retrieves the notifications for a specific user.
     * Queries Firestore for notifications associated with the user.
     *
     * @param userId   The ID of the user whose notifications are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getNotifications(String userId, NotificationController.GetNotificationsCallback callback) {
        notificationsCollection.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Notification> notifications = task.getResult().toObjects(Notification.class);
                        callback.onNotificationsFetched(notifications);
                    } else {
                        callback.onError("Failed to fetch notifications");
                    }
                });
    }

    /**
     * Marks a specific notification as read for a user.
     * Updates the "read" field of the notification document in Firestore.
     *
     * @param notificationId The ID of the notification to mark as read.
     * @param callback       The callback interface to notify the success or failure of the operation.
     */
    public void markNotificationAsRead(String notificationId, NotificationController.MarkNotificationAsReadCallback callback) {
        notificationsCollection.document(notificationId)
                .update("read", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to mark notification as read"));
    }

    /**
     * Retrieves the list of events for a user's waiting list.
     * The method queries Firestore for all events where the user is present in the waiting list.
     *
     * @param userId   The ID of the user whose waiting list events are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getUserWaitingListEvents(String userId, EventController.EventListCallback callback) {
        eventsCollection.whereArrayContains("waitingList", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = task.getResult().toObjects(Event.class);
                        callback.onEventsFetched(new ArrayList<>(events));
                    } else {
                        callback.onError("Failed to fetch events for user waiting list.");
                    }
                });
    }

    /**
     * Retrieves the list of events a user is in a waiting list for or owns
     * The method queries Firestore for all events where the user is present in the waiting list or owns.
     *
     * @param userId   The ID of the user whose waiting list events/owned events are to be retrieved.
     * @param callback The callback interface to notify the success or failure of the operation.
     */
    public void getAllUserEvents(String userId, EventController.EventListCallback callback) {
        Task<QuerySnapshot> waitingListQuery = eventsCollection.whereArrayContains("waitingList", userId).get();
        Task<QuerySnapshot> invitedListQuery = eventsCollection.whereArrayContains("invitedList", userId).get();
        Task<QuerySnapshot> registeredAttendeesQuery = eventsCollection.whereArrayContains("registeredAttendees", userId).get();
        Task<QuerySnapshot> ownerQuery = eventsCollection.whereEqualTo("organizerID", userId).get();

        Tasks.whenAllSuccess(waitingListQuery, ownerQuery)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get results from the waiting list query
                        List<Event> waitingListEvents = waitingListQuery.getResult().toObjects(Event.class);
                        List<Event> invitedListEvents = invitedListQuery.getResult().toObjects(Event.class);
                        List<Event> registeredListEvents = registeredAttendeesQuery.getResult().toObjects(Event.class);
                        List<Event> organizerEvents = ownerQuery.getResult().toObjects(Event.class);

                        // Combine results into a single list, avoiding duplicates
                        List<Event> allEvents = new ArrayList<>();
                        addEventsToList(allEvents, waitingListEvents);
                        addEventsToList(allEvents, invitedListEvents);
                        addEventsToList(allEvents, registeredListEvents);
                        addEventsToList(allEvents, organizerEvents);

                        // Return the combined event list via callback
                        callback.onEventsFetched(new ArrayList<>(allEvents));
                    } else {
                        callback.onError("Failed to fetch events for user waiting list.");
                    }
                });
    }

    /**
     * Adds events from the source list to the target list, avoiding duplicates.
     *
     * @param targetList The list to which events are added.
     * @param sourceList The list of events to add.
     */
    private void addEventsToList(List<Event> targetList, List<Event> sourceList) {
        for (Event event : sourceList) {
            if (!targetList.contains(event)) {
                targetList.add(event);
            }
        }
    }

    /**
     * Updates the event's invited and declined lists in the Firebase database.
     * This method updates the event document with the provided lists of invited and declined users.
     *
     * @param eventId The ID of the event whose lists are being updated.
     * @param invitedList The list of users invited to the event.
     * @param waitingList The list of users who have declined the invitation.
     * @param callback The callback to notify the success or failure of the operation.
     */
    public void updateEventLists(String eventId, List<String> invitedList, List<String> waitingList, List<String> registeredAttendees, List<String> cancelledList, UpdateCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("waitingList", waitingList);
        updates.put("invitedList", invitedList);
        updates.put("registeredAttendees", registeredAttendees);
        updates.put("declinedList", cancelledList);

        eventsCollection.document(eventId)
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to update event lists: " + e.getMessage()));
    }

    /**
     * Uploads the event poster to Firebase Storage and retrieves the download URL.
     * The image is uploaded under the "event_posters" directory in Firebase Storage.
     *
     * @param eventUri URI of the event poster image.
     * @param eventObj The event object associated with the poster.
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void addEventPoster(Uri eventUri, Event eventObj, EventController.UploadEventPosterCallback callback) {
        String eventId = eventObj.getEventId();
        String filePath = "event_posters/" + eventId;
        StorageReference ref = storageReference.child(filePath);

        ref.putFile(eventUri).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            callback.onSuccessfulUpload(downloadUrl);
        })).addOnFailureListener(e -> callback.onError("Unable to upload event poster: " + e.getMessage()));
    }

    /**
     * Adds the QR code for an event to Firebase Storage and retrieves the download URL.
     * The QR code is generated based on the event's `qrCode` field and uploaded as a JPEG image.
     *
     * @param eventObj The event object containing the QR code data.
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void addEventQRCode(Event eventObj, EventController.UploadEventQRCodeCallback callback) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            Bitmap eventQrCode = barcodeEncoder.encodeBitmap(eventObj.getQrCode(), BarcodeFormat.QR_CODE, 400, 400);
            String filePath = "event_qrcodes/" + eventObj.getQrCode();
            StorageReference ref = storageReference.child(filePath);

            // Convert eventQrCode into bytes array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            eventQrCode.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] image = baos.toByteArray();

            ref.putBytes(image).addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                callback.onSuccessfulQRUpload(downloadUrl);
            })).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    callback.onError("Unable to upload event QR code: " + e.getMessage());
                }
            });

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }



    // --- Sending/Receiving notification --- //

    /**
     * Uploads a notification to firebase telling the user if they have been drawn
     * @param notification Notification to be uploaded
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void sendDrawNotification(Notification notification, EventController.SendDrawNotificationCallback callback){
        DocumentReference notificationRef = notificationsCollection.document(notification.getNotificationId());
        notificationRef.set(notification)  // This will update the user document with all current fields
                .addOnSuccessListener(aVoid -> Log.d("FirebaseAttendee", "Notification sent."))
                .addOnFailureListener(e -> callback.onError("Error sending notification"));
    }

    /**
     * Uploads a notification to firebase which is a general notification sent by an organizer
     * @param notification Notification to be uploaded
     * @param callback The callback interface to notify the success or failure of the upload.
     */
    public void sendGeneralNotification(Notification notification, NotificationController.SendNotificationCallback callback){
        DocumentReference notificationRef = notificationsCollection.document();
        String notificationId = notificationRef.getId();

        // Set the ID in the notification object
        notification.setNotificationId(notificationId);

        notificationRef.set(notification)  // This will update the user document with all current fields
                .addOnSuccessListener(aVoid -> callback.onSuccess(Boolean.TRUE))
                .addOnFailureListener(e -> callback.onError("Error sending notification"));
    }

    /**
     * Callback interface for handling the result of fetching event details.
     */
    public interface EventDetailsCallback {
        /**
         * Callback which contains an Event object
         * @param event The event fetched
         */
        void onEventDetailsFetched(Event event);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for handling the result of fetching user details.
     */
    public interface UserFetchCallback {
        /**
         * Callback which contains a User object
         * @param user The user fetched
         */
        void onUserFetched(User user);

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for handling success or failure of an update operation.
     */
    public interface UpdateCallback {
        /**
         * Callback which is called upon successful update operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for handling success or failure of an event deletion operation.
     */
    public interface DeleteEventCallback {
        /**
         * Callback which is called upon successful delete event operation
         */
        void onSuccess();

        /**
         * Callback which contains an error message
         * @param message description of the error
         */
        void onError(String message);
    }

    /**
     * Callback interface for handling the result of a user deletion operation.
     */
    public interface DeleteUserCallback {
        /**
         * Callback which contains a Boolean reflecting the success of the delete user operation
         * @param success Boolean value regarding delete user success
         */
        void onUserDeleted(boolean success);
    }
}

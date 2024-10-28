package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAttendee {

    private FirebaseFirestore db;
    private CollectionReference usersCollection;
    private CollectionReference eventsCollection;

    public FirebaseAttendee() {
        db = FirebaseFirestore.getInstance();
        usersCollection = db.collection("users");
        eventsCollection = db.collection("events");
    }

    public void updateUser(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.set(user);
    }

    public void updateWaitingList(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("waitingListEventIds", user.getWaitingListEventIds());
    }

    public void updateRegisteredEvents(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("registeredEventIds", user.getRegisteredEventIds());
    }

    public void updateNotificationPreference(User user) {
        DocumentReference userRef = usersCollection.document(user.getUserId());
        userRef.update("notificationsEnabled", user.isNotificationsEnabled());
    }

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

    public void joinWaitingList(String eventId, String userId, EventController.JoinWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to join waiting list"));
    }

    public void leaveWaitingList(String eventId, String userId, EventController.LeaveWaitingListCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to leave waiting list"));
    }

    public void acceptEventInvitation(String eventId, String userId, EventController.AcceptInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("registeredAttendees", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to accept invitation"));
    }

    public void declineEventInvitation(String eventId, String userId, EventController.DeclineInvitationCallback callback) {
        DocumentReference eventRef = eventsCollection.document(eventId);
        eventRef.update("waitingList", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Failed to decline invitation"));
    }
}

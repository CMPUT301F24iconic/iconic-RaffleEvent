package com.example.iconic_raffleevent.controller;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.OnEventRetrievedListener;
import com.example.iconic_raffleevent.model.OnUserRetrievedListener;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

/**
 * FirebaseController handles all logic related to firebase including the addition,
 * deletion, and updating of values in the database
 * It communicates between the application and database server
 */
public class FirebaseController {
    private FirebaseFirestore firebaseDB;
    private CollectionReference userRef;
    private CollectionReference notificationRef;
    private CollectionReference facilityRef;
    private CollectionReference eventRef;

    public FirebaseController() {
        // Get instance of firebase database or generate one if none exist
        firebaseDB = FirebaseFirestore.getInstance();
        userRef = firebaseDB.collection("User");
        notificationRef = firebaseDB.collection("Notification");
        facilityRef = firebaseDB.collection("Facility");
        eventRef = firebaseDB.collection("Event");
    }

    public void getUser(String userID, final OnUserRetrievedListener listener) {
        userRef.document(userID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    listener.onUserRetrieved(user);
                } else {
                    listener.onUserRetrieved(null);
                }
            } else {
                listener.onUserRetrieved(null);
            }
        });
    }

    public void addUser(User user) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("name", user.getName());
        userData.put("phone_number", user.getPhoneNo());
        userData.put("roles", user.getRoles());
        userData.put("userId", user.getUserId());
        userData.put("username", user.getUsername());

        // Add document to firestore
        userRef.document(user.getUserId()).set(userData);

        // This snapshot listener should most likely go in EventController. That way the views or array
        // adapter can be updated with the user information
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String user = doc.getId();
                        String name = doc.getString("name");
                        Log.d("Firestore", String.format("User(%s, %s) fetched", user, name));
                    }
                }
            }
        });
    }

    public void getEvent(String eventID, final OnEventRetrievedListener listener) {
        eventRef.document(eventID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    listener.onEventRetrieved(event);
                } else {
                    listener.onEventRetrieved(null);
                }
            } else {
                listener.onEventRetrieved(null);
            }
        });
    }

    public void addEvent(Event event) {
        HashMap<String, Object> eventData = new HashMap<>();


        // This snapshot listener should most likely go in EventController. That way the views or array
        // adapter can be updated with the user information
        userRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc: querySnapshots) {
                        String user = doc.getId();
                        String name = doc.getString("name");
                        Log.d("Firestore", String.format("User(%s, %s) fetched", user, name));
                    }
                }
            }
        });
    }
}


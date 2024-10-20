package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.FirebaseModel;

/**
 * EventController handles the logic related to event creation, modification, and management.
 * It communicates between the event model and the view components.
 */
public class EventController {

    private FirebaseModel firebaseModel;

    public EventController() {
        this.firebaseModel = new FirebaseModel();
    }

    /**
     * Creates a new event and saves it to Firebase.
     *
     * @param event The event object containing event details.
     */
    public void createEvent(Event event) {
        // Validate event data (e.g., check if all fields are filled)
        if (event.getTitle().isEmpty() || event.getDate().isEmpty()) {
            // Handle validation error (show message to the user)
            return;
        }

        // Save event to Firebase
        firebaseModel.saveEvent(event);
    }

    /**
     * Fetches event details from Firebase and updates the view.
     *
     * @param eventId The ID of the event to fetch.
     * @param listener Listener that handles event retrieval and UI updates.
     */
    public void getEvent(String eventId, FirebaseModel.OnEventReceivedListener listener) {
        // Fetch event from Firebase and pass the data to the view via listener
        firebaseModel.getEvent(eventId, listener);
    }
}
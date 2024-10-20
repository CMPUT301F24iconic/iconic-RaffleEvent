package com.example.iconic_raffleevent.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a waiting list for an event.
 * Stores a list of users (entrants) who are waiting to be drawn for the event.
 */
public class WaitingList {
    private String eventId;
    private List<User> entrants;  // List of users in the waiting list

    // Constructor
    public WaitingList(String eventId) {
        this.eventId = eventId;
        this.entrants = new ArrayList<>();
    }

    // Add an entrant to the waiting list
    public void addEntrant(User user) {
        entrants.add(user);
    }

    // Remove an entrant from the waiting list
    public void removeEntrant(User user) {
        entrants.remove(user);
    }

    // Get a list of all entrants
    public List<User> getEntrants() {
        return entrants;
    }

    // Randomly select an entrant from the waiting list
    public User drawEntrant() {
        if (entrants.isEmpty()) return null;
        int randomIndex = (int) (Math.random() * entrants.size());
        return entrants.get(randomIndex);
    }
}
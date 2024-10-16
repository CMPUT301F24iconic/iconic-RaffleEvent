package com.example.iconic_raffleevent;

import java.util.ArrayList;

public class Organizer extends User {
    private String role;
    private ArrayList<Event> createdEvents;
    private Facility facility;


    public Organizer(String name, String email, String deviceID) {
        super(name, email, deviceID);
        this.role = "organizer";
        this.createdEvents = new ArrayList<Event>();
    }

    public void addEvent(Event event) {
        createdEvents.add(event);
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public ArrayList<Event> getCreatedEvents() {
        return createdEvents;
    }

    public String getRole() {
        return role;
    }

    public Facility getFacility() {
        return facility;
    }
}

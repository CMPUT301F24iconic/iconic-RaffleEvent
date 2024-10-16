package com.example.iconic_raffleevent;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;

public class Facility {
    private Organizer owner;
    private ArrayList<Event> eventsInFacility;
    private String facilityName;
    private String description;
    // This may need to be changed if we are using long and lat
    private String location;
    private ArrayList<Date> availableTimes;
    private Integer capacity;
    private String poster;


    public Facility(Organizer owner, String facilityName, String description, String location, Integer capacity) {
        this.owner = owner;
        this.facilityName = facilityName;
        this.eventsInFacility = new ArrayList<Event>();
        this.description = description;
        this.location = location;
        this.availableTimes = new ArrayList<Date>();
        this.capacity = capacity;
    }

    public Organizer getOwner() {
        return owner;
    }

    public void setOwner(Organizer owner) {
        this.owner = owner;
    }

    public ArrayList<Event> getEventsInFacility() {
        return eventsInFacility;
    }

    public Event getEventInFacility(Integer index) {
        return eventsInFacility.get(index);
    }

    public void setEventsInFacility(ArrayList<Event> eventsInFacility) {
        this.eventsInFacility = eventsInFacility;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<Date> getAvailableTimes() {
        return availableTimes;
    }

    public Date getTime(Integer index) {
        return availableTimes.get(index);
    }

    public void setAvailableTimes(ArrayList<Date> availableTimes) {
        this.availableTimes = availableTimes;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void addEvent(Event event) {
        this.eventsInFacility.add(event);
    }

    public void addAvailableTime(Date time) {
        this.availableTimes.add(time);
    }

    public void deleteEvent(Event event) {
        this.eventsInFacility.remove(event);
    }

    public void deleteTime(Date time) {
        this.availableTimes.remove(time);
    }
}

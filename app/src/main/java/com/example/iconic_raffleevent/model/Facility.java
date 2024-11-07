package com.example.iconic_raffleevent.model;

import java.io.Serializable;

public class Facility implements Serializable {

    private String id;  // Unique identifier for each facility
    private String facilityName;
    private String facilityLocation;
    private String additionalInfo;
    private User creator;  // Organizer of the event

    // Default constructor for Firestore serialization
    public Facility() {}

    // Constructor for required fields
    public Facility(String facilityName, String facilityLocation, User creator) {
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
        this.creator = creator;
        this.additionalInfo = "";  // Optional, can be set later
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityLocation() {
        return facilityLocation;
    }

    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }
}

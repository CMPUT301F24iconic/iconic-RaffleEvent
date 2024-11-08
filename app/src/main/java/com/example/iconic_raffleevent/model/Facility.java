package com.example.iconic_raffleevent.model;

import java.io.Serializable;

/**
 * Represents a facility associated with an event, containing details such as name, location,
 * additional information, and the creator (organizer).
 */
public class Facility implements Serializable {

    private String id;  // Unique identifier for each facility
    private String facilityName;
    private String facilityLocation;
    private String additionalInfo;
    private User creator;  // Organizer of the event

    /**
     * Default constructor for Firestore serialization.
     */
    public Facility() {}

    /**
     * Constructor for creating a facility with required fields.
     * @param facilityName
     * This is the name of the facility.
     * @param facilityLocation
     * This is the location of the facility.
     * @param creator
     * This is the User object representing the organizer of the event.
     */
    public Facility(String facilityName, String facilityLocation, User creator) {
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
        this.creator = creator;
        this.additionalInfo = "";  // Optional, can be set later
    }

    /**
     * Gets the unique identifier for the facility.
     * @return
     * Return the facility ID as a String.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the facility.
     * @param id
     * This is the unique ID to assign to the facility.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the name of the facility.
     * @return
     * Return the facility name as a String.
     */
    public String getFacilityName() {
        return facilityName;
    }

    /**
     * Sets the name of the facility.
     * @param facilityName
     * This is the name to assign to the facility.
     */
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    /**
     * Gets the location of the facility.
     * @return
     * Return the facility location as a String.
     */
    public String getFacilityLocation() {
        return facilityLocation;
    }

    /**
     * Sets the location of the facility.
     * @param facilityLocation
     * This is the location to assign to the facility.
     */
    public void setFacilityLocation(String facilityLocation) {
        this.facilityLocation = facilityLocation;
    }

    /**
     * Gets any additional information about the facility.
     * @return
     * Return the additional information as a String.
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets additional information for the facility.
     * @param additionalInfo
     * This is additional information to add about the facility.
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * Gets the creator (organizer) of the facility.
     * @return
     * Return the User object representing the creator.
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Sets the creator (organizer) of the facility.
     * @param creator
     * This is the User object representing the creator to assign.
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }
}

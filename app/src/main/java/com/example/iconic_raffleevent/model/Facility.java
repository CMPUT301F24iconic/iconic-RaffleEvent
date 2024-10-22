package com.example.iconic_raffleevent.model;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a facility created by an organizer.
 * Each facility must have a unique name and location, along with the available timings and capacity.
 * It also includes optional details such as additional information and a poster.
 */
public class Facility {

    private String facilityId;         // Unique identifier for the facility (could be UUID)
    private String facilityName;       // Unique name of the facility
    private String location;           // Unique location of the facility
    private Date openTime;             // The time when the facility opens
    private Date closeTime;            // The time when the facility closes
    private int capacity;              // The number of people the facility can accommodate
    private String additionalDetails;  // Optional additional details about the facility
    private String posterUrl;          // Optional URL or file path for the facility poster
    private User creator;              // Creator of the facility (one-to-one relationship with User)

    /**
     * Constructor that initializes the facility with required fields and ensures the creator is assigned.
     *
     * @param facilityName The unique name of the facility.
     * @param location The unique location of the facility.
     * @param openTime The time when the facility opens.
     * @param closeTime The time when the facility closes.
     * @param capacity The capacity of the facility.
     * @param creator The user who created the facility.
     */
    public Facility(String facilityName, String location, Date openTime, Date closeTime, int capacity, User creator) {
        if (facilityName == null || location == null || creator == null) {
            throw new IllegalArgumentException("Facility name, location, and creator are required.");
        }
        this.facilityId = generateFacilityId();  // Generate a unique ID for the facility
        this.facilityName = facilityName;
        this.location = location;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.capacity = capacity;
        this.creator = creator;
    }

    /**
     * Generates a unique ID for the facility. This can be implemented as a UUID or similar.
     *
     * @return The generated unique ID string.
     */
    private String generateFacilityId() {
        return java.util.UUID.randomUUID().toString();  // Generates a random unique ID
    }

    // Getters and setters

    public String getFacilityId() {
        return facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    // Equals and hashCode for comparing Facility objects

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Facility facility = (Facility) o;
        return facilityName.equals(facility.facilityName) &&
                location.equals(facility.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(facilityName, location);
    }
}

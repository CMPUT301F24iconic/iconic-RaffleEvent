package com.example.iconic_raffleevent.model;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an event within the raffle system.
 * This includes event details such as title, description, location, date, time, and attendee lists.
 */
public class Event {
    private String eventId;
    private String eventTitle;
    private String eventDescription;
    private String eventLocation;
    private String eventStartDate;
    private String eventStartTime;
    private String eventEndDate;
    private String eventEndTime;
    private String eventImageUrl;
    private Integer maxAttendees;
    private boolean geolocationRequired;
    private List<String> waitingList;
    private Integer waitingListLimit;
    private List<String> registeredAttendees;
    private String qrCode;
    private String organizerId;
    private String facilityId;
    private ArrayList<String> declinedList;
    private ArrayList<String> invitedList;
    private Map<String, Object> locations;

    // Qrcode Url
    private String eventQrUrl;

    /**
     * Constructs an empty Event object with initialized lists.
     */
    public Event() {
        declinedList = new ArrayList<>();
        invitedList = new ArrayList<>();
        waitingList = new ArrayList<>();
        registeredAttendees = new ArrayList<>();
        this.waitingListLimit = Integer.MAX_VALUE;
        this.maxAttendees = Integer.MAX_VALUE;
    }

    // Getters and setters

    /**
     * Gets the event ID.
     * @return the event ID as a String
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     * @param eventId the event ID as a String
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the title of the event.
     * @return the event title as a String
     */
    public String getEventTitle() {
        return eventTitle;
    }

    /**
     * Sets the title of the event.
     * @param eventTitle the event title as a String
     */
    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    /**
     * Gets the description of the event.
     * @return the event description as a String
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     * Sets the description of the event.
     * @param eventDescription the event description as a String
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * Gets the location of the event.
     * @return the event location as a String
     */
    public String getEventLocation() {
        return eventLocation;
    }

    /**
     * Sets the location of the event.
     * @param eventLocation the event location as a String
     */
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    /**
     * Gets the start date of the event.
     * @return the event start date as a String
     */
    public String getEventStartDate() {
        return eventStartDate;
    }

    /**
     * Sets the start date of the event.
     * @param eventStartDate the event start date as a String
     */
    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    /**
     * Gets the start time of the event.
     * @return the event start time as a String
     */
    public String getEventStartTime() {
        return eventStartTime;
    }

    /**
     * Sets the start time of the event.
     * @param eventStartTime the event start time as a String
     */
    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    /**
     * Gets the end date of the event.
     * @return the event end date as a String
     */
    public String getEventEndDate() {
        return eventEndDate;
    }

    /**
     * Sets the end date of the event.
     * @param eventEndDate the event end date as a String
     */
    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    /**
     * Gets the end time of the event.
     * @return the event end time as a String
     */
    public String getEventEndTime() {
        return eventEndTime;
    }

    /**
     * Sets the end time of the event.
     * @param eventEndTime the event end time as a String
     */
    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    /**
     * Gets the URL of the event image.
     * @return the event image URL as a String
     */
    public String getEventImageUrl() {
        return eventImageUrl;
    }

    /**
     * Sets the URL of the event image.
     * @param eventImageUrl the event image URL as a String
     */
    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    /**
     * Gets the maximum number of attendees for the event.
     *
     * @return the max number of attendees, or Integer.MAX_VALUE if not explicitly set.
     */
    public Integer getMaxAttendees() {
        return maxAttendees != null ? maxAttendees : Integer.MAX_VALUE;
    }

    /**
     * Sets the maximum number of attendees for the event.
     *
     * @param maxAttendees the maximum number of attendees.
     *                     If null or negative, it defaults to Integer.MAX_VALUE.
     */
    public void setMaxAttendees(Integer maxAttendees) {
        if (maxAttendees == null || maxAttendees < 0) {
            this.maxAttendees = Integer.MAX_VALUE;
        } else {
            this.maxAttendees = maxAttendees;
        }
    }

    /**
     * Checks if the maximum attendees limit has been reached.
     *
     * @return true if the registered attendees count is greater than or equal to the limit,
     *         or false if the limit has not been reached.
     */
    public boolean isMaxAttendeesLimitReached() {
        return registeredAttendees != null && registeredAttendees.size() >= getMaxAttendees();
    }

    /**
     * Checks if geolocation is required for the event.
     * @return true if geolocation is required, otherwise false
     */
    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    /**
     * Sets whether geolocation is required for the event.
     * @param geolocationRequired a boolean indicating geolocation requirement
     */
    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    /**
     * Gets the list of waiting attendees.
     * @return a List of Strings representing waiting attendees
     */
    public List<String> getWaitingList() {
        return waitingList;
    }

    /**
     * Sets the waiting list of attendees.
     * @param waitingList a List of Strings representing waiting attendees
     */
    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Gets the waiting list limit.
     *
     * @return the waiting list limit, or Integer.MAX_VALUE if no limit is set.
     */
    public Integer getWaitingListLimit() {
        return waitingListLimit != null ? waitingListLimit : Integer.MAX_VALUE;
    }

    /**
     * Sets the waiting list limit.
     *
     * @param waitingListLimit the maximum number of entries allowed in the waiting list.
     *                        If null or negative, it defaults to Integer.MAX_VALUE.
     */
    public void setWaitingListLimit(Integer waitingListLimit) {
        if (waitingListLimit == null || waitingListLimit < 0) {
            this.waitingListLimit = Integer.MAX_VALUE;
        } else {
            this.waitingListLimit = waitingListLimit;
        }
    }

    /**
     * Checks if the waiting list limit has been reached.
     *
     * @return true if the waiting list size is greater than or equal to the limit,
     *         or false if the limit has not been reached.
     */
    public boolean isWaitingListLimitReached() {
        return waitingList != null && waitingList.size() >= getWaitingListLimit();
    }

    /**
     * Adds an entrant to the waiting list.
     * @param entrantID the ID of the entrant to add as a String
     */
    public void addWaitingListEntrant(String entrantID) {
        this.waitingList.add(entrantID);
    }

    /**
     * Gets the list of registered attendees.
     * @return a List of Strings representing registered attendees
     */
    public List<String> getRegisteredAttendees() {
        return registeredAttendees;
    }

    /**
     * Sets the list of registered attendees.
     * @param registeredAttendees a List of Strings representing registered attendees
     */
    public void setRegisteredAttendees(List<String> registeredAttendees) {
        this.registeredAttendees = registeredAttendees;
    }

    /**
     * Adds an attendee to the registered list.
     * @param registeredID the ID of the attendee as a String
     */
    public void addRegisteredAttendees(String registeredID) {
        this.registeredAttendees.add(registeredID);
    }

    /**
     * Returns the hashed qrcode string for an event
     * @return qrCode the hashed qrcode
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * Sets the event qrcode
     * @param qrCode the hashed qrcode of the event
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    /**
     * The Url linking to an qrcode image
     * @param qrUrl url for qrcode image
     */
    public void setEventQrUrl(String qrUrl) {
        this.eventQrUrl = qrUrl;
    }

    /**
     * Return the qrcode url
     * @return eventQrUrl The url of the qrcode
     */
    public String getEventQrUrl() {
        return this.eventQrUrl;
    }

    /**
     * Set the organizer id for the event
     * @param organizerID The id of the user who created the event
     */
    public void setOrganizerID(String organizerID) {
        this.organizerId = organizerID;
    }

    /**
     * Gets the ID of the event organizer.
     * @return
     * Return the organizer's ID as a String.
     */
    public String getOrganizerID() {
        return this.organizerId;
    }

    /**
     * Gets the list of users who declined the invitation.
     * @return
     * Return the declined list as an ArrayList of Strings.
     */
    public ArrayList<String> getDeclinedList() {
        return this.declinedList;
    }

    /**
     * Gets the list of users who are invited to the event.
     * @return
     * Return the invited list as an ArrayList of Strings.
     */
    public ArrayList<String> getInvitedList() {
        return this.invitedList;
    }

    /**
     * Sets the list of users invited to the event.
     * @param invitedList
     * This is the ArrayList of Strings representing the invited users.
     */
    public void setInvitedList(ArrayList<String> invitedList) {
        this.invitedList = invitedList;
    }

    /**
     * Sets the list of users who declined the invitation.
     * @param declinedList
     * This is the ArrayList of Strings representing users who declined.
     */
    public void setDeclinedList(ArrayList<String> declinedList) {
        this.declinedList = declinedList;
    }

    /**
     * Adds a user to the invite list.
     * @param userId
     * This is the ID of the user to add to the invited list.
     */
    public void addToInviteList(String userId) {
        this.invitedList.add(userId);
    }

    /**
     * Adds a user to the declined list.
     * @param userId
     * This is the ID of the user to add to the declined list.
     */
    public void addToDeclineList(String userId) {
        this.declinedList.add(userId);
    }

    /**
     * Removes a user from the invite list.
     * @param userId
     * This is the ID of the user to remove from the invited list.
     */
    public void removeFromInviteList(String userId) {
        this.invitedList.remove(userId);
    }

    /**
     * Removes a user from the declined list.
     * @param userId
     * This is the ID of the user to remove from the declined list.
     */
    public void removeFromDeclinedList(String userId) {
        this.declinedList.remove(userId);
    }

    /**
     * Set the facility ID for an event
     * @param facilityId
     * The facility ID for the event
     */
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the facility ID for an event
     * @return
     * Return the facility ID for an event
     */
    public String getFacilityId() {
        return facilityId;
    }

    /**
     * Gets a maps containing all the entrants who joined a waitlist and the locations they joined from
     * @return Map<String, Object> The map of entrants and their locations
     */
    public Map<String, Object> getLocations() {
        return locations;
    }

    /**
     * Sets a map containing entrants and their locations
     * @param locations a map holding entrant IDs as the key and their location as the value
     */
    public void setLocations(Map<String, Object> locations) {
        this.locations = locations;
    }

    /**
     * Adds a location to the locations map
     * @param key user ID
     * @param location Location user joined waitlist from
     */
    public void addLocation(String key, Object location) {
        this.locations.put(key, location);
    }

    /**
     * Gets a location based on an entrant id
     * @param key entrant id
     * @return Object of users location
     */
    public Object getLocation(String key) {
        return this.locations.get(key);
    }
}
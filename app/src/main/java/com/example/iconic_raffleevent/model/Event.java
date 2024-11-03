package com.example.iconic_raffleevent.model;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

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
    private int maxAttendees;
    private boolean geolocationRequired;
    private List<String> waitingList;
    private List<String> registeredAttendees;
    private String qrCode;
    private String organizerId;
    private ArrayList<String> declinedList;
    private ArrayList<String> invitedList;
    private ArrayList<GeoPoint> entrantLocations;

    // Qrcode Bitmap
    private Bitmap eventQRImage;

    public Event() {
        declinedList = new ArrayList<>();
        invitedList = new ArrayList<>();
        waitingList = new ArrayList<>();
        registeredAttendees = new ArrayList<>();
        entrantLocations = new ArrayList<>();
    }

    // Getters and setters

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(String eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public String getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(String eventStartTime) {
        this.eventStartTime = eventStartTime;
    }

    public String getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(String eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public String getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(String eventEndTime) {
        this.eventEndTime = eventEndTime;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public int getMaxAttendees() {
        return maxAttendees;
    }

    public void setMaxAttendees(int maxAttendees) {
        this.maxAttendees = maxAttendees;
    }

    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }

    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    public void addWaitingListEntrant(String entrantID) {
        this.waitingList.add(entrantID);
    }

    public List<String> getRegisteredAttendees() {
        return registeredAttendees;
    }

    public void setRegisteredAttendees(List<String> registeredAttendees) {
        this.registeredAttendees = registeredAttendees;
    }

    public void addRegisteredAttendees(String registeredID) {
        this.registeredAttendees.add(registeredID);
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setBitmap() {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            this.eventQRImage = barcodeEncoder.encodeBitmap(this.qrCode, BarcodeFormat.QR_CODE, 400, 400);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public Bitmap getEventQR() {
        return eventQRImage;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerId = organizerID;
    }

    public String getOrganizerID() {
        return this.organizerId;
    }

    public ArrayList<String> getDeclinedList() {
        return this.declinedList;
    }

    public ArrayList<String> getInvitedList() {
        return this.invitedList;
    }

    public void setInvitedList(ArrayList<String> invitedList) {
        this.invitedList = invitedList;
    }

    public void setDeclinedList(ArrayList<String> declinedList) {
        this.declinedList = declinedList;
    }

    public void addToInviteList(String userId) {
        this.invitedList.add(userId);
    }

    public void addToDeclineList(String userId) {
        this.declinedList.add(userId);
    }

    public void removeFromInviteList(String userId) {
        this.invitedList.remove(userId);
    }

    public void removeFromDeclinedList(String userId) {
        this.declinedList.remove(userId);
    }

    public ArrayList<GeoPoint> getEntrantLocations() {
        return this.entrantLocations;
    }

    public void addEntrantLocation(GeoPoint entrantLocation) {
        this.entrantLocations.add(entrantLocation);
    }

    public void deleteEntrantLocation(GeoPoint entrantLocation) {
        this.entrantLocations.remove(entrantLocation);
    }

    public void setEntrantLocations(ArrayList<GeoPoint> entrantLocations) {
        this.entrantLocations.clear();
        this.entrantLocations.addAll(entrantLocations);
    }
}
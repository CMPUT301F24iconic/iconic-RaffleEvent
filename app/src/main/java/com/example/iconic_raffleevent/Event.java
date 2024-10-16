package com.example.iconic_raffleevent;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    private Organizer owner;
    private String title;
    private String location;
    private Date startTime;
    private Date endTime;
    private Integer maximumAttendees;
    private String description;
    // private QRCode qrcode;
    private String poster;
    private ArrayList<Entrant> waitlist;
    private ArrayList<Entrant> pendingInvites;
    private ArrayList<Entrant> cancelledEntrants;
    private ArrayList<Entrant> enrolledEntrants;
    private ArrayList<String> entrantLocations;

    public Event(Organizer owner, String title, String location, Date startTime, Date endTime, Integer maximumAttendees, String description, String poster) {
        this.owner = owner;
        this.title = title;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumAttendees = maximumAttendees;
        this.description = description;
        this.poster = poster;
        this.waitlist = new ArrayList<Entrant>();
        this.pendingInvites = new ArrayList<Entrant>();
        this.cancelledEntrants = new ArrayList<Entrant>();
        this.enrolledEntrants = new ArrayList<Entrant>();
        this.entrantLocations = new ArrayList<String>();
    }

    public Organizer getOwner() {
        return owner;
    }

    public void setOwner(Organizer owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getMaximumAttendees() {
        return maximumAttendees;
    }

    public void setMaximumAttendees(Integer maximumAttendees) {
        this.maximumAttendees = maximumAttendees;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public ArrayList<Entrant> getWaitlist() {
        return waitlist;
    }

    public void setWaitlist(ArrayList<Entrant> waitlist) {
        this.waitlist = waitlist;
    }

    public ArrayList<Entrant> getPendingInvites() {
        return pendingInvites;
    }

    public void setPendingInvites(ArrayList<Entrant> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    public ArrayList<Entrant> getCancelledEntrants() {
        return cancelledEntrants;
    }

    public void setCancelledEntrants(ArrayList<Entrant> cancelledEntrants) {
        this.cancelledEntrants = cancelledEntrants;
    }

    public ArrayList<Entrant> getEnrolledEntrants() {
        return enrolledEntrants;
    }

    public void setEnrolledEntrants(ArrayList<Entrant> enrolledEntrants) {
        this.enrolledEntrants = enrolledEntrants;
    }

    public ArrayList<String> getEntrantLocations() {
        return entrantLocations;
    }

    public void setEntrantLocations(ArrayList<String> entrantLocations) {
        this.entrantLocations = entrantLocations;
    }
}

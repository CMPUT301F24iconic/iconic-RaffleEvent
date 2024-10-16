package com.example.iconic_raffleevent;

import java.util.ArrayList;

public class Entrant extends User {
    private ArrayList<String> notiPref;
    private ArrayList<Event> joinedEvents;
    // This needs to be changed, Event type is placeholder
    private ArrayList<Event> eventInvites;
    private ArrayList<Notification> notifications;

    public Entrant(String name, String email, String deviceID) {
        super(name, email, deviceID);
        this.notiPref = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
        this.eventInvites = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public Entrant(String name, String email, String phoneNumber, String deviceID) {
        super(name, email, phoneNumber, deviceID);
        this.notiPref = new ArrayList<>();
        this.joinedEvents = new ArrayList<>();
        this.eventInvites = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public Event getJoinedEvent(Integer index) {
        return joinedEvents.get(index);
    }

    public ArrayList<Event> getJoinedEvents() {
        return joinedEvents;
    }

    public void addJoinedEvent(Event event) {
        this.joinedEvents.add(event);
    }

    public void removeJoinedEvent(Event event) {
        this.joinedEvents.remove(event);
    }

    public ArrayList<Event> getEventInvites() {
        return eventInvites;
    }

    public Event getEventInvite(Integer index) {
        return eventInvites.get(index);
    }

    public void addEventInvite(Event event) {
        this.eventInvites.add(event);
    }

    public void removeEventInvite(Event event) {
        this.eventInvites.remove(event);
    }

    public ArrayList<String> getNotificationPreferences() {
        return notiPref;
    }

    public String getNotificationPreference(Integer index) {
        return notiPref.get(index);
    }

    public void addNotificationPreference(String preference) {
        this.notiPref.add(preference);
    }

    public void removeNotificationPreference(String preference) {
        this.notiPref.remove(preference);
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public Notification getNotification(Integer index) {
        return notifications.get(index);
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
    }

    public void removeNotification(Notification notification) {
        this.notifications.remove(notification);
    }

    // setters

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setEventInvites(ArrayList<Event> eventInvites) {
        this.eventInvites = eventInvites;
    }

    public void setJoinedEvents(ArrayList<Event> joinedEvents) {
        this.joinedEvents = joinedEvents;
    }

    public void setNotificationPreferences(ArrayList<String> notiPref) {
        this.notiPref = notiPref;
    }
}

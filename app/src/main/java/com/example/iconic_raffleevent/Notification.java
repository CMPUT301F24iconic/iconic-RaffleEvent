package com.example.iconic_raffleevent;

public class Notification {
    private String message;
    private Organizer sender;
    private Event event;

    public Notification(String message, Organizer sender, Event event) {
        this.message = message;
        this.sender = sender;
        this.event = event;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setSender(Organizer sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public Event getEvent() {
        return event;
    }

    public Organizer getSender() {
        return sender;
    }
}

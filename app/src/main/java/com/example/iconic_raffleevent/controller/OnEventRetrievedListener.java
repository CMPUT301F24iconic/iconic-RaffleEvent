package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.Event;

/**
 * Interface for listening to event retrieval callbacks.
 *
 * This interface should be implemented by any class that wants to receive
 * notifications when an event has been retrieved from a data source.
 * Implementing classes must define the behavior of the
 * onEventRetrieved method to handle the retrieved event.
 */
public interface OnEventRetrievedListener {

    /**
     * Called when an event has been successfully retrieved.
     *
     * @param event The retrieved Event object. If no event is found,
     *              this parameter will be null
     */
    void onEventRetrieved(Event event);
}

package com.example.iconic_raffleevent.controller;

import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.model.WaitingList;

/**
 * WaitingListController manages the waiting list logic, including drawing entrants in a lottery system.
 * It interacts with the waiting list model and handles updates to the view.
 */
public class WaitingListController {

    private WaitingList waitingList;

    public WaitingListController(WaitingList waitingList) {
        this.waitingList = waitingList;
    }

    /**
     * Adds a user to the waiting list for an event.
     *
     * @param user The user to be added to the waiting list.
     */
    public void addToWaitingList(User user) {
        waitingList.addEntrant(user);
    }

    /**
     * Removes a user from the waiting list.
     *
     * @param user The user to be removed.
     */
    public void removeFromWaitingList(User user) {
        waitingList.removeEntrant(user);
    }

    /**
     * Draws a random user from the waiting list.
     *
     * @return The randomly selected user.
     */
    public User drawFromWaitingList() {
        return waitingList.drawEntrant();
    }
}
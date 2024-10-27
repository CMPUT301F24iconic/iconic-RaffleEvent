package com.example.iconic_raffleevent.model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Manages various participant lists for an event, categorizing participants based on their
 * invitation status and response.
 */
public class ParticipantLists {

    private Event event;                        // The event associated with these participant lists
    private Set<User> waitingList;              // Users who have shown interest in the event
    private Set<User> invitedList;              // Users who have been randomly invited
    private Set<User> confirmedList;            // Users who have accepted the invitation
    private Set<User> declinedList;             // Users who declined the invitation
    private Set<User> cancelledList;            // Users who were either not selected or who declined and opted out

    /**
     * Constructor that initializes the participant lists for a specific event.
     *
     * @param event The event for which these participant lists are created.
     */
    public ParticipantLists(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event is required to create participant lists.");
        }
        this.event = event;
        this.waitingList = new HashSet<>();
        this.invitedList = new HashSet<>();
        this.confirmedList = new HashSet<>();
        this.declinedList = new HashSet<>();
        this.cancelledList = new HashSet<>();
    }

    // Getters

    public Set<User> getWaitingList() {
        return waitingList;
    }

    public Set<User> getInvitedList() {
        return invitedList;
    }

    public Set<User> getConfirmedList() {
        return confirmedList;
    }

    public Set<User> getDeclinedList() {
        return declinedList;
    }

    public Set<User> getCancelledList() {
        return cancelledList;
    }

    // Participant list management

    /**
     * Adds a user to the waiting list if they are not already in another category.
     *
     * @param user The user to be added.
     * @return True if the user was successfully added to the waiting list.
     */
    public boolean addToWaitingList(User user) {
        if (user == null || isUserInAnyList(user)) {
            return false;
        }
        return waitingList.add(user);
    }

    /**
     * Randomly selects users from the waiting list to be invited.
     *
     * @param numberOfInvitations The number of users to invite.
     */
    public void inviteUsers(int numberOfInvitations) {
        if (numberOfInvitations <= 0 || numberOfInvitations > waitingList.size()) {
            throw new IllegalArgumentException("Invalid number of invitations.");
        }

        Random random = new Random();
        int invitations = 0;
        User[] waitingArray = waitingList.toArray(new User[0]);

        while (invitations < numberOfInvitations) {
            User selectedUser = waitingArray[random.nextInt(waitingArray.length)];
            if (waitingList.remove(selectedUser)) {
                invitedList.add(selectedUser);
                invitations++;
            }
        }
    }

    /**
     * Confirms the attendance of an invited user.
     *
     * @param user The user to be moved to the confirmed list.
     * @return True if the user was successfully confirmed.
     */
    public boolean confirmUser(User user) {
        if (user == null || !invitedList.remove(user)) {
            return false;
        }
        return confirmedList.add(user);
    }

    /**
     * Declines the invitation for an invited user, moving them to the declined list.
     *
     * @param user The user to be moved to the declined list.
     * @return True if the user was successfully declined.
     */
    public boolean declineUser(User user) {
        if (user == null || !invitedList.remove(user)) {
            return false;
        }
        return declinedList.add(user);
    }

    /**
     * Cancels the participation of a user not chosen in the lottery or one who declined the invitation.
     *
     * @param user The user to be moved to the cancelled list.
     * @return True if the user was successfully cancelled.
     */
    public boolean cancelUser(User user) {
        if (user == null) return false;
        if (waitingList.remove(user) || declinedList.remove(user)) {
            return cancelledList.add(user);
        }
        return false;
    }

    // Utility Methods

    /**
     * Checks if a user is present in any of the lists.
     *
     * @param user The user to check.
     * @return True if the user is present in any list, false otherwise.
     */
    public boolean isUserInAnyList(User user) {
        return waitingList.contains(user) || invitedList.contains(user) || confirmedList.contains(user) || declinedList.contains(user) || cancelledList.contains(user);
    }

    /**
     * Resets the participant lists, clearing all categories except confirmed attendees.
     * Useful for re-running the lottery for a new round of invites.
     */
    public void resetParticipantLists() {
        waitingList.clear();
        invitedList.clear();
        declinedList.clear();
        cancelledList.clear();
    }
}
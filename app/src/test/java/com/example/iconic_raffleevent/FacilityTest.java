package com.example.iconic_raffleevent;

import static org.junit.jupiter.api.Assertions.*;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacilityTest {

    private Facility facility;
    private User creator;

    @BeforeEach
    public void setup() {
        creator = new User("123", "Organizer Name");  // Assuming User class has this constructor
        facility = new Facility("Event Hall", "123 Main St", creator);
    }

    @Test
    public void testFacilityInitialization() {
        assertEquals("Event Hall", facility.getFacilityName());
        assertEquals("123 Main St", facility.getFacilityLocation());
        assertEquals(creator, facility.getCreator());
        assertEquals("", facility.getAdditionalInfo());
    }

    @Test
    public void testSetFacilityName() {
        facility.setFacilityName("New Name");
        assertEquals("New Name", facility.getFacilityName());
    }

    @Test
    public void testSetAdditionalInfo() {
        facility.setAdditionalInfo("Parking available");
        assertEquals("Parking available", facility.getAdditionalInfo());
    }

    @Test
    public void testSetCreator() {
        User newCreator = new User("456", "New Organizer");
        facility.setCreator(newCreator);
        assertEquals(newCreator, facility.getCreator());
    }
}

package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.Facility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FacilityTest {

    private Facility facility;

    @BeforeEach
    void setUp() {
        facility = new Facility("fac123", "Conference Hall", "Downtown", 100);
    }

    @Test
    void testGetId() {
        assertEquals("fac123", facility.getId());
    }

    @Test
    void testSetId() {
        facility.setId("fac456");
        assertEquals("fac456", facility.getId());
    }

    @Test
    void testGetName() {
        assertEquals("Conference Hall", facility.getName());
    }

    @Test
    void testSetName() {
        facility.setName("Banquet Hall");
        assertEquals("Banquet Hall", facility.getName());
    }

    @Test
    void testGetLocation() {
        assertEquals("Downtown", facility.getLocation());
    }

    @Test
    void testSetLocation() {
        facility.setLocation("Uptown");
        assertEquals("Uptown", facility.getLocation());
    }

    @Test
    void testGetCapacity() {
        assertEquals(100, facility.getCapacity());
    }

    @Test
    void testSetCapacity() {
        facility.setCapacity(150);
        assertEquals(150, facility.getCapacity());
    }

    @Test
    void testEquals() {
        Facility sameFacility = new Facility("fac123", "Conference Hall", "Downtown", 100);
        Facility differentFacility = new Facility("fac789", "Meeting Room", "Suburb", 50);
        assertEquals(facility, sameFacility);
        assertNotEquals(facility, differentFacility);
    }
}

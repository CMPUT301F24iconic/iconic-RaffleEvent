package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FacilityTest {

    private Facility facility;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        facility = new Facility("Conference Hall", "Downtown", user);
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
        assertEquals("Conference Hall", facility.getFacilityName());
    }

    @Test
    void testSetName() {
        facility.setFacilityName("Banquet Hall");
        assertEquals("Banquet Hall", facility.getFacilityName());
    }

    @Test
    void testGetLocation() {
        assertEquals("Downtown", facility.getFacilityName());
    }

    @Test
    void testSetLocation() {
        facility.setFacilityLocation("Uptown");
        assertEquals("Uptown", facility.getFacilityName());
    }

    //@Test
    //void testGetCapacity() {
       // assertEquals(100, facility.getCapacity());
    //}

   // @Test
    //void testSetCapacity() {
      //  facility.setCapacity(150);
        //assertEquals(150, facility.getCapacity());
    //}

    @Test
    void testEquals() {
        Facility sameFacility = new Facility("Conference Hall", "Downtown", user);
        Facility differentFacility = new Facility("Meeting Room", "Suburb", user);
        assertEquals(facility, sameFacility);
        assertNotEquals(facility, differentFacility);
    }
}

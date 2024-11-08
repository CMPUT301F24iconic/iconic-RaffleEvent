package com.example.iconic_raffleevent;

import static org.mockito.Mockito.*;

import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;

public class FacilityControllerTest {

    private FacilityController facilityController;
    private FirebaseOrganizer firebaseOrganizerMock;
    private Facility facility;

    @BeforeEach
    public void setup() {
        firebaseOrganizerMock = mock(FirebaseOrganizer.class);
        facilityController = new FacilityController();
        facilityController.firebaseOrganizer = firebaseOrganizerMock; // Inject mock FirebaseOrganizer

        User creator = new User("user123", "John Doe");
        facility = new Facility("Event Hall", "123 Main St", creator);
    }

    @Test
    public void testCreateFacility() {
        FacilityController.FacilityCreationCallback callback = mock(FacilityController.FacilityCreationCallback.class);

        facilityController.createFacility(facility, callback);

        ArgumentCaptor<FirebaseOrganizer.FacilityCreationCallback> captor = ArgumentCaptor.forClass(FirebaseOrganizer.FacilityCreationCallback.class);
        verify(firebaseOrganizerMock).createFacility(eq(facility), captor.capture());

        captor.getValue().onFacilityCreated("facility123");
        verify(callback).onFacilityCreated("facility123");
    }

    @Test
    public void testCheckUserFacility() {
        FacilityController.FacilityCheckCallback callback = mock(FacilityController.FacilityCheckCallback.class);

        facilityController.checkUserFacility("user123", callback);

        ArgumentCaptor<FirebaseOrganizer.FacilityCheckCallback> captor = ArgumentCaptor.forClass(FirebaseOrganizer.FacilityCheckCallback.class);
        verify(firebaseOrganizerMock).checkUserFacility(eq("user123"), captor.capture());

        captor.getValue().onFacilityExists("facility123");
        verify(callback).onFacilityExists("facility123");

        captor.getValue().onFacilityNotExists();
        verify(callback).onFacilityNotExists();
    }

    @Test
    public void testGetAllFacilities() {
        FacilityController.FacilityListCallback callback = mock(FacilityController.FacilityListCallback.class);

        facilityController.getAllFacilities(callback);

        ArgumentCaptor<FirebaseOrganizer.GetFacilitiesCallback> captor = ArgumentCaptor.forClass(FirebaseOrganizer.GetFacilitiesCallback.class);
        verify(firebaseOrganizerMock).getAllFacilities(captor.capture());

        ArrayList<Facility> facilities = new ArrayList<>();
        facilities.add(facility);
        captor.getValue().onFacilitiesFetched(facilities);
        verify(callback).onFacilitiesFetched(facilities);
    }

    @Test
    public void testDeleteFacility() {
        FacilityController.DeleteFacilityCallback callback = mock(FacilityController.DeleteFacilityCallback.class);

        facilityController.deleteFacility("facility123", callback);

        ArgumentCaptor<FirebaseOrganizer.DeleteFacilityCallback> captor = ArgumentCaptor.forClass(FirebaseOrganizer.DeleteFacilityCallback.class);
        verify(firebaseOrganizerMock).deleteFacility(eq("facility123"), captor.capture());

        captor.getValue().onSuccess();
        verify(callback).onSuccess();
    }
}

package com.example.iconic_raffleevent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class FacilityControllerTest {

    @Mock
    private FirebaseOrganizer mockFirebaseOrganizer;

    @Mock
    private FirebaseFirestore mockFirestore;

    private FacilityController facilityController;
    private Facility testFacility;
    private User testUser;

    @Before
    public void setup() {
        // Initialize Mockito annotations
        MockitoAnnotations.openMocks(this);

        // Mock Firebase initialization
        FirebaseApp mockFirebaseApp = mock(FirebaseApp.class);
        when(FirebaseApp.getInstance()).thenReturn(mockFirebaseApp);
        when(FirebaseFirestore.getInstance()).thenReturn(mockFirestore);

        // Create test user
        testUser = new User();
        testUser.setUserId("test-user-id");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");

        // Create test facility
        testFacility = new Facility("Test Facility", "123 Test St", testUser);
        testFacility.setId("test-facility-id");
        testFacility.setAdditionalInfo("Test facility details");

        // Initialize controller with mocked dependencies
        facilityController = new FacilityController();
        facilityController.firebaseOrganizer = mockFirebaseOrganizer;
    }

    @Test
    public void testCreateFacility_Success() {
        // Arrange
        FacilityController.FacilityCreationCallback callback = mock(FacilityController.FacilityCreationCallback.class);

        doAnswer(invocation -> {
            FirebaseOrganizer.FacilityCreationCallback orgCallback = invocation.getArgument(1);
            orgCallback.onFacilityCreated("test-facility-id");
            return null;
        }).when(mockFirebaseOrganizer).createFacility(eq(testFacility), any());

        // Act
        facilityController.createFacility(testFacility, callback);

        // Assert
        verify(callback).onFacilityCreated("test-facility-id");
        verify(callback, never()).onError(anyString());
    }

    @Test
    public void testCreateFacility_Error() {
        // Arrange
        FacilityController.FacilityCreationCallback callback = mock(FacilityController.FacilityCreationCallback.class);
        String errorMessage = "Failed to create facility";

        doAnswer(invocation -> {
            FirebaseOrganizer.FacilityCreationCallback orgCallback = invocation.getArgument(1);
            orgCallback.onError(errorMessage);
            return null;
        }).when(mockFirebaseOrganizer).createFacility(eq(testFacility), any());

        // Act
        facilityController.createFacility(testFacility, callback);

        // Assert
        verify(callback).onError(errorMessage);
        verify(callback, never()).onFacilityCreated(anyString());
    }

    @Test
    public void testGetAllFacilities_Success() {
        // Arrange
        FacilityController.FacilityListCallback callback = mock(FacilityController.FacilityListCallback.class);
        ArrayList<Facility> testFacilities = new ArrayList<>();
        testFacilities.add(testFacility);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback orgCallback = invocation.getArgument(0);
            orgCallback.onFacilitiesFetched(testFacilities);
            return null;
        }).when(mockFirebaseOrganizer).getAllFacilities(any());

        // Act
        facilityController.getAllFacilities(callback);

        // Assert
        verify(callback).onFacilitiesFetched(eq(testFacilities));
        verify(callback, never()).onError(anyString());
    }

    @Test
    public void testGetAllFacilities_Error() {
        // Arrange
        FacilityController.FacilityListCallback callback = mock(FacilityController.FacilityListCallback.class);
        String errorMessage = "Failed to fetch facilities";

        doAnswer(invocation -> {
            FirebaseOrganizer.GetFacilitiesCallback orgCallback = invocation.getArgument(0);
            orgCallback.onError(errorMessage);
            return null;
        }).when(mockFirebaseOrganizer).getAllFacilities(any());

        // Act
        facilityController.getAllFacilities(callback);

        // Assert
        verify(callback).onError(errorMessage);
        verify(callback, never()).onFacilitiesFetched(any());
    }

    @Test
    public void testUpdateFacility_Success() {
        // Arrange
        FacilityController.FacilityUpdateCallback callback = mock(FacilityController.FacilityUpdateCallback.class);

        doAnswer(invocation -> {
            FirebaseOrganizer.FacilityUpdateCallback orgCallback = invocation.getArgument(2);
            orgCallback.onFacilityUpdated();
            return null;
        }).when(mockFirebaseOrganizer).updateFacility(eq(testFacility.getId()), eq(testFacility), any());

        // Act
        facilityController.updateFacility(testFacility, callback);

        // Assert
        verify(callback).onFacilityUpdated();
        verify(callback, never()).onError(anyString());
    }

    @Test
    public void testUpdateFacility_Error() {
        // Arrange
        FacilityController.FacilityUpdateCallback callback = mock(FacilityController.FacilityUpdateCallback.class);
        String errorMessage = "Failed to update facility";

        doAnswer(invocation -> {
            FirebaseOrganizer.FacilityUpdateCallback orgCallback = invocation.getArgument(2);
            orgCallback.onError(errorMessage);
            return null;
        }).when(mockFirebaseOrganizer).updateFacility(eq(testFacility.getId()), eq(testFacility), any());

        // Act
        facilityController.updateFacility(testFacility, callback);

        // Assert
        verify(callback).onError(errorMessage);
        verify(callback, never()).onFacilityUpdated();
    }

    @Test
    public void testDeleteFacility_Success() {
        // Arrange
        FacilityController.DeleteFacilityCallback callback = mock(FacilityController.DeleteFacilityCallback.class);

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteFacilityCallback orgCallback = invocation.getArgument(1);
            orgCallback.onSuccess();
            return null;
        }).when(mockFirebaseOrganizer).deleteFacility(eq(testFacility.getId()), any());

        // Act
        facilityController.deleteFacility(testFacility.getId(), callback);

        // Assert
        verify(callback).onSuccess();
        verify(callback, never()).onError(anyString());
    }

    @Test
    public void testDeleteFacility_Error() {
        // Arrange
        FacilityController.DeleteFacilityCallback callback = mock(FacilityController.DeleteFacilityCallback.class);
        String errorMessage = "Failed to delete facility";

        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteFacilityCallback orgCallback = invocation.getArgument(1);
            orgCallback.onError(errorMessage);
            return null;
        }).when(mockFirebaseOrganizer).deleteFacility(eq(testFacility.getId()), any());

        // Act
        facilityController.deleteFacility(testFacility.getId(), callback);

        // Assert
        verify(callback).onError(errorMessage);
        verify(callback, never()).onSuccess();
    }

    @Test
    public void testCheckUserFacility_Exists() {
        // Arrange
        FacilityController.FacilityCheckCallback callback = mock(FacilityController.FacilityCheckCallback.class);

        doAnswer(invocation -> {
            FirebaseOrganizer.FacilityCheckCallback orgCallback = invocation.getArgument(1);
            orgCallback.onFacilityExists(testFacility.getId());
            return null;
        }).when(mockFirebaseOrganizer).checkUserFacility(eq(testUser.getUserId()), any());

        // Act
        facilityController.checkUserFacility(testUser.getUserId(), callback);

        // Assert
        verify(callback).onFacilityExists(testFacility.getId());
        verify(callback, never()).onFacilityNotExists();
        verify(callback, never()).onError(anyString());
    }
}
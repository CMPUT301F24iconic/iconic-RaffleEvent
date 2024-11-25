package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.CreateFacilityActivity;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.example.iconic_raffleevent.view.UserControllerViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class CreateFacilityActivityTest {

    private CreateFacilityActivity activity;
    private ActivityController<CreateFacilityActivity> activityController;

    @Mock
    private FacilityController facilityController;
    @Mock
    private UserController userController;
    @Mock
    private ViewModelProvider viewModelProvider;
    @Mock
    private UserControllerViewModel userControllerViewModel;
    @Mock
    private TextInputEditText facilityNameEditText;
    @Mock
    private TextInputEditText locationEditText;
    @Mock
    private TextInputEditText facilityDetailsEditText;
    @Mock
    private TextInputLayout facilityNameInputLayout;
    @Mock
    private TextInputLayout locationInputLayout;
    @Mock
    private TextInputLayout facilityDetailsInputLayout;
    @Mock
    private DrawerLayout drawerLayout;
    @Mock
    private NavigationView navigationView;
    @Mock
    private Button saveButton;

    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_FACILITY_NAME = "Test Facility";
    private static final String TEST_LOCATION = "Test Location";
    private static final String TEST_DETAILS = "Test Details";

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create Intent with required user ID
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateFacilityActivity.class);
        intent.putExtra("userId", TEST_USER_ID);

        // Initialize activity
        activityController = Robolectric.buildActivity(CreateFacilityActivity.class, intent);
        activity = activityController.create().start().resume().get();

        // Set up mocked TextInputEditText behavior
        setupMockEditTexts();

        // Set up mock controllers
        activity.facilityController = facilityController;
        activity.userController = userController;
    }

    private void setupMockEditTexts() {
        // Mock Editable objects
        Editable mockFacilityNameEditable = mock(Editable.class);
        Editable mockLocationEditable = mock(Editable.class);
        Editable mockDetailsEditable = mock(Editable.class);

        // Set up behavior for EditTexts
        when(facilityNameEditText.getText()).thenReturn(mockFacilityNameEditable);
        when(locationEditText.getText()).thenReturn(mockLocationEditable);
        when(facilityDetailsEditText.getText()).thenReturn(mockDetailsEditable);

        // Set up toString() behavior for Editables
        when(mockFacilityNameEditable.toString()).thenReturn(TEST_FACILITY_NAME);
        when(mockLocationEditable.toString()).thenReturn(TEST_LOCATION);
        when(mockDetailsEditable.toString()).thenReturn(TEST_DETAILS);
    }

    @Test
    public void testActivityCreation() {
        assertNotNull(activity);
    }

    @Test
    public void testUserIdMissing() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateFacilityActivity.class);
        CreateFacilityActivity activity = Robolectric.buildActivity(CreateFacilityActivity.class, intent)
                .create().get();

        assertEquals("Error: User ID is missing. Please try again.", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testSaveFacilitySuccess() {
        // Mock successful facility creation
        doAnswer(invocation -> {
            FacilityController.FacilityCreationCallback callback = invocation.getArgument(1);
            callback.onFacilityCreated("test_facility_id");
            return null;
        }).when(facilityController).createFacility(any(Facility.class), any(FacilityController.FacilityCreationCallback.class));

        // Simulate save button click
        activity.findViewById(R.id.saveButton).performClick();

        // Verify success toast
        assertEquals("Facility created successfully", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testSaveFacilityError() {
        // Mock facility creation error
        doAnswer(invocation -> {
            FacilityController.FacilityCreationCallback callback = invocation.getArgument(1);
            callback.onError("Test error");
            return null;
        }).when(facilityController).createFacility(any(Facility.class), any(FacilityController.FacilityCreationCallback.class));

        // Simulate save button click
        activity.findViewById(R.id.saveButton).performClick();

        // Verify error toast
        assertEquals("Error creating facility: Test error", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testValidateInputFields_AllFieldsEmpty() {
        // Set up empty fields
        when(facilityNameEditText.getText().toString()).thenReturn("");
        when(locationEditText.getText().toString()).thenReturn("");
        when(facilityDetailsEditText.getText().toString()).thenReturn("");

        activity.validateInputFields();

        // Verify error messages
        verify(facilityNameInputLayout).setError("Facility name cannot be empty");
        verify(locationInputLayout).setError("Location cannot be empty");
        verify(facilityDetailsInputLayout).setError("Facility details cannot be empty");
        assertTrue(activity.inputError);
    }

    @Test
    public void testValidateInputFields_AllFieldsValid() {
        // Set up valid fields
        when(facilityNameEditText.getText().toString()).thenReturn(TEST_FACILITY_NAME);
        when(locationEditText.getText().toString()).thenReturn(TEST_LOCATION);
        when(facilityDetailsEditText.getText().toString()).thenReturn(TEST_DETAILS);

        activity.validateInputFields();

        // Verify no errors
        verify(facilityNameInputLayout).setError(null);
        verify(locationInputLayout).setError(null);
        verify(facilityDetailsInputLayout).setError(null);
        assertFalse(activity.inputError);
    }

    @Test
    public void testLoadUserProfile() {
        User mockUser = new User();
        mockUser.setUserId(TEST_USER_ID);

        // Mock successful user fetch
        doAnswer(invocation -> {
            UserController.UserFetchCallback callback = invocation.getArgument(0);
            callback.onUserFetched(mockUser);
            return null;
        }).when(userController).getUserInformation(any(UserController.UserFetchCallback.class));

        activity.loadUserProfile();

        // Verify user was set and drawer was setup
        assertEquals(TEST_USER_ID, mockUser.getUserId());
    }

    @Test
    public void testLoadFacilityDetails() {
        Facility mockFacility = new Facility(TEST_FACILITY_NAME, TEST_LOCATION, new User());
        mockFacility.setAdditionalInfo(TEST_DETAILS);

        // Mock successful facility fetch
        doAnswer(invocation -> {
            FacilityController.FacilityFetchCallback callback = invocation.getArgument(1);
            callback.onFacilityFetched(mockFacility);
            return null;
        }).when(facilityController).getFacilityByUserId(anyString(), any(FacilityController.FacilityFetchCallback.class));

        activity.loadFacilityDetails();

        // Verify facility details were loaded
        verify(facilityNameEditText).setText(TEST_FACILITY_NAME);
        verify(locationEditText).setText(TEST_LOCATION);
        verify(facilityDetailsEditText).setText(TEST_DETAILS);
        verify(saveButton).setText("Update");
    }

    @Test
    public void testNavigationButtons() {
        // Test home button
        activity.findViewById(R.id.home_button).performClick();
        Intent expectedHomeIntent = new Intent(activity, EventListActivity.class);
        assertEquals(expectedHomeIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test QR button
        activity.findViewById(R.id.qr_button).performClick();
        Intent expectedQrIntent = new Intent(activity, QRScannerActivity.class);
        assertEquals(expectedQrIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test profile button
        activity.findViewById(R.id.profile_button).performClick();
        Intent expectedProfileIntent = new Intent(activity, ProfileActivity.class);
        assertEquals(expectedProfileIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());

        // Test notification button
        activity.findViewById(R.id.notification_icon).performClick();
        Intent expectedNotificationIntent = new Intent(activity, NotificationsActivity.class);
        assertEquals(expectedNotificationIntent.getComponent(), shadowOf(activity).getNextStartedActivity().getComponent());
    }

    @Test
    public void testMenuButtonOpenDrawer() {
        activity.findViewById(R.id.menu_button).performClick();
        verify(drawerLayout).openDrawer(GravityCompat.START);
    }

    @Test
    public void testUpdateExistingFacility() {
        // Set up existing facility
        Facility mockFacility = new Facility(TEST_FACILITY_NAME, TEST_LOCATION, new User());
        activity.currentFacility = mockFacility;

        // Mock successful facility update
        doAnswer(invocation -> {
            FacilityController.FacilityUpdateCallback callback = invocation.getArgument(1);
            callback.onFacilityUpdated();
            return null;
        }).when(facilityController).updateFacility(any(Facility.class), any(FacilityController.FacilityUpdateCallback.class));

        // Simulate save button click
        activity.findViewById(R.id.saveButton).performClick();

        // Verify success toast
        assertEquals("Facility updated successfully", ShadowToast.getTextOfLatestToast());
    }

    @Test
    public void testUpdateFacilityError() {
        // Set up existing facility
        Facility mockFacility = new Facility(TEST_FACILITY_NAME, TEST_LOCATION, new User());
        activity.currentFacility = mockFacility;

        // Mock facility update error
        doAnswer(invocation -> {
            FacilityController.FacilityUpdateCallback callback = invocation.getArgument(1);
            callback.onError("Test error");
            return null;
        }).when(facilityController).updateFacility(any(Facility.class), any(FacilityController.FacilityUpdateCallback.class));

        // Simulate save button click
        activity.findViewById(R.id.saveButton).performClick();

        // Verify error toast
        assertEquals("Error updating facility: Test error", ShadowToast.getTextOfLatestToast());
    }
}
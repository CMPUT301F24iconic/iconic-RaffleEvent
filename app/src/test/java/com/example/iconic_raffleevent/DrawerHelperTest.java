package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.test.core.app.ApplicationProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.view.CreateEventActivity;
import com.example.iconic_raffleevent.view.CreateFacilityActivity;
import com.example.iconic_raffleevent.view.DrawerHelper;
import com.example.iconic_raffleevent.view.EventListActivity;
import com.example.iconic_raffleevent.view.NotificationsActivity;
import com.example.iconic_raffleevent.view.ProfileActivity;
import com.example.iconic_raffleevent.view.QRScannerActivity;
import com.google.android.material.navigation.NavigationView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

@RunWith(RobolectricTestRunner.class)
public class DrawerHelperTest {

    @Mock
    private Context mockContext;
    @Mock
    private DrawerLayout mockDrawerLayout;
    @Mock
    private NavigationView mockNavigationView;
    @Mock
    private MenuItem mockMenuItem;
    @Mock
    private FacilityController mockFacilityController;

    private static final String TEST_USER_ID = "test_user_id";
    private static final String TEST_FACILITY_ID = "test_facility_id";

    private ArgumentCaptor<NavigationView.OnNavigationItemSelectedListener> listenerCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        listenerCaptor = ArgumentCaptor.forClass(NavigationView.OnNavigationItemSelectedListener.class);
    }

    @Test
    public void testSetupDrawer_Profile() {
        // Setup
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_profile);
        simulateDrawerSetup();

        // Trigger navigation item selected
        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        // Verify intent and drawer closing
        verifyIntentStarted(ProfileActivity.class);
        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_Events() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_events);
        simulateDrawerSetup();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        verifyIntentStarted(EventListActivity.class);
        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_Notifications() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_notifications);
        simulateDrawerSetup();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        verifyIntentStarted(NotificationsActivity.class);
        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_ScanQR() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_scan_qr);
        simulateDrawerSetup();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        verifyIntentStarted(QRScannerActivity.class);
        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_Facility() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_facility);
        simulateDrawerSetup();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        // Verify intent with user ID extra
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(intentCaptor.capture());
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(CreateFacilityActivity.class.getName(), capturedIntent.getComponent().getClassName());
        assertEquals(TEST_USER_ID, capturedIntent.getStringExtra("userId"));

        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_CreateEvent_FacilityExists() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_create_event);
        simulateDrawerSetup();

        // Mock facility controller to return existing facility
        mockFacilityExistsResponse();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        // Verify intent with facility ID extra
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(intentCaptor.capture());
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(CreateEventActivity.class.getName(), capturedIntent.getComponent().getClassName());
        assertEquals(TEST_FACILITY_ID, capturedIntent.getStringExtra("facilityId"));

        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_CreateEvent_NoFacility() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_create_event);
        simulateDrawerSetup();

        // Mock facility controller to return no facility
        mockNoFacilityResponse();

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        // Verify toast message
        assertEquals("You must create a facility before creating an event.",
                ShadowToast.getTextOfLatestToast());

        // Verify redirect to Create Facility
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(intentCaptor.capture());
        Intent capturedIntent = intentCaptor.getValue();
        assertEquals(CreateFacilityActivity.class.getName(), capturedIntent.getComponent().getClassName());
        assertEquals(TEST_USER_ID, capturedIntent.getStringExtra("userId"));

        verifyDrawerClosed();
    }

    @Test
    public void testSetupDrawer_CreateEvent_Error() {
        when(mockMenuItem.getItemId()).thenReturn(R.id.nav_create_event);
        simulateDrawerSetup();

        // Mock facility controller to return error
        mockFacilityErrorResponse("Test error");

        listenerCaptor.getValue().onNavigationItemSelected(mockMenuItem);

        // Verify error toast
        assertEquals("Error checking facility: Test error",
                ShadowToast.getTextOfLatestToast());

        verifyDrawerClosed();
    }

    private void simulateDrawerSetup() {
        DrawerHelper.setupDrawer(mockContext, mockDrawerLayout, mockNavigationView, TEST_USER_ID);
        verify(mockNavigationView).setNavigationItemSelectedListener(listenerCaptor.capture());
    }

    private void verifyIntentStarted(Class<?> activityClass) {
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(mockContext).startActivity(intentCaptor.capture());
        assertEquals(activityClass.getName(), intentCaptor.getValue().getComponent().getClassName());
    }

    private void verifyDrawerClosed() {
        verify(mockDrawerLayout).closeDrawer(GravityCompat.START);
    }

    private void mockFacilityExistsResponse() {
        doAnswer(invocation -> {
            FacilityController.FacilityCheckCallback callback = invocation.getArgument(1);
            callback.onFacilityExists(TEST_FACILITY_ID);
            return null;
        }).when(mockFacilityController).checkUserFacility(eq(TEST_USER_ID), any(FacilityController.FacilityCheckCallback.class));
    }

    private void mockNoFacilityResponse() {
        doAnswer(invocation -> {
            FacilityController.FacilityCheckCallback callback = invocation.getArgument(1);
            callback.onFacilityNotExists();
            return null;
        }).when(mockFacilityController).checkUserFacility(eq(TEST_USER_ID), any(FacilityController.FacilityCheckCallback.class));
    }

    private void mockFacilityErrorResponse(String errorMessage) {
        doAnswer(invocation -> {
            FacilityController.FacilityCheckCallback callback = invocation.getArgument(1);
            callback.onError(errorMessage);
            return null;
        }).when(mockFacilityController).checkUserFacility(eq(TEST_USER_ID), any(FacilityController.FacilityCheckCallback.class));
    }
}
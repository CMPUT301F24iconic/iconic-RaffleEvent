package com.example.iconic_raffleevent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.view.FacilityAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@Config(manifest = Config.NONE)
public class FacilityAdapterTest {

    private Context context;
    private FacilityAdapter adapter;
    private ArrayList<Facility> facilities;
    private Facility testFacility1;
    private Facility testFacility2;

    @Before
    public void setUp() {
        // Initialize context
        context = ApplicationProvider.getApplicationContext();

        // Create test user
        User testUser = new User();
        testUser.setUserId("test-user-id");
        testUser.setName("Test User");

        // Create test facilities
        testFacility1 = new Facility("Test Facility 1", "123 Test St", testUser);
        testFacility1.setId("facility-1");

        testFacility2 = new Facility("Test Facility 2", "456 Test Ave", testUser);
        testFacility2.setId("facility-2");

        // Initialize facilities list
        facilities = new ArrayList<>();
        facilities.add(testFacility1);
        facilities.add(testFacility2);

        // Create adapter
        //adapter = new FacilityAdapter(context, facilities);
    }

    /*
    @Test
    public void testAdapterCreation() {
        assertNotNull("Adapter should not be null", adapter);
        assertEquals("Adapter should have 2 items", 2, adapter.getCount());
    }

    @Test
    public void testGetItem() {
        Facility facility = adapter.getItem(0);
        assertNotNull("Retrieved facility should not be null", facility);
        assertEquals("Facility name should match", testFacility1.getFacilityName(), facility.getFacilityName());
        assertEquals("Facility location should match", testFacility1.getFacilityLocation(), facility.getFacilityLocation());
    }

    @Test
    public void testGetView_NewView() {
        // Get view from adapter
        View view = adapter.getView(0, null, null);

        assertNotNull("View should not be null", view);

        // Get TextViews from view
        TextView nameTextView = view.findViewById(R.id.facility_name);
        TextView locationTextView = view.findViewById(R.id.facility_location);

        assertNotNull("Name TextView should not be null", nameTextView);
        assertNotNull("Location TextView should not be null", locationTextView);

        // Verify text content
        assertEquals("Facility name should match",
                testFacility1.getFacilityName(), nameTextView.getText().toString());
        assertEquals("Facility location should match",
                testFacility1.getFacilityLocation(), locationTextView.getText().toString());
    }

    @Test
    public void testGetView_ReuseView() {
        // Create mock View
        View convertView = LayoutInflater.from(context)
                .inflate(R.layout.item_facility, null, false);

        // Get view from adapter with reused view
        View view = adapter.getView(1, convertView, null);

        assertNotNull("View should not be null", view);

        // Get TextViews from view
        TextView nameTextView = view.findViewById(R.id.facility_name);
        TextView locationTextView = view.findViewById(R.id.facility_location);

        // Verify text content for second facility
        assertEquals("Facility name should match",
                testFacility2.getFacilityName(), nameTextView.getText().toString());
        assertEquals("Facility location should match",
                testFacility2.getFacilityLocation(), locationTextView.getText().toString());
    }

    @Test
    public void testEmptyAdapter() {
        // Create empty adapter
        FacilityAdapter emptyAdapter = new FacilityAdapter(context, new ArrayList<>());
        assertEquals("Empty adapter should have 0 items", 0, emptyAdapter.getCount());
    }

    @Test
    public void testAddFacility() {
        // Create new facility
        User testUser = new User();
        Facility newFacility = new Facility("New Facility", "789 Test Blvd", testUser);

        // Add to adapter
        adapter.add(newFacility);

        assertEquals("Adapter should have 3 items", 3, adapter.getCount());
        assertEquals("New facility should be at position 2",
                newFacility.getFacilityName(), adapter.getItem(2).getFacilityName());
    }

    @Test
    public void testRemoveFacility() {
        // Remove first facility
        adapter.remove(testFacility1);

        assertEquals("Adapter should have 1 item", 1, adapter.getCount());
        assertEquals("Remaining facility should be facility 2",
                testFacility2.getFacilityName(), adapter.getItem(0).getFacilityName());
    }

    @Test
    public void testClear() {
        // Clear adapter
        adapter.clear();
        assertEquals("Adapter should have 0 items", 0, adapter.getCount());
    }

     */
}
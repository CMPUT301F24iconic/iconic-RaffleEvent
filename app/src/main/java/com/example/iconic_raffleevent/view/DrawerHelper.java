package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.google.android.material.navigation.NavigationView;

/**
 * Helper class for setting up and managing a navigation drawer in the application.
 * This class provides a method to initialize the drawer with menu items that
 * navigate to different activities within the app.
 */
public class DrawerHelper {
    /**
     * Sets up the navigation drawer with menu item selections, allowing users to
     * navigate to different activities within the app based on the item they select.
     *
     * Each menu item in the NavigationView is assigned an action
     * to start a specific activity. The drawer will close after an item is selected.</p>
     *
     * @param context The context in which the drawer is being set up, typically an
     *                Activity context.
     * @param drawerLayout The DrawerLayout containing the navigation view
     *                     and other content.
     * @param navigationView The NavigationView containing menu items for navigation.
     * @param userId The ID of the currently logged-in user to pass to activities.
     */
    public static void setupDrawer(Context context, DrawerLayout drawerLayout, NavigationView navigationView, String userId) {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                context.startActivity(new Intent(context, ProfileActivity.class));
            } else if (id == R.id.nav_events) {
                context.startActivity(new Intent(context, EventListActivity.class));
            } else if (id == R.id.nav_notifications) {
                context.startActivity(new Intent(context, NotificationsActivity.class));
            } else if (id == R.id.nav_scan_qr) {
                context.startActivity(new Intent(context, QRScannerActivity.class));
            } else if (id == R.id.nav_facility) {
                Intent intent = new Intent(context, CreateFacilityActivity.class);
                intent.putExtra("userId", userId); // Pass the userId from the DrawerHelper setup
                context.startActivity(intent);
            } else if (id == R.id.nav_create_event) {
                // Check if the user has a facility before redirecting
                FacilityController facilityController = new FacilityController();
                facilityController.checkUserFacility(userId, new FacilityController.FacilityCheckCallback() {
                    @Override
                    public void onFacilityExists(String facilityId) {
                        // Redirect to Create Event page with the facility ID
                        Intent intent = new Intent(context, CreateEventActivity.class);
                        intent.putExtra("facilityId", facilityId); // Pass the facility ID
                        context.startActivity(intent);
                    }

                    @Override
                    public void onFacilityNotExists() {
                        // Redirect to Create Facility page if no facility exists
                        Toast.makeText(context, "You must create a facility before creating an event.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(context, CreateFacilityActivity.class);
                        intent.putExtra("userId", userId); // Pass the user ID
                        context.startActivity(intent);
                    }

                    @Override
                    public void onError(String message) {
                        Toast.makeText(context, "Error checking facility: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}

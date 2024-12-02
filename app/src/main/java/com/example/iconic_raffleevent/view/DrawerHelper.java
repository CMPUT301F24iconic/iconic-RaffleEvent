package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.AvatarGenerator;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;

/**
 * Helper class for setting up and managing the navigation drawer in the application.
 */
public class DrawerHelper {

    /**
     * Sets up the navigation drawer.
     *
     * @param context      The context of the calling activity.
     * @param drawerLayout The DrawerLayout containing the navigation menu.
     * @param userId       The ID of the currently logged-in user.
     */
    public static void setupDrawer(Context context, DrawerLayout drawerLayout, String userId) {
        NavigationView navigationView = drawerLayout.findViewById(R.id.navigation_view);

        UserController userController = new UserController(userId, context);
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    setupProfilePhoto(context, navigationView, drawerLayout, user);
                    setupAdminPanel(context, navigationView, drawerLayout, user);
                } else {
                    Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(context, "Error fetching user data: " + message, Toast.LENGTH_SHORT).show();
            }
        });

        setupMenuButtons(context, navigationView, drawerLayout, userId);
    }

    /**
     * Sets up the profile photo section in the drawer.
     *
     * @param context        The context of the calling activity.
     * @param navigationView The NavigationView containing the navigation menu.
     * @param drawerLayout   The DrawerLayout for closing the drawer.
     * @param user           The current user's data.
     */
    private static void setupProfilePhoto(Context context, NavigationView navigationView, DrawerLayout drawerLayout, User user) {
        System.out.println("Setting up profile photo");
        ImageView profilePhoto = navigationView.findViewById(R.id.nav_profile_photo);

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            System.out.println("Fetching photo from url");
            // Load profile photo using Glide
            Glide.with(context)
                    .load(user.getProfileImageUrl())
                    .circleCrop()
                    .error(R.drawable.default_profile)
                    .into(profilePhoto);
        } else if (user.getName() != null && !user.getName().isEmpty()) {
            // Generate an avatar if profile image URL is unavailable
            Bitmap avatarBitmap = AvatarGenerator.generateAvatar(user.getName(), 200);
            profilePhoto.setImageBitmap(avatarBitmap);
        } else {
            // Fallback to a default profile image
            profilePhoto.setImageResource(R.drawable.default_profile);
        }

        // Navigate to ProfileActivity when profile photo is clicked
        profilePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("userId", user.getUserId());
            context.startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    /**
     * Sets up the admin panel if the user has admin privileges.
     *
     * @param context        The context of the calling activity.
     * @param navigationView The NavigationView containing the navigation menu.
     * @param drawerLayout   The DrawerLayout for closing the drawer.
     * @param user           The current user's data.
     */
    private static void setupAdminPanel(Context context, NavigationView navigationView, DrawerLayout drawerLayout, User user) {
        System.out.println("Setting up admin");
        TextView adminPanelHeader = navigationView.findViewById(R.id.admin_panel_header);
        TextView manageUsersButton = navigationView.findViewById(R.id.admin_manage_users);
        TextView manageEventsButton = navigationView.findViewById(R.id.admin_manage_events);
        TextView manageQrCodesButton = navigationView.findViewById(R.id.admin_manage_qr_codes);
        TextView manageFacilitiesButton = navigationView.findViewById(R.id.admin_manage_facilities);

        if (user.getRoles() != null && user.getRoles().contains("admin")) {
            System.out.println("User is Admin");
            // Show admin panel
            adminPanelHeader.setVisibility(View.VISIBLE);
            manageUsersButton.setVisibility(View.VISIBLE);
            manageEventsButton.setVisibility(View.VISIBLE);
            manageQrCodesButton.setVisibility(View.VISIBLE);
            manageFacilitiesButton.setVisibility(View.VISIBLE);

            // Set click listeners for admin buttons
            manageUsersButton.setOnClickListener(v -> {
                context.startActivity(new Intent(context, UserListActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

            manageEventsButton.setOnClickListener(v -> {
                context.startActivity(new Intent(context, EventListForAdminActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

            manageQrCodesButton.setOnClickListener(v -> {
                context.startActivity(new Intent(context, QRCodeGalleryActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });

            manageFacilitiesButton.setOnClickListener(v -> {
                context.startActivity(new Intent(context, FacilityListForAdminActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        } else {
            System.out.println("User does not have admin privileges");
        }
    }

    /**
     * Sets up the menu buttons for "Create Event" and "Facility".
     *
     * @param context        The context of the calling activity.
     * @param navigationView The NavigationView containing the navigation menu.
     * @param drawerLayout   The DrawerLayout for closing the drawer.
     * @param userId         The ID of the currently logged-in user.
     */
    private static void setupMenuButtons(Context context, NavigationView navigationView, DrawerLayout drawerLayout, String userId) {
        TextView createEventButton = navigationView.findViewById(R.id.nav_create_event);
        TextView facilityButton = navigationView.findViewById(R.id.nav_facility);

        FacilityController facilityController = new FacilityController();

        // Handle Create Event button logic
        createEventButton.setOnClickListener(v -> {
            facilityController.checkUserFacility(userId, new FacilityController.FacilityCheckCallback() {
                @Override
                public void onFacilityExists(String facilityId) {
                    // Redirect to Create Event page with the facility ID
                    Intent intent = new Intent(context, CreateEventActivity.class);
                    intent.putExtra("facilityId", facilityId); // Pass the facility ID
                    intent.putExtra("userId", userId);         // Pass the user ID
                    context.startActivity(intent);
                }

                @Override
                public void onFacilityNotExists() {
                    // Redirect to Create Facility page if no facility exists
                    Toast.makeText(context, "You must create a facility before creating an event.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, CreateFacilityActivity.class);
                    intent.putExtra("userId", userId);         // Pass the user ID
                    intent.putExtra("fromCreateEvent", true); // Indicate navigation from Create Event
                    context.startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(context, "Error checking facility: " + message, Toast.LENGTH_SHORT).show();
                }
            });
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        // Handle Create Facility button logic
        facilityButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateFacilityActivity.class);
            intent.putExtra("userId", userId);              // Pass the user ID
            intent.putExtra("fromCreateEvent", false);      // Indicate direct facility creation
            context.startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }
}

package com.example.iconic_raffleevent.view;

import android.content.Context;
import android.content.Intent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.iconic_raffleevent.R;
import com.google.android.material.navigation.NavigationView;

public class DrawerHelper {

    public static void setupDrawer(Context context, DrawerLayout drawerLayout, NavigationView navigationView) {
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
                context.startActivity(new Intent(context, CreateFacilityActivity.class));
            } else if (id == R.id.nav_create_event) {
                context.startActivity(new Intent(context, QRScannerActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}

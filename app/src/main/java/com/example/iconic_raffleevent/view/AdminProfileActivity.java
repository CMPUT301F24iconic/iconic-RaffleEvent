package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.User;

import java.util.ArrayList;

/**
 * AdminProfileActivity provides an interface for administrators to manage user profiles within the application.
 * This activity displays a list of user profiles fetched from Firebase and allows administrators to delete profiles.
 */
public class AdminProfileActivity extends AppCompatActivity {
    private ListView profileListView;
    private ArrayAdapter<String> profileAdapter;
    private ArrayList<User> userList;
    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Called when the activity is created. Sets up the layout and initializes the user profile list.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        profileListView = findViewById(R.id.profile_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadUserList();
    }

    /**
     * Loads the list of user profiles from Firebase and populates the ListView with user profile names.
     * Sets up a click listener for each item in the list to allow for profile deletion.
     */
    private void loadUserList() {
        firebaseOrganizer.getAllUsers(new FirebaseOrganizer.GetUsersCallback() {
            @Override
            public void onUsersFetched(ArrayList<User> users) {
                userList = users;
                ArrayList<String> userNames = new ArrayList<>();
                for (User user : users) {
                    userNames.add(user.getUsername());
                }
                profileAdapter = new ArrayAdapter<>(AdminProfileActivity.this, android.R.layout.simple_list_item_1, userNames);
                profileListView.setAdapter(profileAdapter);
                profileListView.setOnItemClickListener((adapterView, view, i, l) -> showDeleteDialog(userList.get(i)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminProfileActivity.this, "Error loading profiles: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog for deleting a selected user profile.
     *
     * @param user The User object representing the profile to be deleted.
     */
    private void showDeleteDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the specified user profile from Firebase and refreshes the profile list on success.
     *
     * @param user The User object representing the profile to delete.
     */
    private void deleteUser(User user) {
        firebaseOrganizer.deleteUser(user.getUserId(), new FirebaseOrganizer.DeleteUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminProfileActivity.this, "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                loadUserList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminProfileActivity.this, "Error deleting profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

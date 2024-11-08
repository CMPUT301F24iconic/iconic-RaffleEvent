package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

public class ProfileManagementActivity extends AppCompatActivity {

    private ListView profileListView;
    private UserController userController;
    private ArrayList<User> userList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_management);  // Ensure this layout file exists

        profileListView = findViewById(R.id.profile_list_view);

        // Initialize UserController with context
        userController = new UserController(getUserID(), this);

        loadProfileList();
    }

    private void loadProfileList() {
        userController.getAllUsers(new UserController.UserListCallback() {
            @Override
            public void onUsersFetched(ArrayList<User> users) {
                userList = users;
                userNames = new ArrayList<>();

                for (User user : userList) {
                    userNames.add(user.getUsername());
                }

                adapter = new ArrayAdapter<>(ProfileManagementActivity.this, android.R.layout.simple_list_item_1, userNames);
                profileListView.setAdapter(adapter);

                profileListView.setOnItemClickListener((parent, view, position, id) -> showProfileOptionsDialog(userList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProfileManagementActivity.this, "Error loading profiles: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProfileOptionsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Profile")
                .setMessage("Would you like to remove this profile?")
                .setPositiveButton("Remove", (dialog, which) -> removeProfile(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeProfile(User user) {
        userController.deleteUser(user.getUserId(), new UserController.DeleteUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ProfileManagementActivity.this, "Profile removed successfully", Toast.LENGTH_SHORT).show();
                loadProfileList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProfileManagementActivity.this, "Failed to remove profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserID() {
        return android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }
}

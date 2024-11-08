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

public class AdminProfileActivity extends AppCompatActivity {

    private ListView profileListView;
    private ArrayAdapter<String> profileAdapter;
    private ArrayList<User> userList;
    private FirebaseOrganizer firebaseOrganizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        profileListView = findViewById(R.id.profile_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadUserList();
    }

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

    private void showDeleteDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

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
package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

/**
 * Activity that displays a list of all users in the app and provides management options for each user.
 */
public class UserListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private UserController userController;

    /**
     * Called when the activity is created. Sets up the layout and initializes the components.
     *
     * @param savedInstanceState the saved state of the activity if it was previously terminated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        // Initialize RecyclerView and UserAdapter
        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(new ArrayList<>());

        userRecyclerView.setAdapter(userAdapter);

        // Initialize UserController with userID and context
        String userID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        userController = new UserController(userID, this);

        // Set item click listener to show user details and delete options
        userAdapter.setOnItemClickListener(user -> {
            EventListUtils.showUserDetailsDialog(
                    UserListActivity.this,
                    user,
                    null, // No eventObj needed since this is a global user list
                    null, // No FirebaseAttendee needed
                    this::refreshUserList // Callback to refresh the list after deletion
            );
        });

        // Load and display all users
        loadUserList();
    }

    /**
     * Refreshes the user list by clearing the adapter and reloading the data.
     */
    private void refreshUserList() {
        userAdapter.clearUsers();
        loadUserList();
    }

    /**
     * Loads the list of all users in the app using the UserController.
     * Updates the RecyclerView with the fetched users.
     */
    private void loadUserList() {
        userController.getAllUsers(new UserController.UserListCallback() {
            @Override
            public void onUsersFetched(ArrayList<User> users) {
                userAdapter.addUsers(users); // Add all users to the adapter
            }

            @Override
            public void onError(String message) {
                Toast.makeText(UserListActivity.this, "Error loading users: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

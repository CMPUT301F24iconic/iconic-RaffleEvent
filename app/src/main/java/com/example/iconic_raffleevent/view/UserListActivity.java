package com.example.iconic_raffleevent.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import java.util.ArrayList;

/**
 * Activity that displays a list of users and provides options for managing each user.
 * Users are fetched from a UserController, displayed in a ListView,
 * and can be removed with a confirmation dialog.
 */
public class UserListActivity extends AppCompatActivity {
    private ListView userListView;
    private UserController userController;
    private ArrayList<User> userList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userNames;

    /**
     * Called when the activity is starting. Sets up the layout and initializes components.
     *
     * @param savedInstanceState the saved state of the activity if it was previously terminated.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.user_list_view);

        initializeController();
        loadUserList();
    }

    /**
     * Initializes the UserController by obtaining it from the ViewModel.
     * Ensures the controller is only created once and retained across configuration changes.
     */
    private void initializeController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    /**
     * Retrieves the unique device ID to use as the user ID.
     *
     * @return the Android device ID as a String.
     */
    @SuppressLint("HardwareIds")
    private String getUserID() {
        return android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    /**
     * Loads the list of users from the UserController.
     * Sets up the ListView adapter and click listener for displaying options for each user.
     */
    private void loadUserList() {
        userController.getAllUsers(new UserController.UserListCallback() {
            @Override
            public void onUsersFetched(ArrayList<User> users) {
                userList = users;
                userNames = new ArrayList<>();
                for (User user : userList) {
                    userNames.add(user.getUsername());
                }
                adapter = new ArrayAdapter<>(UserListActivity.this, android.R.layout.simple_list_item_1, userNames);
                userListView.setAdapter(adapter);

                userListView.setOnItemClickListener((parent, view, position, id) -> showUserOptionsDialog(userList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(UserListActivity.this, "Error loading users: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Displays an alert dialog to confirm the removal of a user.
     *
     * @param user the User object representing the user to be managed.
     */
    private void showUserOptionsDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Manage User")
                .setMessage("Would you like to remove this user?")
                .setPositiveButton("Remove", (dialog, which) -> removeUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Removes a specified user by calling the UserController.
     * Displays a success message on successful removal or an error message otherwise.
     *
     * @param user the User object to be removed.
     */
    private void removeUser(User user) {
        userController.deleteUser(user.getUserId(), new UserController.DeleteUserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(UserListActivity.this, "User removed successfully", Toast.LENGTH_SHORT).show();
                loadUserList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(UserListActivity.this, "Failed to remove user: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

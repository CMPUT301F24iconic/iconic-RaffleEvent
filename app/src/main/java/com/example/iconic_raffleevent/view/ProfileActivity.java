package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.example.iconic_raffleevent.AvatarGenerator;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Switch notificationsSwitch;
    private Button saveButton;
    private Button removePhotoButton;
    private Button backButton;

    // Aiden Teal
    private User userObj;

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        saveButton = findViewById(R.id.save_button);
        removePhotoButton = findViewById(R.id.remove_photo_button);
        backButton = findViewById(R.id.back_to_hub_button);

        //User currentUser = getCurrentUser();
        //userController = new UserController(currentUser);

        // Aiden Teal
        userController = getUserController();

        loadUserProfile();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNo = phoneEditText.getText().toString().trim();
            boolean notificationsEnabled = notificationsSwitch.isChecked();

            userController.updateProfile(userObj, name, email, phoneNo);
            userController.setNotificationsEnabled(userObj, notificationsEnabled);
        });

        profileImageView.setOnClickListener(v -> {
            // Open image picker or camera to select profile image
            // Upload the selected image to Firebase Storage
            // Get the download URL of the uploaded image
            String imageUrl = ""; // Replace with the actual download URL
            // userController.uploadProfileImage(imageUrl);

            // Aiden Teal code
            userController.uploadProfileImageTest(userObj, imageUrl);
        });

        removePhotoButton.setOnClickListener(v -> {
            //userController.removeProfileImage();

            // Aiden Teal code
            userController.removeProfileImage(userObj);

            loadUserProfile();
        });

        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HubActivity.class));
        });
    }

    private void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    nameEditText.setText(user.getName());
                    emailEditText.setText(user.getEmail());
                    phoneEditText.setText(user.getPhoneNo());
                    notificationsSwitch.setChecked(user.isNotificationsEnabled());
                    String profileImageUrl = user.getProfileImageUrl();
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(profileImageUrl).into(profileImageView);
                    } else {
                        // Generate avatar image based on profile name
                        Bitmap avatar = AvatarGenerator.generateAvatar(user.getUsername(), 200);
                        profileImageView.setImageBitmap(avatar);
                    }
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Cannot fetch user information");
            }
        });
    }

    /*
    Aiden Teal function to get userID
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }


    /*
    private void loadUserProfile() {
        User user = userController.getCurrentUser();
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNo());
        notificationsSwitch.setChecked(user.isNotificationsEnabled());
        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(ProfileActivity.this).load(profileImageUrl).into(profileImageView);
        } else {
            // Generate avatar image based on profile name
            Bitmap avatar = AvatarGenerator.generateAvatar(user.getName(), 200);
            profileImageView.setImageBitmap(avatar);
        }
       }

    private User getCurrentUser() {
        // Placeholder implementation. Replace with actual logic to get the current user.
        User user = new User();
        user.setUserId("user123");
        user.setUsername("johndoe");
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        return user;
    }

     */
}
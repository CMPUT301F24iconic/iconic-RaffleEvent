package com.example.iconic_raffleevent.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
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

        User currentUser = getCurrentUser();
        userController = new UserController(currentUser);

        loadUserProfile();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNo = phoneEditText.getText().toString().trim();
            boolean notificationsEnabled = notificationsSwitch.isChecked();

            userController.updateProfile(name, email, phoneNo);
            userController.setNotificationsEnabled(notificationsEnabled);
        });

        profileImageView.setOnClickListener(v -> {
            // Open image picker or camera to select profile image
            // Upload the selected image to Firebase Storage
            // Get the download URL of the uploaded image
            String imageUrl = ""; // Replace with the actual download URL
            userController.uploadProfileImage(imageUrl);
        });

        removePhotoButton.setOnClickListener(v -> {
            userController.removeProfileImage();
            loadUserProfile();
        });
    }

    private void loadUserProfile() {
        User user = userController.getCurrentUser();

        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNo());
        notificationsSwitch.setChecked(user.isNotificationsEnabled());

        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this).load(profileImageUrl).into(profileImageView);
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
}
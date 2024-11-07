package com.example.iconic_raffleevent.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Switch notificationsSwitch;
    private Button saveButton;
    private Button backButton;
    private Button uploadPhotoButton;
    private Button removePhotoButton;

    private Uri currentImageUri;
    private User currentUser;
    private UserController userController;

    // Activity Result Launcher for image picking
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    handleSelectedImage(imageUri);
                }
            }
    );

    // Activity Result Launcher for permissions
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            this::handlePermissionResult
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        initializeControllers();
        setupClickListeners();
        loadUserProfile();
    }

    private void initializeViews() {
        profileImageView = findViewById(R.id.profile_image);
        nameEditText = findViewById(R.id.name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneEditText = findViewById(R.id.phone_edit_text);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        saveButton = findViewById(R.id.save_button);
        uploadPhotoButton = findViewById(R.id.upload_photo_button);
        removePhotoButton = findViewById(R.id.remove_photo_button);
        backButton = findViewById(R.id.back_to_hub_button);
    }

    private void initializeControllers() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }

    private void setupClickListeners() {
        uploadPhotoButton.setOnClickListener(v -> checkAndRequestPermissions());
        removePhotoButton.setOnClickListener(v -> removeProfileImage());
        saveButton.setOnClickListener(v -> saveProfile());
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EventListActivity.class));
            finish();
        });
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            } else {
                openImagePicker();
            }
        } else {
            // For Android 12 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                openImagePicker();
            }
        }
    }

    private void handlePermissionResult(Map<String, Boolean> results) {
        boolean allGranted = true;
        for (Boolean result : results.values()) {
            if (!result) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) {
            openImagePicker();
        } else {
            Toast.makeText(this, "Permission required to select image", Toast.LENGTH_LONG).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImage.launch(intent);
    }

    private void handleSelectedImage(Uri imageUri) {
        if (imageUri != null) {
            currentImageUri = imageUri;
            // Display the selected image
            Glide.with(this)
                    .load(imageUri)
                    .error(R.drawable.default_profile)
                    .into(profileImageView);

            // Upload the image
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (currentUser == null) {
            Toast.makeText(this, "User profile not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show();

        userController.uploadProfileImage(currentUser, imageUri, new UserController.ProfileImageUploadCallback() {
            @Override
            public void onProfileImageUploaded(String imageUrl) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Profile picture updated",
                            Toast.LENGTH_SHORT).show();
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .error(R.drawable.default_profile)
                            .into(profileImageView);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Upload error: " + message);
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + message,
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void removeProfileImage() {
        if (currentUser == null || currentUser.getProfileImageUrl() == null || currentUser.getProfileImageUrl().isEmpty()) {
            Toast.makeText(this, "No profile picture to remove", Toast.LENGTH_SHORT).show();
            return;
        }

        userController.removeProfileImage(currentUser, new UserController.ProfileImageRemovalCallback() {
            @Override
            public void onProfileImageRemoved() {
                runOnUiThread(() -> {
                    profileImageView.setImageResource(R.drawable.default_profile);
                    Toast.makeText(ProfileActivity.this, "Profile picture removed",
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void saveProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "Unable to save profile: User data not loaded",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNo = phoneEditText.getText().toString().trim();
        boolean notificationsEnabled = notificationsSwitch.isChecked();

        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || phoneNo.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        userController.updateProfile(currentUser, name, email, phoneNo);
        userController.setNotificationsEnabled(currentUser, notificationsEnabled);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
                    runOnUiThread(() -> updateUIWithUserData(user));
                } else {
                    runOnUiThread(() -> Toast.makeText(ProfileActivity.this,
                            "Unable to load user profile", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(ProfileActivity.this,
                        "Error loading profile: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUIWithUserData(User user) {
        nameEditText.setText(user.getName());
        emailEditText.setText(user.getEmail());
        phoneEditText.setText(user.getPhoneNo());
        notificationsSwitch.setChecked(user.isNotificationsEnabled());

        String profileImageUrl = user.getProfileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .error(R.drawable.default_profile)
                    .into(profileImageView);
        } else {
            profileImageView.setImageResource(R.drawable.default_profile);
        }
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any resources if needed
    }
}
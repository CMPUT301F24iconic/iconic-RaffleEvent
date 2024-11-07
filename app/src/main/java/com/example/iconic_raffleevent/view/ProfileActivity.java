package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.database.Cursor;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;

import com.example.iconic_raffleevent.AvatarGenerator; // Import AvatarGenerator class

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB in bytes

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

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Navigation Bars
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

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
        uploadPhotoButton.setOnClickListener(v -> chooseImage());
        removePhotoButton.setOnClickListener(v -> removeProfileImage());
        saveButton.setOnClickListener(v -> saveProfile());
        backButton.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EventListActivity.class));
            finish();
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "No app available to select images", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            currentImageUri = data.getData();

            try {
                // Check file size
                long fileSize = getFileSize(currentImageUri);
                if (fileSize > MAX_IMAGE_SIZE) {
                    Toast.makeText(this, "Image size too large. Please select an image under 5MB.", Toast.LENGTH_LONG).show();
                    return;
                }

                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), currentImageUri);
                profileImageView.setImageBitmap(bitmap);

                // Upload the image
                uploadImage(currentImageUri);

            } catch (Exception e) {
                Log.e("ProfileActivity", "Error processing image: " + e.getMessage(), e);
                Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private long getFileSize(Uri fileUri) {
        try {
            Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
            if (cursor != null) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                long size = cursor.getLong(sizeIndex);
                cursor.close();
                return size;
            }
        } catch (Exception e) {
            Log.e("ProfileActivity", "Error getting file size: " + e.getMessage(), e);
        }
        return 0;
    }

    private void uploadImage(Uri imageUri) {
        if (currentUser == null) {
            Toast.makeText(this, "User profile not loaded", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress
        Toast.makeText(this, "Starting upload...", Toast.LENGTH_SHORT).show();

        userController.uploadProfileImage(currentUser, imageUri, new UserController.ProfileImageUploadCallback() {
            @Override
            public void onProfileImageUploaded(String imageUrl) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                    // Load the new image
                    Glide.with(ProfileActivity.this)
                            .load(imageUrl)
                            .error(R.drawable.default_profile)
                            .into(profileImageView);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + message, Toast.LENGTH_LONG).show();
                    Log.e("ProfileActivity", "Upload error: " + message);
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
                    Toast.makeText(ProfileActivity.this, "Profile picture removed", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Unable to save profile: User data not loaded", Toast.LENGTH_SHORT).show();
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
            // Load profile image from URL if available
            Glide.with(this)
                    .load(profileImageUrl)
                    .error(R.drawable.default_profile)
                    .into(profileImageView);
        } else {
            // Generate avatar if no profile image is available
            generateAndSetAvatar(user.getName());
        }
    }

    private void generateAndSetAvatar(String name) {
        // Generate an avatar bitmap using the user's name initials
        Bitmap avatar = AvatarGenerator.generateAvatar(name, 120); // Size 120 is used for demonstration
        profileImageView.setImageBitmap(avatar); // Set the generated avatar as profile image
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
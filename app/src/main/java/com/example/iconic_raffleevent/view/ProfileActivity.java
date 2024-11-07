package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 71;

    private ImageView profileImageView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private Switch notificationsSwitch;
    private Button saveButton;
    private Button backButton;

    private Button uploadPhotoButton;
    private Button removePhotoButton;
    private Uri filePath;
    private FirebaseStorage firebaseAttendee;
    private StorageReference storageReference;

    private User currentUser;
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
        uploadPhotoButton = findViewById(R.id.upload_photo_button);
        removePhotoButton = findViewById(R.id.remove_photo_button);
        backButton = findViewById(R.id.back_to_hub_button);

        userController = getUserController();
        firebaseAttendee = FirebaseStorage.getInstance();
        storageReference = firebaseAttendee.getReference();

        loadUserProfile();

        uploadPhotoButton.setOnClickListener(v -> chooseImage());

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNo = phoneEditText.getText().toString().trim();
            boolean notificationsEnabled = notificationsSwitch.isChecked();

            userController.updateProfile(currentUser, name, email, phoneNo);
            userController.setNotificationsEnabled(currentUser, notificationsEnabled);
        });

        removePhotoButton.setOnClickListener(v -> removeProfileImage());
        backButton.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EventListActivity.class)));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                profileImageView.setImageBitmap(bitmap);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null && currentUser != null) {
            userController.uploadProfileImage(currentUser, filePath, new UserController.ProfileImageUploadCallback() {
                @Override
                public void onProfileImageUploaded(String imageUrl) {
                    Toast.makeText(ProfileActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ProfileActivity.this, "Upload failed: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void removeProfileImage() {
        if (currentUser != null) {
            userController.removeProfileImage(currentUser, new UserController.ProfileImageRemovalCallback() {
                @Override
                public void onProfileImageRemoved() {
                    loadUserProfile();
                    Toast.makeText(ProfileActivity.this, "Profile picture removed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
                    nameEditText.setText(user.getName());
                    emailEditText.setText(user.getEmail());
                    phoneEditText.setText(user.getPhoneNo());
                    notificationsSwitch.setChecked(user.isNotificationsEnabled());

                    String profileImageUrl = user.getProfileImageUrl();
                    if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this).load(profileImageUrl).into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.drawable.default_profile); // fallback image
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID());
        userController = userControllerViewModel.getUserController();
        return userController;
    }

    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
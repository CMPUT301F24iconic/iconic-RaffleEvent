package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.databinding.ActivityProfileBinding;
import com.example.iconic_raffleevent.model.User;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        User currentUser = getCurrentUser();
        userController = new UserController(currentUser);

        loadUserProfile();

        binding.saveProfileButton.setOnClickListener(v -> {
            String name = binding.nameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String phoneNo = binding.phoneEditText.getText().toString().trim();

            userController.updateProfile(name, email, phoneNo);
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        User user = userController.getCurrentUser();
        binding.nameEditText.setText(user.getName());
        binding.emailEditText.setText(user.getEmail());
        binding.phoneEditText.setText(user.getPhoneNo());
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
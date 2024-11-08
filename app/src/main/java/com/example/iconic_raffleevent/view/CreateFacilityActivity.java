package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateFacilityActivity extends AppCompatActivity {

    private TextInputEditText facilityNameEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText facilityDetailsEditText;
    private TextInputLayout facilityNameInputLayout;
    private TextInputLayout locationInputLayout;
    private TextInputLayout facilityDetailsInputLayout;

    private Button saveButton;
    private FacilityController facilityController;
    private UserControllerViewModel userControllerViewModel;
    private UserController userController;
    private User currentUser;  // Assume this is passed in as the organizer user
    private String userId;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton notificationButton;

    // Input Error
    Boolean inputError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_facility);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Navigation Bars
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

        DrawerHelper.setupDrawer(this, drawerLayout, navigationView);

        // Top nav bar
        notificationButton = findViewById(R.id.notification_icon);
        notificationButton.setOnClickListener(v ->
                startActivity(new Intent(CreateFacilityActivity.this, NotificationsActivity.class))
        );

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateFacilityActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateFacilityActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateFacilityActivity.this, ProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize views
        facilityNameInputLayout = findViewById(R.id.facilityNameInputLayout);
        facilityNameEditText = findViewById(R.id.facilityNameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        locationInputLayout = findViewById(R.id.locationInputLayout);
        facilityDetailsEditText = findViewById(R.id.facilityDetailsEditText);
        facilityDetailsInputLayout = findViewById(R.id.facilityDetailsInputLayout);
        saveButton = findViewById(R.id.saveButton);

        // Get user ID
        userId = getIntent().getStringExtra("userId");

        // Initialize FacilityController
        facilityController = new FacilityController();

        // Initialize UserController
        userController = getUserController();

        // Load user object
        loadUserProfile();

        // Set up save button listener
        saveButton.setOnClickListener(v -> saveFacility());
    }

    private void saveFacility() {
        validateInputFields();
        if (!inputError) {
            // Create Facility object
            Facility facility = new Facility(facilityNameEditText.getText().toString(), locationEditText.getText().toString(), currentUser);
            facility.setAdditionalInfo(facilityDetailsEditText.getText().toString());

            // Use FacilityController to create facility
            facilityController.createFacility(facility, new FacilityController.FacilityCreationCallback() {
                @Override
                public void onFacilityCreated(String facilityId) {
                    Toast.makeText(CreateFacilityActivity.this, "Facility created successfully", Toast.LENGTH_SHORT).show();
                    // Redirect to event creation page or another activity as needed
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CreateFacilityActivity.this, "Error creating facility: " + message, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    /*
      Description: Sets an error message for the TextInputLayout xml tag

      Arguments: TextInputLayout textLayout: Xml tag wrapping the edittable input
                 TextInputEditText: An android text block containing the input a user enters
                 String errorMessage: The error message that should be shown

      Returns: Nothing
    */
    public void setErrorMessage(TextInputLayout textLayout, TextInputEditText textString, String errorMessage) {
        // If the input fails to pass the requirements, set an error message
        if (checkInput(textString)) {
            textLayout.setError(errorMessage);
        } else {
            textLayout.setError(null);
        }
    }

    /*
      Description: Checks input for null or empty values

      Arguments: TextInputEditText text: An android text block containing the input a user enters

      Returns: Boolean - True if input passes checks, False if input fails checks
    */
    public Boolean checkInput(TextInputEditText text) {
        if (text.getText() == null) {
            return Boolean.TRUE;
        }
        if (text.getText().length() == 0) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public void validateInputFields() {
        inputError = Boolean.FALSE;

        // Ensure text blocks are not null or empty
        if (checkInput(facilityNameEditText) || checkInput(facilityDetailsEditText) || checkInput(locationEditText)) {
            inputError = Boolean.TRUE;
        }

        // setErrorMessage in the text layout if any of the user inputs don't satisfy requirements
        setErrorMessage(facilityDetailsInputLayout, facilityDetailsEditText, "Facility details cannot be empty");
        setErrorMessage(facilityNameInputLayout, facilityNameEditText, "Facility name cannot be empty");
        setErrorMessage(locationInputLayout, locationEditText, "Location cannot be empty");
    }



    private void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
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

    // Zhiyuan Li
    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(userId,getApplicationContext());
        userController = userControllerViewModel.getUserController();
        return userController;
    }
}

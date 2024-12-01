package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

/**
 * Activity for creating a new facility in the application.
 * Allows the user to input facility details and save them to the database.
 */
public class CreateFacilityActivity extends AppCompatActivity {
    private TextInputEditText facilityNameEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText facilityDetailsEditText;
    private TextInputLayout facilityNameInputLayout;
    private TextInputLayout locationInputLayout;
    private TextInputLayout facilityDetailsInputLayout;

    private Button saveButton;
    public FacilityController facilityController;
    private UserControllerViewModel userControllerViewModel;
    public UserController userController;
    private User currentUser;  // Assume this is passed in as the organizer user
    private String userId;
    public Facility currentFacility; // Holds existing facility details if any

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    ImageButton profileButton;
    private ImageButton backButton;
//    private DrawerLayout drawerLayout;
//    private NavigationView navigationView;
//    private ImageButton notificationButton;

    // Input Error
    public Boolean inputError;

    /**
     * Called when the activity is starting. Initializes views, navigation, controllers, and the current user profile.
     * Sets up listeners for the navigation buttons and the save button.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the most recent data supplied in {@link #onSaveInstanceState(Bundle)}.
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_facility);

        // Initialize DrawerLayout and NavigationView
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView = findViewById(R.id.navigation_view);

        // Navigation Bars
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        backButton = findViewById(R.id.back_button);

        // Top nav bar
//        notificationButton = findViewById(R.id.notification_icon);
//        notificationButton.setOnClickListener(v ->
//                startActivity(new Intent(CreateFacilityActivity.this, NotificationsActivity.class))
//        );

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

        backButton.setOnClickListener(v -> finish());

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

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Error: User ID is missing. Please try again.", Toast.LENGTH_LONG).show();
            finish(); // Close the activity to prevent further errors
            return;
        }

        // Initialize FacilityController
        facilityController = new FacilityController();

        // Initialize UserController
        userController = getUserController();

        // Load user object
        loadUserProfile();

        // Set up save button listener
        saveButton.setOnClickListener(v -> {
            if (currentFacility != null) {
                updateFacility(); // Call update logic if prefilled
            } else {
                saveFacility(); // Call save logic if new
            }
        });

        // Hint behavior for Facility Details
        facilityDetailsEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                facilityDetailsInputLayout.setHint(null); // Remove hint when focused
            } else if (TextUtils.isEmpty(facilityDetailsEditText.getText())) {
                facilityDetailsInputLayout.setHint(getString(R.string.facility_details_hint)); // Restore hint if input is empty
            }
        });
    }

    /**
     * Saves a new facility to the database.
     */
    private void saveFacility() {
        validateInputFields();
        if (!inputError) {
            // Create Facility object
            Facility facility = new Facility(
                    facilityNameEditText.getText().toString(),
                    locationEditText.getText().toString(),
                    currentUser
            );
            facility.setAdditionalInfo(facilityDetailsEditText.getText().toString());

            // Use FacilityController to create facility
            facilityController.createFacility(facility, new FacilityController.FacilityCreationCallback() {
                @Override
                public void onFacilityCreated(String facilityId) {
                    Toast.makeText(CreateFacilityActivity.this, "Facility created successfully", Toast.LENGTH_SHORT).show();

                    boolean fromCreateEvent = getIntent().getBooleanExtra("fromCreateEvent", false);
                    Intent intent;
                    if (fromCreateEvent) {
                        // Redirect to Create Event page
                        intent = new Intent(CreateFacilityActivity.this, CreateEventActivity.class);
                        intent.putExtra("facilityId", facilityId);
                    } else {
                        // Redirect to Home or Facility Access page
                        intent = new Intent(CreateFacilityActivity.this, EventListActivity.class);
                    }
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CreateFacilityActivity.this, "Error creating facility: " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Updates an existing facility in the database.
     */
    private void updateFacility() {
        validateInputFields();
        if (!inputError) {
            // Update currentFacility object
            currentFacility.setFacilityName(facilityNameEditText.getText().toString());
            currentFacility.setFacilityLocation(locationEditText.getText().toString());
            currentFacility.setAdditionalInfo(facilityDetailsEditText.getText().toString());

            // Use FacilityController to update facility
            facilityController.updateFacility(currentFacility, new FacilityController.FacilityUpdateCallback() {
                @Override
                public void onFacilityUpdated() {
                    Toast.makeText(CreateFacilityActivity.this, "Facility updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CreateFacilityActivity.this, "Error updating facility: " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * Sets an error message for a TextInputLayout if input validation fails.
     *
     * @param textLayout The TextInputLayout containing the editable input.
     * @param textString The TextInputEditText holding the user input.
     * @param errorMessage The error message to display if validation fails.
     */
    public void setErrorMessage(TextInputLayout textLayout, TextInputEditText textString, String errorMessage) {
        // If the input fails to pass the requirements, set an error message
        if (checkInput(textString)) {
            textLayout.setError(errorMessage);
        } else {
            textLayout.setError(null);
        }
    }

    /**
     * Checks if the input field is empty or null.
     *
     * @param text The TextInputEditText containing user input.
     * @return True if input is invalid (empty or null); false otherwise.
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

    /**
     * Validates all input fields and sets error messages if any are invalid.
     * Updates inputError to true if any validation fails.
     */
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

    /**
     * Loads the current user's profile information from the database.
     */
    public void loadUserProfile() {
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    currentUser = user;
                    System.out.println("User fetched successfully: " + currentUser.getUsername());
                    runOnUiThread(() -> {
//                        DrawerHelper.setupDrawer(CreateFacilityActivity.this, drawerLayout, navigationView, currentUser.getUserId());
                        loadFacilityDetails(); // Load facility details after setting up the drawer
                    });
                } else {
                    System.out.println("User information is null");
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user information: " + message);
            }
        });
    }

    /**
     * Loads the facility details if a facility exists for the user.
     */
    public void loadFacilityDetails() {
        facilityController.getFacilityByUserId(userId, new FacilityController.FacilityFetchCallback() {
            @Override
            public void onFacilityFetched(Facility facility) {
                currentFacility = facility;
                System.out.println("Facility fetched successfully: " + currentFacility.getFacilityName());
                runOnUiThread(() -> prefillFacilityForm()); // Prefill form on the main thread
            }

            @Override
            public void onError(String message) {
                currentFacility = null;
                System.out.println("No facility found or error fetching facility: " + message);
            }
        });
    }

    /**
     * Prefills the form with existing facility details and updates the button text to "Update".
     */
    private void prefillFacilityForm() {
        TextView createFacilityTitle = findViewById(R.id.createFacilityTitle);


        if (currentFacility != null) {
            System.out.println("Prefilling form with: " + currentFacility.getFacilityName());
            facilityNameEditText.setText(currentFacility.getFacilityName());
            locationEditText.setText(currentFacility.getFacilityLocation());
            facilityDetailsEditText.setText(currentFacility.getAdditionalInfo());

            if (!TextUtils.isEmpty(currentFacility.getAdditionalInfo())) {
                facilityDetailsInputLayout.setHint(null);
            }

            saveButton.setText("Update"); // Change button text to "Update"
            createFacilityTitle.setText("Edit Facility"); // Change title to "Edit Facility"
        } else {
            System.out.println("No facility to prefill.");
            createFacilityTitle.setText("Create Facility");
        }
    }

    /**
     * Initializes and retrieves the UserController for managing user data.
     *
     * @return The initialized UserController.
     */
    private UserController getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(userId,getApplicationContext());
        userController = userControllerViewModel.getUserController();
        return userController;
    }
}

package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

/**
 * Activity for creating an event within the Iconic Raffle Event application.
 * Allows users to input event details, attach a facility, upload a poster, and save the event to the database.
 */
public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 71;

    // Views
    TextInputLayout eventTitleLayout;
    TextInputEditText eventTitleText;
    Switch geolocationRequriedSwitch;
    TextInputLayout startDateLayout;
    TextInputEditText startDateText;
    TextInputLayout startTimeLayout;
    TextInputEditText startTimeText;
    TextInputLayout endDateLayout;
    TextInputEditText endDateText;
    TextInputLayout endTimeLayout;
    TextInputEditText endTimeText;
    TextInputLayout maxAttendeesLayout;
    TextInputEditText maxAttendeesText;
    TextInputLayout eventDescriptionLayout;
    TextInputEditText eventDescriptionText;
    CardView addFacility;
    Button uploadPosterButton;
    Button saveEventButton;

    // Controllers
    UserControllerViewModel userControllerViewModel;
    UserController userController;
    EventController eventController;
    FacilityController facilityController;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton notificationButton;

    // Linked Facility
    String userFacilityId;

    // Objects
    User userObj;
    Event eventObj;

    // Input Error
    Boolean inputError;

    // Event poster
    Uri imageUri;

    /**
     * Initializes the activity, setting up views, navigation drawer, event and facility controllers, and user details.
     *
     * @param savedInstanceState The saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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
                startActivity(new Intent(CreateEventActivity.this, NotificationsActivity.class))
        );

        // Footer buttons logic
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, EventListActivity.class));
        });

        qrButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, QRScannerActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(CreateEventActivity.this, ProfileActivity.class));
        });

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Initialize Views
        initializeViews();

        // Initialize Event Controller
        eventController = new EventController();

        // Initialize Facility Controller
        facilityController = new FacilityController();

        // Initialize User Controller
        initializeUserController();

        // Set listener for add facility button
        addFacility.setOnClickListener(v -> {
            // Check if user has a facility
            facilityController.checkUserFacility(userObj.getUserId(), new FacilityController.FacilityCheckCallback() {
                @Override
                public void onFacilityExists(String facilityId) {
                    userFacilityId = facilityId;
                    Toast.makeText(CreateEventActivity.this, "Successfully attached facility", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFacilityNotExists() {
                    Toast.makeText(CreateEventActivity.this, "You do not own a facility", Toast.LENGTH_SHORT).show();
                    // If user does not have facility, redirect to create facility page
                    Intent intent = new Intent(CreateEventActivity.this, CreateFacilityActivity.class);
                    intent.putExtra("userId", userObj.getUserId());
                    startActivity(intent);
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CreateEventActivity.this, "Unable to locate facility in database", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set listener for upload poster button
        uploadPosterButton.setOnClickListener(v -> {
            // Implement functionality to let user upload a poster
            selectPoster();
        });

        // Set listener for save event button
        saveEventButton.setOnClickListener(v -> {
            // Check to ensure all inputs are valid
            validateInputFields();
            Integer maxAttendees = null;

            if (!inputError) {
                // Check to ensure facility is linked
                if (userFacilityId.isEmpty()) {
                    return;
                }
                if (maxAttendeesText.getText().toString() != null) {
                    maxAttendees = Integer.valueOf(maxAttendeesText.getText().toString());
                }

                // Create a new event and save it to firebase
                eventObj = new Event();
                eventObj.setEventTitle(eventTitleText.getText().toString());
                eventObj.setEventId(eventTitleText.getText().toString());
                eventObj.setEventDescription(eventDescriptionText.getText().toString());
                eventObj.setEventEndTime(endTimeText.getText().toString());
                eventObj.setEventStartTime(startTimeText.getText().toString());
                eventObj.setEventStartDate(startDateText.getText().toString());
                eventObj.setEventEndDate(endDateText.getText().toString());
                eventObj.setGeolocationRequired(geolocationRequriedSwitch.isChecked());
                String hashed_qr_data = "event_" + eventObj.getEventId();
                eventObj.setMaxAttendees(maxAttendees);
                eventObj.setQrCode(hashed_qr_data);
                eventObj.setFacilityId(userFacilityId);

                // Save event and event poster to database
                saveEvent(imageUri, eventObj);
            }
        });
    }

    /**
     * Initializes UI components for the event creation form.
     */
    public void initializeViews() {
        eventTitleLayout = findViewById(R.id.eventTitleInputLayout);
        eventTitleText = findViewById(R.id.eventTitleEditText);
        geolocationRequriedSwitch = findViewById(R.id.geolocationSwitch);
        startDateLayout = findViewById(R.id.startDateInputLayout);
        startDateText = findViewById(R.id.startDateEditText);
        startTimeLayout = findViewById(R.id.startTimeInputLayout);
        startTimeText = findViewById(R.id.startTimeEditText);
        endDateLayout = findViewById(R.id.endDateInputLayout);
        endDateText = findViewById(R.id.endDateEditText);
        endTimeLayout = findViewById(R.id.endTimeInputLayout);
        endTimeText = findViewById(R.id.endTimeEditText);
        maxAttendeesLayout = findViewById(R.id.maxAttendeesInputLayout);
        maxAttendeesText = findViewById(R.id.maxAttendeesEditText);
        eventDescriptionLayout = findViewById(R.id.eventDescriptionInputLayout);
        eventDescriptionText = findViewById(R.id.eventDescriptionEditText);
        addFacility = findViewById(R.id.addFacilityButton);
        uploadPosterButton = findViewById(R.id.uploadPosterButton);
        saveEventButton = findViewById(R.id.saveButton);
    }

    /**
     * Initializes the UserController with the device ID and retrieves the user's information.
     */
    public void initializeUserController() {
        // Retrieve Device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Set up ViewModel
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceID, getApplicationContext());
        userController = userControllerViewModel.getUserController();

        // Fetch User Information
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user");
            }
        });
    }

    /**
     * Sets an error message for a TextInputLayout when the input does not meet requirements.
     *
     * @param textLayout   The TextInputLayout containing the input field
     * @param textString   The input field with user-entered text
     * @param errorMessage The error message to display if input is invalid
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
     * Checks if a TextInputEditText field is empty or null.
     *
     * @param text The input field to check
     * @return True if input is null or empty, false otherwise
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
     * Validates required input fields and sets error messages for invalid inputs.
     */
    public void validateInputFields() {
        inputError = Boolean.FALSE;

        // Ensure text blocks are not null or empty
        if (checkInput(eventTitleText) || checkInput(startDateText) || checkInput(startTimeText) || checkInput(endTimeText) ||
            checkInput(endDateText) || checkInput(eventDescriptionText)) {
            inputError = Boolean.TRUE;
        }

        // setErrorMessage in the text layout if any of the user inputs don't satisfy requirements
        setErrorMessage(eventTitleLayout, eventTitleText, "Title cannot be empty");
        setErrorMessage(startDateLayout, startDateText, "Start date cannot be empty");
        setErrorMessage(startTimeLayout, startTimeText, "Start time cannot be empty");
        setErrorMessage(endTimeLayout, endTimeText, "End time cannot be empty");
        setErrorMessage(endDateLayout, endDateText, "End date cannot be empty");
        setErrorMessage(eventDescriptionLayout, eventDescriptionText, "Event description cannot be empty");
    }

    /**
     * Opens a file chooser for the user to select an image for the event poster.
     */
    private void selectPoster() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Event Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Receives the result of an image selection and sets the chosen image as the event poster.
     *
     * @param requestCode The request code identifying the request
     * @param resultCode  The result code indicating the outcome of the request
     * @param data        The intent data containing the selected image
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the event to the database, including the event poster.
     *
     * @param imageUri The URI of the selected image
     * @param eventObj The event object to be saved
     */
    private void saveEvent(Uri imageUri, Event eventObj) {
        if (imageUri != null) {
            eventController.uploadEventPoster(imageUri, eventObj, new EventController.UploadEventPosterCallback() {
                @Override
                public void onSuccessfulUpload(String posterUrl) {
                    eventObj.setEventImageUrl(posterUrl);

                    // Save event qr image to storage
                    saveEventQR(eventObj);
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(CreateEventActivity.this, "Unable to add image to database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Generates a QR code for the event and uploads it to the database.
     *
     * @param eventObj The event object with QR code details to be saved
     */
    private void saveEventQR(Event eventObj) {
        eventController.uploadEventQRCode(eventObj, new EventController.UploadEventQRCodeCallback() {
            @Override
            public void onSuccessfulQRUpload(String qrUrl) {
                eventObj.setEventQrUrl(qrUrl);

                // upload event to database
                eventController.saveEventToDatabase(eventObj, userObj);

                // Redirect user to event details page after creating event
                Intent intent = new Intent(CreateEventActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", eventObj.getEventId());
                startActivity(intent);
            }

            @Override
            public void onError(String message) {

            }
        });
    }
}

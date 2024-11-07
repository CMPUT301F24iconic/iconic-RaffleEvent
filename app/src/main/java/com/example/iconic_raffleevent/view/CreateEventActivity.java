package com.example.iconic_raffleevent.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;

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

    // Linked Facility
    String userFacilityId;

    // Objects
    User userObj;
    Event eventObj;

    // Input Error
    Boolean inputError;

    // Event poster
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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
                }

                @Override
                public void onFacilityNotExists() {
                    // If user does not have facility, redirect to create facility page
                    startActivity(new Intent(CreateEventActivity.this, CreateFacilityActivity.class));
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

            if (!inputError) {
                // Check to ensure facility is linked
                if (userFacilityId.isEmpty()) {
                    return;
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
                eventObj.setQrCode(hashed_qr_data);
                eventObj.setFacilityId(userFacilityId);

                // Save event and event poster to database
                saveEvent(imageUri, eventObj);
            }
        });
    }

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

    public void initializeUserController() {
        // Retrieve Device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Set up ViewModel
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(deviceID);
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

    private void selectPoster() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Event Picture"), PICK_IMAGE_REQUEST);
    }

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

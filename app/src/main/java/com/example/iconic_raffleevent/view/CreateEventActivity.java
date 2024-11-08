package com.example.iconic_raffleevent.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Calendar;

/**
 * Activity for creating an event within the Iconic Raffle Event application.
 * Allows users to input event details, attach a facility, upload a poster, and save the event to the database.
 * Users can select the event's start and end date and time using date and time pickers.
 */
public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 71;

    // Views
    TextInputLayout eventTitleLayout, startDateLayout, startTimeLayout, endDateLayout, endTimeLayout, maxAttendeesLayout, eventDescriptionLayout;
    TextInputEditText eventTitleText, startDateText, startTimeText, endDateText, endTimeText, maxAttendeesText, eventDescriptionText;
    Switch geolocationRequiredSwitch;
    CardView addFacility;
    Button uploadPosterButton, saveEventButton;

    // Controllers
    UserControllerViewModel userControllerViewModel;
    UserController userController;
    EventController eventController;
    FacilityController facilityController;

    // Objects
    User userObj;
    Event eventObj;

    // Input Error
    Boolean inputError;

    // Event poster
    Uri imageUri;

    /**
     * Called when the activity is created. Initializes UI components,
     * sets up date and time pickers, handles user input validation, and saves events.
     *
     * @param savedInstanceState The saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialize views and controllers
        initializeViews();
        initializeControllers();

        // Set up listeners for date and time pickers
        setupDateTimePickers();

        uploadPosterButton.setOnClickListener(v -> selectPoster());
        saveEventButton.setOnClickListener(v -> validateAndSaveEvent());
    }

    /**
     * Initializes UI components for the event creation form.
     */
    private void initializeViews() {
        eventTitleLayout = findViewById(R.id.eventTitleInputLayout);
        eventTitleText = findViewById(R.id.eventTitleEditText);
        geolocationRequiredSwitch = findViewById(R.id.geolocationSwitch);
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
     * Initializes the UserController, EventController, and FacilityController.
     */
    private void initializeControllers() {
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();
        eventController = new EventController();
        facilityController = new FacilityController();
    }



    /**
     * Sets up click listeners for date and time text fields to open DatePickerDialog and TimePickerDialog.
     */
    private void setupDateTimePickers() {
        startDateText.setOnClickListener(v -> showDatePickerDialog(startDateText));
        startTimeText.setOnClickListener(v -> showTimePickerDialog(startTimeText));
        endDateText.setOnClickListener(v -> showDatePickerDialog(endDateText));
        endTimeText.setOnClickListener(v -> showTimePickerDialog(endTimeText));
    }

    /**
     * Displays a DatePickerDialog and sets the selected date in the given TextInputEditText.
     *
     * @param editText The TextInputEditText where the selected date will be displayed.
     */
    private void showDatePickerDialog(final TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedMonth += 1;
                    String date = String.format("%04d/%02d/%02d", selectedYear, selectedMonth, selectedDay);
                    editText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    /**
     * Displays a TimePickerDialog and sets the selected time in the given TextInputEditText.
     *
     * @param editText The TextInputEditText where the selected time will be displayed.
     */
    private void showTimePickerDialog(final TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String period = (selectedHour < 12) ? "AM" : "PM";
                    selectedHour = (selectedHour % 12 == 0) ? 12 : selectedHour % 12;
                    String time = String.format("%02d:%02d %s", selectedHour, selectedMinute, period);
                    editText.setText(time);
                },
                hour, minute, false
        );
        timePickerDialog.show();
    }
    /**
     * Opens a file chooser to allow the user to select an image for the event poster.
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
     * @param requestCode The request code identifying the request.
     * @param resultCode  The result code indicating the outcome of the request.
     * @param data        The intent data containing the selected image.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Validates the user inputs and, if valid, creates and saves the event to the database.
     */
    private void validateAndSaveEvent() {
        validateInputFields();
        if (!inputError) {
            eventObj = new Event();
            eventObj.setEventTitle(eventTitleText.getText().toString());
            eventObj.setEventStartDate(startDateText.getText().toString());
            eventObj.setEventStartTime(startTimeText.getText().toString());
            eventObj.setEventEndDate(endDateText.getText().toString());
            eventObj.setEventEndTime(endTimeText.getText().toString());
            eventObj.setEventDescription(eventDescriptionText.getText().toString());
            eventObj.setGeolocationRequired(geolocationRequiredSwitch.isChecked());
            saveEvent(imageUri, eventObj);
        }
    }

    /**
     * Validates required input fields and sets error messages for invalid inputs.
     */
    private void validateInputFields() {
        inputError = Boolean.FALSE;
        if (checkInput(eventTitleText) || checkInput(startDateText) || checkInput(startTimeText) ||
                checkInput(endDateText) || checkInput(endTimeText) || checkInput(eventDescriptionText)) {
            inputError = Boolean.TRUE;
        }
        setErrorMessage(eventTitleLayout, eventTitleText, "Title cannot be empty");
        setErrorMessage(startDateLayout, startDateText, "Start date cannot be empty");
        setErrorMessage(startTimeLayout, startTimeText, "Start time cannot be empty");
        setErrorMessage(endDateLayout, endDateText, "End date cannot be empty");
        setErrorMessage(endTimeLayout, endTimeText, "End time cannot be empty");
        setErrorMessage(eventDescriptionLayout, eventDescriptionText, "Event description cannot be empty");
    }

    /**
     * Sets an error message for a TextInputLayout if the input is invalid.
     *
     * @param layout  The TextInputLayout containing the input field.
     * @param editText The TextInputEditText containing the user input.
     * @param errorMessage The error message to display if input is invalid.
     */
    private void setErrorMessage(TextInputLayout layout, TextInputEditText editText, String errorMessage) {
        if (checkInput(editText)) {
            layout.setError(errorMessage);
        } else {
            layout.setError(null);
        }
    }

    /**
     * Checks if a TextInputEditText field is empty or null.
     *
     * @param editText The input field to check.
     * @return True if input is null or empty, false otherwise.
     */
    private Boolean checkInput(TextInputEditText editText) {
        return editText.getText() == null || editText.getText().length() == 0;
    }

    /**
     * Saves the event to the database, including uploading the event poster if provided.
     *
     * @param imageUri The URI of the selected image.
     * @param eventObj The event object to be saved.
     */
    private void saveEvent(Uri imageUri, Event eventObj) {
        if (imageUri != null) {
            eventController.uploadEventPoster(imageUri, eventObj, new EventController.UploadEventPosterCallback() {
                @Override
                public void onSuccessfulUpload(String posterUrl) {
                    eventObj.setEventImageUrl(posterUrl);
                    saveEventToDatabase(eventObj);
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(CreateEventActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            saveEventToDatabase(eventObj);
        }
    }

    /**
     * Saves the event object to the database and redirects the user to the event details page.
     *
     * @param eventObj The event object to be saved.
     */
    private void saveEventToDatabase(Event eventObj) {
        eventController.saveEventToDatabase(eventObj, userObj);
        Intent intent = new Intent(CreateEventActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventId", eventObj.getEventId());
        startActivity(intent);
    }

    /**
     * Retrieves the unique user ID for the device.
     *
     * @return A unique string identifier for the device.
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}

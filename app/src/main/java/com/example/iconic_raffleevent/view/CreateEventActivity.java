package com.example.iconic_raffleevent.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.FacilityController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.Facility;
import com.example.iconic_raffleevent.model.User;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Activity for creating an event within the Iconic Raffle Event application.
 * Allows users to input event details, attach a facility, upload a poster, and save the event to the database.
 * Users can select the event's start and end date and time using date and time pickers.
 */
public class CreateEventActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 71;
    private ImageView posterPreviewImageView;

    // Views
    TextInputLayout eventTitleLayout, startDateLayout, startTimeLayout, endDateLayout, endTimeLayout, maxAttendeesLayout, eventDescriptionLayout;
    TextInputEditText eventTitleText, startDateText, startTimeText, endDateText, endTimeText, maxAttendeesText, eventDescriptionText;
    TextView posterLabel, eventTitleHeader;
    Switch geolocationRequiredSwitch;
    Button uploadPosterButton, saveEventButton;

    // Nav bar
    private ImageButton homeButton;
    private ImageButton qrButton;
    private ImageButton profileButton;
    private ImageButton menuButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton notificationButton;

    // IDs
    String eventId;

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

        // upload poster preview
        posterPreviewImageView = findViewById(R.id.posterPreviewImageView);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Navigation Bars
        homeButton = findViewById(R.id.home_button);
        qrButton = findViewById(R.id.qr_button);
        profileButton = findViewById(R.id.profile_button);
        menuButton = findViewById(R.id.menu_button);

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

        userFacilityId = getIntent().getStringExtra("facilityId");

        if (TextUtils.isEmpty(userFacilityId)) {
            Toast.makeText(this, "No facility linked. Please create a facility first.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views and controllers
        initializeControllers();
        initializeViews();

        // Set up listeners for date and time pickers
        setupDateTimePickers();

        uploadPosterButton.setOnClickListener(v -> selectPoster());
        saveEventButton.setOnClickListener(v -> validateAndSaveEvent());
    }

    /**
     * Initializes UI components for the event creation form.
     */
    private void initializeViews() {
        eventTitleHeader = findViewById(R.id.createEventTitle);
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
        posterLabel = findViewById(R.id.posterLabel);
        uploadPosterButton = findViewById(R.id.uploadPosterButton);
        saveEventButton = findViewById(R.id.saveButton);

        // Disable manual input for Start Date, Start Time, End Date, and End Time
        startDateText.setKeyListener(null);
        startTimeText.setKeyListener(null);
        endDateText.setKeyListener(null);
        endTimeText.setKeyListener(null);

        // For Max Attendees
        maxAttendeesText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                maxAttendeesLayout.setHint(null); // Remove the hint when focused
            } else if (maxAttendeesText.getText().toString().isEmpty()) {
                maxAttendeesLayout.setHint(getString(R.string.max_attendees_hint)); // Restore hint if input is empty
            }
        });

        // For Event Description
        eventDescriptionText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                eventDescriptionLayout.setHint(null); // Remove the hint when focused
            } else if (eventDescriptionText.getText().toString().isEmpty()) {
                eventDescriptionLayout.setHint(getString(R.string.event_description_hint)); // Restore hint if input is empty
            }
        });

        // Check to see if we are editing event
        if (checkEventExists() == Boolean.TRUE) {
            // Fill in fields and adjust text to reflect edit event
            loadEventDetails();
        }
    }

    /**
     * Initializes the UserController, EventController, and FacilityController.
     * Fetches the user and checks for facility after the user is fetched.
     */
    private void initializeControllers() {
        userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(), getApplicationContext());
        userController = userControllerViewModel.getUserController();

        // Fetch User Information
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
                    runOnUiThread(() -> {
                        // Set up the drawer only after fetching the user information
                        DrawerHelper.setupDrawer(CreateEventActivity.this, drawerLayout, navigationView, userObj.getUserId());
                    });
                } else {
                    Toast.makeText(CreateEventActivity.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                System.out.println("Error fetching user: " + message);
            }
        });

        // Initialize event and facility controllers
        eventController = new EventController();
        facilityController = new FacilityController();
    }

    /**
     * Sets up click listeners for date and time text fields to open DatePickerDialog and TimePickerDialog.
     */
    private void setupDateTimePickers() {
        // Open Date Picker on click
        startDateText.setOnClickListener(v -> showDatePickerDialog(startDateText));
        endDateText.setOnClickListener(v -> showDatePickerDialog(endDateText));

        // Open Time Picker on click
        startTimeText.setOnClickListener(v -> showTimePickerDialog(startTimeText));
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
                showPosterPreviewDialog(bitmap); // Show preview dialog
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPosterPreviewDialog(Bitmap posterBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Poster Preview");
        builder.setMessage("Please confirm the selected poster.");

        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(posterBitmap);
        imageView.setAdjustViewBounds(true);
        imageView.setMaxHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.5));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        builder.setView(imageView);

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            // User confirmed the poster, proceed with saving the event
            posterPreviewImageView.setImageBitmap(posterBitmap);
            posterPreviewImageView.setVisibility(View.VISIBLE);
            posterLabel.setVisibility(View.VISIBLE);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User canceled the poster selection
            imageUri = null;
            if (eventId == null) {
                posterPreviewImageView.setVisibility(View.GONE);
                posterLabel.setVisibility(View.GONE);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set the dialog window to half screen size
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (getResources().getDisplayMetrics().heightPixels * 0.5));
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
        }

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
    }

    /**
     * Validates the user inputs and, if valid, updates/saves the event in the database.
     */
    private void validateAndSaveEvent() {
        // Keep the Save button visible and enabled
        saveEventButton.setVisibility(View.VISIBLE);
        saveEventButton.setEnabled(true);

        validateInputFields();


        if (eventId == null){
            // Check if a poster is uploaded
            if (imageUri == null) {
                Toast.makeText(this, "Please upload a poster for the event.", Toast.LENGTH_SHORT).show();
                return; // Stop further processing
            }
        }

        // Check if end time is later than start time
        if (!isEndTimeLaterThanStartTime()) {
            Toast.makeText(this, "End time must be later than start time.", Toast.LENGTH_SHORT).show();
            return; // Stop further processing
        }

        // If there are input errors, notify the user and return
        if (inputError) {
            Toast.makeText(this, "Please complete all required fields and try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with saving the event if all validations pass
        Integer maxAttendees = maxAttendeesText.getText().toString().isEmpty() ? null : Integer.valueOf(maxAttendeesText.getText().toString());

        // If user is creating new event
        if (eventObj == null) {
            eventObj = new Event();
            eventObj.setEventId(eventTitleText.getText().toString());
            eventObj.setEventTitle(eventTitleText.getText().toString());
            eventObj.setEventStartDate(startDateText.getText().toString());
            eventObj.setEventStartTime(startTimeText.getText().toString());
            eventObj.setEventEndDate(endDateText.getText().toString());
            eventObj.setEventEndTime(endTimeText.getText().toString());
            eventObj.setEventDescription(eventDescriptionText.getText().toString());
            eventObj.setGeolocationRequired(geolocationRequiredSwitch.isChecked());
            String hashedQrData = "event_" + eventObj.getEventId();
            eventObj.setMaxAttendees(maxAttendees);
            eventObj.setQrCode(hashedQrData);
            eventObj.setFacilityId(userFacilityId);

            // Save event and event poster to the database
            saveEvent(imageUri, eventObj);
        } else {
            // User is updating an existing event
            eventObj.setEventTitle(eventTitleText.getText().toString());
            eventObj.setEventStartDate(startDateText.getText().toString());
            eventObj.setEventStartTime(startTimeText.getText().toString());
            eventObj.setEventEndDate(endDateText.getText().toString());
            eventObj.setEventEndTime(endTimeText.getText().toString());
            eventObj.setEventDescription(eventDescriptionText.getText().toString());
            eventObj.setGeolocationRequired(geolocationRequiredSwitch.isChecked());
            eventObj.setMaxAttendees(maxAttendees);

            // Update event details in database
            updateEvent(imageUri, eventObj);
        }
    }

    /**
     * Checks if the selected end time is later than the selected start time.
     *
     * @return True if end time is later than start time, false otherwise.
     */
    public boolean isEndTimeLaterThanStartTime() {
        String startDateTimeStr = startDateText.getText().toString() + " " + startTimeText.getText().toString();
        String endDateTimeStr = endDateText.getText().toString() + " " + endTimeText.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);

        try {
            Date startDateTime = format.parse(startDateTimeStr);
            Date endDateTime = format.parse(endDateTimeStr);

            return endDateTime.after(startDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
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
            layout.setError(null); // Clear error when input is valid
        }
        // Ensure the Save button is visible and enabled
        saveEventButton.setVisibility(View.VISIBLE);
        saveEventButton.setEnabled(true);
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

                    // Show a success toast for the poster upload
                    Toast.makeText(CreateEventActivity.this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Save event qr image to storage
                    saveEventQR(eventObj);
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(CreateEventActivity.this, "Please upload a poster before saving the event.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEvent(Uri imageUri, Event eventObj) {
        // Need to implement functionality to delete only image from firebase storage
        // For now just focus on adding new image to firebase storage
        if (imageUri != null) {
            eventController.uploadEventPoster(imageUri, eventObj, new EventController.UploadEventPosterCallback() {
                @Override
                public void onSuccessfulUpload(String posterUrl) {
                    eventObj.setEventImageUrl(posterUrl);

                    // Show a success toast for the poster upload
                    Toast.makeText(CreateEventActivity.this, "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Update event
                    eventController.updateEventDetails(eventObj);
                }
                @Override
                public void onError(String message) {
                    Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Update event with pre-existing poster image
            eventController.updateEventDetails(eventObj);
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

                if (qrUrl != null && !qrUrl.isEmpty()) {
                    eventController.saveEventToDatabase(eventObj, userObj);
                    Intent intent = new Intent(CreateEventActivity.this, EventDetailsActivity.class);
                    intent.putExtra("eventId", eventObj.getEventId());
                    startActivity(intent);
                } else {
                    Toast.makeText(CreateEventActivity.this, "QR Code URL is missing!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(CreateEventActivity.this, "QR Code generation failed: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Retrieves the unique user ID for the device.
     *
     * @return A unique string identifier for the device.
     */
    private String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private Boolean checkEventExists() {
        System.out.println(getIntent().getStringExtra("eventId"));
        if (getIntent().getStringExtra("eventId") != null) {
            eventId = getIntent().getStringExtra("eventId");
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * Loads the event details if a organizer is editing an existing event.
     */
    private void loadEventDetails() {
        eventController.getEventDetails(eventId, new EventController.EventDetailsCallback() {
            @Override
            public void onEventDetailsFetched(Event event) {
                eventObj = event;
                runOnUiThread(() -> prefillEventForm()); // Prefill form on the main thread
            }
            @Override
            public void onError(String message) {
                eventObj = null;
            }
        });
    }

    /**
     * Prefills the form with existing event details and updates the button text to "Update" and page title to Edit.
     */
    private void prefillEventForm() {
        if (eventObj != null) {
            eventTitleHeader.setText("Edit Event");
            eventTitleText.setText(eventObj.getEventTitle());
            geolocationRequiredSwitch.setChecked(eventObj.isGeolocationRequired());
            startDateText.setText(eventObj.getEventStartDate());
            startTimeText.setText(eventObj.getEventStartTime());
            endDateText.setText(eventObj.getEventEndDate());
            endTimeText.setText(eventObj.getEventEndTime());
            if (eventObj.getMaxAttendees() != null) {
                maxAttendeesText.setText(eventObj.getMaxAttendees().toString());
            }
            eventDescriptionText.setText(eventObj.getEventDescription());
            posterPreviewImageView.setVisibility(View.VISIBLE);
            posterLabel.setVisibility(View.VISIBLE);
            // Load event image
            Glide.with(this)
                    .load(eventObj.getEventImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(posterPreviewImageView);
            saveEventButton.setText("Update"); // Change button text to "Update"
            uploadPosterButton.setText("Change Poster");
        } else {
            System.out.println("No facility to prefill.");
        }
    }

    public void setEventTitle(String title) {
        this.eventTitleText.setText(title);
    }

    public void setStartDate(String date) {
        this.startDateText.setText(date);
    }

    public void setStartTime(String time) {
        this.startTimeText.setText(time);
    }

    public void setEndDate(String date) {
        this.endDateText.setText(date);
    }

    public void setEndTime(String time) {
        this.endTimeText.setText(time);
    }

    public void setEventDescription(String description) {
        this.eventDescriptionText.setText(description);
    }

    public void invokeValidateInputFields() {
        validateInputFields();
    }

    public boolean isInputError() {
        return inputError;
    }

    public String getEventTitleError() {
        return eventTitleLayout.getError() != null ? eventTitleLayout.getError().toString() : null;
    }

    public String getEventDescriptionError() {
        return eventDescriptionLayout.getError() != null ? eventDescriptionLayout.getError().toString() : null;
    }

    public void setImageUri(Uri uri) {
        this.imageUri = uri;
    }

    public void setEventController(EventController controller) {
        this.eventController = controller;
    }

    // Public methods to invoke private methods
    public void invokeValidateAndSaveEvent() {
        validateAndSaveEvent();
    }

}
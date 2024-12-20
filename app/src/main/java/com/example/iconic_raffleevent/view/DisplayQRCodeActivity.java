package com.example.iconic_raffleevent.view;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.EventController;
import com.example.iconic_raffleevent.controller.UserController;
import com.example.iconic_raffleevent.model.Event;
import com.example.iconic_raffleevent.model.User;

public class DisplayQRCodeActivity extends AppCompatActivity {
    public User userObj;
    public UserController userController;
    public EventController eventController;
    public Event event;

    /**
     * Initializes the activity when it is created. Sets up the layout, initializes
     * user and event controllers, and sets listeners for any click elements on screen.
     *
     * @param savedInstanceState If the activity is re-initialized after previously
     *                           being shut down, it will pull any cached data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_qrcode);

        EditText eventIDText = findViewById(R.id.custom_event_id_text);
        Button createEventButton = findViewById(R.id.create_qrcode_button);
        ImageView qrCodeView = findViewById(R.id.qr_code);
        Button showQRCode = findViewById(R.id.show_qrcode_button);

        eventController = new EventController();
        getUserController();
        loadUserProfile();

        createEventButton.setOnClickListener(v -> {
            // Create a new event and save it to firebase
            event = new Event();
            event.setEventTitle("Custom");
            event.setEventId(eventIDText.getText().toString());
            event.setEventDescription("Custom event");
            event.setEventLocation("Edmonton");
            event.setEventEndTime("10:00 PM");
            event.setEventStartTime("5:00 PM");
            event.setEventStartDate("2024-06-02");
            event.setEventEndDate("2024-06-02");

            eventController.saveEventToDatabase(event, userObj);
        });

        showQRCode.setOnClickListener(v -> {
            // Get bitmap from event
            //Bitmap qrCode = event.getEventQR();
            //qrCodeView.setImageBitmap(qrCode);
        });

    }

    /**
     * Load user profile information by fetching from firebase
     */
    public void loadUserProfile() {
        /* Aiden Teal code with user info from database */
        userController.getUserInformation(new UserController.UserFetchCallback() {
            @Override
            public void onUserFetched(User user) {
                if (user != null) {
                    userObj = user;
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

    /**
     * Get device id from settings
     * @return String device id
     */
    @SuppressLint("HardwareIds")
    public String getUserID() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Initialize user controller with user ID
     */
    public void getUserController() {
        UserControllerViewModel userControllerViewModel = new ViewModelProvider(this).get(UserControllerViewModel.class);
        userControllerViewModel.setUserController(getUserID(),getApplicationContext());
        userController = userControllerViewModel.getUserController();
    }
}

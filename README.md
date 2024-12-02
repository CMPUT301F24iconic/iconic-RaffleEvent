

# iconic-RaffleEvent Application
## **[Javadocs Here](https://cmput301f24iconic.github.io/)**
## Overview
The **iconic-RaffleEvent Application** is an Android mobile application that allows users to sign up for popular events at community centers via a lottery system. This ensures a fair and accessible system for people who may have limitations like work or disabilities. Instead of first-come-first-served registration, users can express interest in an event and be selected via a lottery process. Event organizers can manage waiting lists and registrants efficiently, while entrants are notified if they win or lose the lottery.

## Features
- **Lottery System**: Organizers can draw participants from a waiting list.
- **QR Code Scanning**: Entrants can scan QR codes to view event details and join waiting lists.
- **Firebase Integration**: Stores event details, waiting lists, and real-time updates.
- **Multi-User Interaction**: Includes roles for entrants, organizers, and admins.
- **Image Upload**: Organizers can upload event posters.
- **Geolocation Verification (Optional)**: Optionally verify where users are joining the waiting list from.

## Project Structure
This project follows the **Model-View-Controller (MVC)** architecture pattern. Below is a description of the folder structure and its contents:

### `model/`
Contains the core data classes of the application, including:
- `Event.java`: Represents an event within the raffle system.
- `User.java`: Represents a user in the system.
- `Facility.java`: Represents a facility associated with an event, containing details such as name, location, additional information, and the creator (organizer).
- `QRCodeData.java`: Represents data for a QR code used in the raffle event.
- `Notification.java`: Represents a notification sent to a user about their lottery results or updates.
- `ImageData.java`: Represents image data, including an image identifier, title, and the URL to the image.

### `view/`
Contains all the Android activities and UI components that interact with users:
- `FirestoreListenerService.java`: A background service that listens to Firestore updates for notifications.
- `AdminEventActivity.java`: Displays a list of events and allows the admin to delete events through Firebase.
- `AdminFacilityActivity.java`: Displays a list of facilities and allows the admin to delete facilities through Firebase.
- `AdminHubActivity.java`: Serves as the main hub for administrative actions within the application.
- `AdminImageActivity.java`: Provides an interface for managing images within the application.
- `AdminProfileActivity.java`: Displays a list of user profiles fetched from Firebase and allows administrators to delete profiles.
- `AdminQRCodeActivity.java`: Displays a list of QR codes fetched from Firebase and allows administrators to delete QR codes.
- `ConfirmedListActivity.java`: Displays a list of users who have confirmed their attendance for an event.
- `CreateEventActivity.java`: Activity for creating an event within the Iconic Raffle Event application.
- `CreateFacilityActivity.java`: Activity for creating a new facility in the application.
- `DeclinedListActivity.java`: Displays a list of users who have declined an event invitation.
- `DisplayQRCodeActivity.java`: Displays a list of QR codes
- `DrawerHelper.java`: Helper class for setting up and managing the navigation drawer in the application.
- `EventAdapter.java`: Custom ArrayAdapter for displaying a list of Event objects in a ListView.
- `EventDetailsActivity.java`: Displays the details of a specific event.
- `EventDetailsForAdminActivity.java`: Displays the details of a specific event for admin.
- `EventListActivity.java`: Displays a list of events available to the user and allows navigation to other sections of the app such as QR scanner, profile, and notifications.
- `EventListForAdminActivity.java`: Displays a list of events available to the user and allows navigation to other sections of the app such as QR scanner, profile, and notifications.
- `EventListUtils.java`: Helper function for user list related views
- `EventManagementActivity.java`: Activity that provides an interface for administrators to manage events.
- `EventQRViewActivity.java`: Activity for viewing event QR code
- `FacilityAdapter.java`: Adapter for displaying facilities in a RecyclerView.
- `FacilityListForAdminActivity.java`: Activity that displays a list of facilities for the admin to manage.
- `ImageManagementActivity.java`: Activity class for managing a list of images. Provides functionality to display, refresh, and delete images from the list.
- `InvitedListActivity.java`: Displays a list of users who have been invited to an event.
- `MainActivity.java`: The main activity for the Iconic Raffle Event application.
- `ManageEventActivity.java`: Activity for managing various lists associated with an event.
- `MapActivity.java`: Displays the map for a specific event, showing the locations of entrants on the map.
- `NewUserActivity.java`: Responsible for handling the creation of a new user within the application.
- `NotificationAdapter.java`: Adapter class to bind a list of notifications to a ListView.
- `NotificationsActivity.java`: Activity class to display the list of notifications for the user.
- `NotificationSettingsActivity.java`: Activity that handles user notification settings.
- `NotificationUtils.java`: Utility class for creating and sending notifications.
- `ProfileActivity.java`: Manages the user's profile.
- `ProfileManagementActivity.java`: Activity that manages the user profiles, allowing the removal of profiles from the list.
- `QRCodeGalleryActivity.java`: Activity to display a gallery of QR codes.
- `QRCodeGalleryAdapter.java`: Adapter for displaying QR codes in a RecyclerView.
- `QRScannerActivity.java`: Responsible for scanning QR codes using the device's camera, retrieving user information, fetching the user's location, and processing the QR code data.
- `RoleSelectionActivity.java`: Allows the user to choose between different roles (e.g., Entrant/Organizer or Admin).
- `UserAdapter.java`: Adapter class for displaying a list of User objects in a RecyclerView.
- `UserControllerViewModel.java`: ViewModel class for managing an instance of UserController.
- `UserListActivity.java`: Activity that displays a list of all users in the app and provides management options for each user.
- `WaitingListActivity.java`: Activity that displays a waiting list of users for a specified event and allows the organizer to randomly select attendees based on event capacity.
- `AvatarGenerator.java`: Utility class for generating circular avatar images with initials.

### `controller/`
Handles the application logic, ensuring smooth interaction between the views and models:
- `EventController.java`: Handles the logic related to event creation, modification, and management.
- `FacilityController.java`: Handles the business logic related to facilities.
- `FirebaseAttendee.java`: Interacts with Firebase Firestore and Firebase Storage to manage user profiles, events, and notifications.
- `FirebaseOrganizer.java`: Responsible for handling operations related to the firebase database, such as creating, deleting, and fetching data related to users, facilities, events, images, and QR codes.
- `ImageController.java`: Responsible for managing image data stored in Firestore.
- `NotificationController.java`: Responsible for managing notifications for users.
- `OnUserRetrievedListener.java`: Interface for listening to user retrieval callbacks.
- `QRCodeController.java`: Controller class to manage QR code data from the Event collection.
- `UserController.java`: Controller class that manages user-related functionalities such as adding a user, updating user profiles, uploading/removing profile images, enabling/disabling notifications, and fetching user data from Firebase.

### `resources/`
Contains the app’s XML layout files, images, and other resources:
- **Layouts**: Define the UI of each screen (e.g., activity_main.xml, activity_event_details.xml).
- **Drawable**: Stores app icons, images, and event posters.

## Firebase Integration
The application uses Firebase Firestore or Realtime Database to store event data, user profiles, waiting lists, and lottery results. Firebase also handles user authentication and image uploads for event posters.

## Requirements
- **Android Studio**: The app is developed using Android Studio.
- **Firebase Account**: Required for database and storage integration.
- **Zebra Crossing Library or Google ML Kit**: For QR code scanning.

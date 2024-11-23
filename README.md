**[javadoc here](https://cmput301f24iconic.github.io/)**

# iconic-RaffleEvent Application

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
- `Event.java`: Represents an event, storing details such as title, date, capacity, and registrants.
- `User.java`: Represents a user, which could be an entrant, organizer, or admin.
- `WaitingList.java`: Manages the waiting list for an event and handles lottery drawings.
- `QRHash.java`: Stores and verifies QR code hash data.
- `Notification.java`: Represents a notification sent to users about event updates or lottery results.
- `FirebaseModel.java`: Manages interactions with Firebase for storing and retrieving event, user, and waiting list data.

### `view/`
Contains all the Android activities and UI components that interact with users:
- `MainActivity.java`: The main screen that provides navigation options to view events or profiles.
- `EventDetailsActivity.java`: Shows the details of an event after scanning a QR code, allowing users to register.
- `ProfileActivity.java`: Allows users to view and edit their profile information.
- `EventCreationActivity.java`: Lets organizers create and manage events.
- `WaitingListActivity.java`: Displays the waiting list for organizers and manages the lottery drawing process.

### `controller/`
Handles the application logic, ensuring smooth interaction between the views and models:
- `EventController.java`: Manages event creation, retrieval, and validation.
- `UserController.java`: Manages user profile updates and registration.
- `WaitingListController.java`: Manages the waiting list, including adding/removing entrants and conducting the lottery.
- `QRController.java`: Handles QR code generation, verification, and event detail retrieval.

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

//package com.example.iconic_raffleevent;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import com.example.iconic_raffleevent.controller.UserController;
//import com.example.iconic_raffleevent.model.User;
//import com.example.iconic_raffleevent.view.ProfileActivity;
//import com.example.iconic_raffleevent.view.UserControllerViewModel;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.replaceText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doAnswer;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@RunWith(AndroidJUnit4.class)
//public class ProfileActivityTest {
//
//    @Mock
//    private UserController mockUserController;
//
//    @Mock
//    private UserControllerViewModel mockViewModel;
//
//    private Context context;
//    private User mockUser;
//
//    @Before
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        context = ApplicationProvider.getApplicationContext();
//
//        mockUser = new User();
//        mockUser.setName("Test User");
//        mockUser.setEmail("test@example.com");
//        mockUser.setPhoneNo("1234567890");
//        mockUser.setProfileImageUrl("http://test.com/profile.jpg");
//    }
//
//    /**
//     * Test that profile information loads successfully and populates the UI fields.
//     */
//    @Test
//    public void testLoadUserProfile_Success() {
//        doAnswer(invocation -> {
//            UserController.UserFetchCallback callback = invocation.getArgument(0);
//            callback.onUserFetched(mockUser);  // Simulate a successful user fetch
//            return null;
//        }).when(mockUserController).getUserInformation(any(UserController.UserFetchCallback.class));
//
//        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
//        scenario.onActivity(activity -> activity.userController = mockUserController);
//
//        onView(withId(R.id.name_edit_text)).check(matches(withText("Test User")));
//        onView(withId(R.id.email_edit_text)).check(matches(withText("test@example.com")));
//        onView(withId(R.id.phone_edit_text)).check(matches(withText("1234567890")));
//    }
//
//    /**
//     * Test that saveProfile updates user information and displays a success toast.
//     */
//    @Test
//    public void testSaveProfile_UpdatesUserInformation() {
//        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
//        scenario.onActivity(activity -> {
//            activity.userController = mockUserController;
//            activity.currentUser = mockUser;
//        });
//
//        onView(withId(R.id.name_edit_text)).perform(replaceText("Updated User"));
//        onView(withId(R.id.email_edit_text)).perform(replaceText("updated@example.com"));
//        onView(withId(R.id.phone_edit_text)).perform(replaceText("0987654321"));
//
//        onView(withId(R.id.save_button)).perform(click());
//
//        verify(mockUserController).updateProfile(any(User.class), eq("Updated User"), eq("updated@example.com"), eq("0987654321"));
//        onView(withText("Profile updated successfully")).inRoot(new ToastMatcher()).check(matches(withText("Profile updated successfully")));
//    }
//
//    /**
//     * Test that image upload is triggered when selecting an image from the gallery.
//     */
//    @Test
//    public void testUploadImage_SuccessfulUpload() {
//        Uri mockUri = Uri.parse("content://mock_image_uri");
//
//        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
//        scenario.onActivity(activity -> {
//            activity.userController = mockUserController;
//            activity.currentUser = mockUser;
//        });
//
//        doAnswer(invocation -> {
//            UserController.ProfileImageUploadCallback callback = invocation.getArgument(1);
//            callback.onProfileImageUploaded("http://test.com/new_image.jpg");  // Simulate successful upload
//            return null;
//        }).when(mockUserController).uploadProfileImage(any(User.class), eq(mockUri), any(UserController.ProfileImageUploadCallback.class));
//
//        Intent resultData = new Intent();
//        resultData.setData(mockUri);
//
//        // Simulate the image being selected
//        scenario.onActivity(activity -> activity.handleImageResult(mockUri));
//
//        verify(mockUserController).uploadProfileImage(any(User.class), eq(mockUri), any(UserController.ProfileImageUploadCallback.class));
//        onView(withText("Upload successful")).inRoot(new ToastMatcher()).check(matches(withText("Upload successful")));
//    }
//
//    /**
//     * Test that removeProfileImage removes the image and updates the UI accordingly.
//     */
//    @Test
//    public void testRemoveProfileImage_SuccessfulRemoval() {
//        ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(ProfileActivity.class);
//        scenario.onActivity(activity -> {
//            activity.userController = mockUserController;
//            activity.currentUser = mockUser;
//        });
//
//        doAnswer(invocation -> {
//            UserController.ProfileImageRemovalCallback callback = invocation.getArgument(1);
//            callback.onProfileImageRemoved();  // Simulate successful removal
//            return null;
//        }).when(mockUserController).removeProfileImage(any(User.class), any(UserController.ProfileImageRemovalCallback.class));
//
//        onView(withId(R.id.remove_photo_button)).perform(click());
//        verify(mockUserController).removeProfileImage(any(User.class), any(UserController.ProfileImageRemovalCallback.class));
//        onView(withText("Profile picture removed")).inRoot(new ToastMatcher()).check(matches(withText("Profile picture removed")));
//    }
//}

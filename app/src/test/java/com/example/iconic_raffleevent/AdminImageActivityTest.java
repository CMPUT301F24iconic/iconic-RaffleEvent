package com.example.iconic_raffleevent;

import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.ImageData;
import com.example.iconic_raffleevent.view.AdminImageActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.*;

@RunWith(AndroidJUnit4.class)
public class AdminImageActivityTest {

    private FirebaseOrganizer firebaseOrganizerMock;

    @Before
    public void setup() {
        Intents.init();
        firebaseOrganizerMock = Mockito.mock(FirebaseOrganizer.class);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    /**
     * Test that images load successfully and are displayed in the ListView.
     */
    @Test
    public void testLoadImageList_Success() {
        ArrayList<ImageData> mockImages = new ArrayList<>();
        ImageData image = new ImageData();
        image.setTitle("Sample Image");
        mockImages.add(image);

        doAnswer(invocation -> {
            FirebaseOrganizer.GetImagesCallback callback = invocation.getArgument(0);
            callback.onImagesFetched(mockImages); // Simulate successful image fetch
            return null;
        }).when(firebaseOrganizerMock).getAllImages(any(FirebaseOrganizer.GetImagesCallback.class));

        ActivityScenario.launch(AdminImageActivity.class);

        onView(withId(R.id.image_list_view)).check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when loading images fails.
     */
    @Test
    public void testLoadImageList_Failure() {
        doAnswer(invocation -> {
            FirebaseOrganizer.GetImagesCallback callback = invocation.getArgument(0);
            callback.onError("Failed to load images"); // Simulate fetch error
            return null;
        }).when(firebaseOrganizerMock).getAllImages(any(FirebaseOrganizer.GetImagesCallback.class));

        ActivityScenario.launch(AdminImageActivity.class);

        onView(withText("Error loading images: Failed to load images"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that deleting an image shows a success message and refreshes the list.
     */
    @Test
    public void testDeleteImage_Success() {
        ArrayList<ImageData> mockImages = new ArrayList<>();
        ImageData image = new ImageData();
        image.setImageId("image1");
        image.setTitle("Sample Image");
        mockImages.add(image);

        // Simulate successful image fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetImagesCallback callback = invocation.getArgument(0);
            callback.onImagesFetched(mockImages);
            return null;
        }).when(firebaseOrganizerMock).getAllImages(any(FirebaseOrganizer.GetImagesCallback.class));

        // Simulate successful deletion
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteImageCallback callback = invocation.getArgument(1);
            callback.onSuccess();
            return null;
        }).when(firebaseOrganizerMock).deleteImage(eq("image1"), any(FirebaseOrganizer.DeleteImageCallback.class));

        ActivityScenario.launch(AdminImageActivity.class);

        // Click on the image to delete
        onView(withId(R.id.image_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for successful deletion toast
        onView(withText("Image deleted successfully"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * Test that an error message is shown when image deletion fails.
     */
    @Test
    public void testDeleteImage_Failure() {
        ArrayList<ImageData> mockImages = new ArrayList<>();
        ImageData image = new ImageData();
        image.setImageId("image1");
        image.setTitle("Sample Image");
        mockImages.add(image);

        // Simulate successful image fetch
        doAnswer(invocation -> {
            FirebaseOrganizer.GetImagesCallback callback = invocation.getArgument(0);
            callback.onImagesFetched(mockImages);
            return null;
        }).when(firebaseOrganizerMock).getAllImages(any(FirebaseOrganizer.GetImagesCallback.class));

        // Simulate deletion error
        doAnswer(invocation -> {
            FirebaseOrganizer.DeleteImageCallback callback = invocation.getArgument(1);
            callback.onError("Delete failed");
            return null;
        }).when(firebaseOrganizerMock).deleteImage(eq("image1"), any(FirebaseOrganizer.DeleteImageCallback.class));

        ActivityScenario.launch(AdminImageActivity.class);

        // Click on the image to delete
        onView(withId(R.id.image_list_view)).perform(click());

        // Confirm deletion in dialog
        onView(withText("Delete")).perform(click());

        // Check for deletion error toast
        onView(withText("Error deleting image: Delete failed"))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }
}

package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.FirebaseOrganizer;
import com.example.iconic_raffleevent.model.ImageData;

import java.util.ArrayList;

/**
 * AdminImageActivity provides an interface for managing images within the application.
 * This activity displays a list of images fetched from Firebase and allows administrators
 * to delete selected images.
 */
public class AdminImageActivity extends AppCompatActivity {
    private ListView imageListView;
    private ArrayAdapter<String> imageAdapter;
    private ArrayList<ImageData> imageList;
    private FirebaseOrganizer firebaseOrganizer;

    /**
     * Called when the activity is created. Sets up the layout and initializes the image list.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_image);

        imageListView = findViewById(R.id.image_list_view);
        firebaseOrganizer = new FirebaseOrganizer();
        loadImageList();
    }

    /**
     * Loads the list of images from Firebase and populates the ListView with image titles.
     * Sets up a click listener for each item in the list to allow for image deletion.
     */
    private void loadImageList() {
        firebaseOrganizer.getAllImages(new FirebaseOrganizer.GetImagesCallback() {
            @Override
            public void onImagesFetched(ArrayList<ImageData> images) {
                imageList = images;
                ArrayList<String> imageTitles = new ArrayList<>();
                for (ImageData image : images) {
                    imageTitles.add(image.getTitle());
                }
                imageAdapter = new ArrayAdapter<>(AdminImageActivity.this, android.R.layout.simple_list_item_1, imageTitles);
                imageListView.setAdapter(imageAdapter);
                imageListView.setOnItemClickListener((adapterView, view, i, l) -> showDeleteDialog(imageList.get(i)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminImageActivity.this, "Error loading images: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Shows a confirmation dialog for deleting a selected image.
     *
     * @param imageData The ImageData object representing the image to be deleted.
     */
    private void showDeleteDialog(ImageData imageData) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Delete", (dialog, which) -> deleteImage(imageData))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes the specified image from Firebase and refreshes the image list on success.
     *
     * @param imageData The ImageData object representing the image to delete.
     */
    private void deleteImage(ImageData imageData) {
        firebaseOrganizer.deleteImage(imageData.getImageId(), new FirebaseOrganizer.DeleteImageCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AdminImageActivity.this, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                loadImageList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AdminImageActivity.this, "Error deleting image: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

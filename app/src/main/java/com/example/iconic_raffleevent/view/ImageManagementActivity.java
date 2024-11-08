package com.example.iconic_raffleevent.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.iconic_raffleevent.R;
import com.example.iconic_raffleevent.controller.ImageController;
import com.example.iconic_raffleevent.model.ImageData;

import java.util.ArrayList;

public class ImageManagementActivity extends AppCompatActivity {

    private ListView imageListView;
    private ImageController imageController;
    private ArrayList<ImageData> imageList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> imageTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_management);

        imageListView = findViewById(R.id.image_list_view);
        imageController = new ImageController();

        loadImageList();
    }

    private void loadImageList() {
        imageController.getAllImages(new ImageController.ImageListCallback() {
            @Override
            public void onImagesFetched(ArrayList<ImageData> images) {
                imageList = images;
                imageTitles = new ArrayList<>();

                for (ImageData image : imageList) {
                    imageTitles.add(image.getTitle());
                }

                adapter = new ArrayAdapter<>(ImageManagementActivity.this, android.R.layout.simple_list_item_1, imageTitles);
                imageListView.setAdapter(adapter);

                imageListView.setOnItemClickListener((parent, view, position, id) -> showImageOptionsDialog(imageList.get(position)));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ImageManagementActivity.this, "Failed to load images: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageOptionsDialog(ImageData image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Manage Image")
                .setMessage("Would you like to remove this image?")
                .setPositiveButton("Remove", (dialog, which) -> removeImage(image))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void removeImage(ImageData image) {
        imageController.deleteImage(image.getImageId(), new ImageController.DeleteImageCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ImageManagementActivity.this, "Image removed successfully", Toast.LENGTH_SHORT).show();
                loadImageList();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ImageManagementActivity.this, "Failed to remove image: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
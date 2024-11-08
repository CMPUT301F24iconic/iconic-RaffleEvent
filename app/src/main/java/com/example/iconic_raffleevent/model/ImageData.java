package com.example.iconic_raffleevent.model;

/**
 * Represents image data, including an image identifier, title, and the URL to the image.
 */
public class ImageData {
    private String imageId;
    private String title;
    private String imageUrl;

    /**
     * Default constructor for creating an empty ImageData object.
     */
    public ImageData() {}

    /**
     * Constructor for creating an ImageData object with specified values.
     * @param imageId
     * This is the unique identifier for the image.
     * @param title
     * This is the title associated with the image.
     * @param imageUrl
     * This is the URL to access the image.
     */
    public ImageData(String imageId, String title, String imageUrl) {
        this.imageId = imageId;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the unique identifier of the image.
     * @return
     * Return the image ID as a String.
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Sets the unique identifier for the image.
     * @param imageId
     * This is the unique ID to assign to the image.
     */
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    /**
     * Gets the title associated with the image.
     * @return
     * Return the title of the image as a String.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for the image.
     * @param title
     * This is the title to assign to the image.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the URL of the image.
     * @return
     * Return the URL of the image as a String.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the URL for the image.
     * @param imageUrl
     * This is the URL to assign to the image.
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

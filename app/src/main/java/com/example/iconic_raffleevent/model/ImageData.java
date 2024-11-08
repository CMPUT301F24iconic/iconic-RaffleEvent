package com.example.iconic_raffleevent.model;

public class ImageData {

    private String imageId;
    private String title;
    private String imageUrl;

    public ImageData() {}

    public ImageData(String imageId, String title, String imageUrl) {
        this.imageId = imageId;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

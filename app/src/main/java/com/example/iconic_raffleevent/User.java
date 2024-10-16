package com.example.iconic_raffleevent;

public class User {
    private String name;
    private String email;
    private String phoneNumber;
    private String imgURL;
    private String deviceID;

    public User(String name, String email, String deviceID) {
        this.name = name;
        this.email = email;
        this.deviceID = deviceID;
        this.phoneNumber = null;

        // This will eventually be replaced to take in imgURL with the unique image generated
        this.imgURL = null;
    }

    public User(String name, String email, String phoneNumber, String deviceID) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;

        // This will eventually be replaced to take in imgURL with the unique image generated
        this.imgURL = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public String getEmail() {
        return email;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

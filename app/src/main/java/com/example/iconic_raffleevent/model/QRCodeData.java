package com.example.iconic_raffleevent.model;

public class QRCodeData {

    private String qrCodeId;
    private String qrCodeName;
    private String qrCodeUrl;

    // Empty constructor for Firestore
    public QRCodeData() {}

    public QRCodeData(String qrCodeId, String qrCodeName, String qrCodeUrl) {
        this.qrCodeId = qrCodeId;
        this.qrCodeName = qrCodeName;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getQrCodeId() {
        return qrCodeId;
    }

    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    public String getQrCodeName() {
        return qrCodeName;
    }

    public void setQrCodeName(String qrCodeName) {
        this.qrCodeName = qrCodeName;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}

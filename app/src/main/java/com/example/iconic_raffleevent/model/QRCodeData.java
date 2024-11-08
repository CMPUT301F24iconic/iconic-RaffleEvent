package com.example.iconic_raffleevent.model;

/**
 * Represents data for a QR code used in the raffle event.
 */
public class QRCodeData {

    private String qrCodeId;
    private String qrCodeName;
    private String qrCodeUrl;

    /**
     * Default constructor for Firestore serialization.
     */
    public QRCodeData() {}

    /**
     * Constructor to create a QRCodeData object with the specified details.
     * @param qrCodeId
     * This is the unique identifier for the QR code.
     * @param qrCodeName
     * This is the name of the QR code.
     * @param qrCodeUrl
     * This is the URL associated with the QR code.
     */
    public QRCodeData(String qrCodeId, String qrCodeName, String qrCodeUrl) {
        this.qrCodeId = qrCodeId;
        this.qrCodeName = qrCodeName;
        this.qrCodeUrl = qrCodeUrl;
    }

    /**
     * Gets the unique identifier of the QR code.
     * @return
     * Return the QR code ID as a String.
     */
    public String getQrCodeId() {
        return qrCodeId;
    }

    /**
     * Sets the unique identifier for the QR code.
     * @param qrCodeId
     * This is the unique ID to assign to the QR code.
     */
    public void setQrCodeId(String qrCodeId) {
        this.qrCodeId = qrCodeId;
    }

    /**
     * Gets the name of the QR code.
     * @return
     * Return the QR code name as a String.
     */
    public String getQrCodeName() {
        return qrCodeName;
    }

    /**
     * Sets the name for the QR code.
     * @param qrCodeName
     * This is the name to assign to the QR code.
     */
    public void setQrCodeName(String qrCodeName) {
        this.qrCodeName = qrCodeName;
    }

    /**
     * Gets the URL associated with the QR code.
     * @return
     * Return the QR code URL as a String.
     */
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    /**
     * Sets the URL for the QR code.
     * @param qrCodeUrl
     * This is the URL to assign to the QR code.
     */
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}


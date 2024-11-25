package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.QRCodeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QRCodeDataTest {
    private QRCodeData qrCodeData;
    private String testId = "qr123";
    private String testName = "EventQRCode";
    private String testUrl = "http://example.com/qrcode123.png";

    @BeforeEach
    void setUp() {
        qrCodeData = new QRCodeData(testId, testName, testUrl);
    }

    @Test
    void testGetQRCodeId() {
        assertEquals(testId, qrCodeData.getQrCodeId());
    }

    @Test
    void testSetQRCodeId() {
        String newId = "qr456";
        qrCodeData.setQrCodeId(newId);
        assertEquals(newId, qrCodeData.getQrCodeId());
    }

    @Test
    void testGetQRCodeName() {
        assertEquals(testName, qrCodeData.getQrCodeName());
    }

    @Test
    void testSetQRCodeName() {
        String newName = "UpdatedQRCode";
        qrCodeData.setQrCodeName(newName);
        assertEquals(newName, qrCodeData.getQrCodeName());
    }

    @Test
    void testGetQRCodeUrl() {
        assertEquals(testUrl, qrCodeData.getQrCodeUrl());
    }

    @Test
    void testSetQRCodeUrl() {
        String newUrl = "http://example.com/qrcode456.png";
        qrCodeData.setQrCodeUrl(newUrl);
        assertEquals(newUrl, qrCodeData.getQrCodeUrl());
    }

    @Test
    void testEqualsWithSameValues() {
        QRCodeData sameQRCodeData = new QRCodeData(testId, testName, testUrl);
        assertTrue(qrCodeData.equals(sameQRCodeData),
                "QRCodeData objects with same values should be equal");
        assertTrue(sameQRCodeData.equals(qrCodeData),
                "Equals should be symmetric");
    }

    @Test
    void testEqualsWithDifferentValues() {
        QRCodeData differentQRCodeData = new QRCodeData("qr789", "AnotherQRCode", "http://example.com/qrcode789.png");
        assertFalse(qrCodeData.equals(differentQRCodeData),
                "QRCodeData objects with different values should not be equal");
    }

    @Test
    void testEqualsWithSelf() {
        assertTrue(qrCodeData.equals(qrCodeData),
                "QRCodeData should be equal to itself");
    }

    @Test
    void testEqualsWithNull() {
        assertFalse(qrCodeData.equals(null),
                "QRCodeData should not be equal to null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertFalse(qrCodeData.equals("not a QRCodeData"),
                "QRCodeData should not be equal to other types");
    }

    @Test
    void testHashCodeConsistency() {
        QRCodeData sameQRCodeData = new QRCodeData(testId, testName, testUrl);
        assertEquals(qrCodeData.hashCode(), sameQRCodeData.hashCode(),
                "Hash codes should be same for equal objects");
    }

    @Test
    void testHashCodeDifference() {
        QRCodeData differentQRCodeData = new QRCodeData("qr789", "AnotherQRCode", "http://example.com/qrcode789.png");
        assertNotEquals(qrCodeData.hashCode(), differentQRCodeData.hashCode(),
                "Hash codes should be different for unequal objects");
    }

    @Test
    void testNullValues() {
        QRCodeData nullQRCodeData = new QRCodeData(null, null, null);
        assertNull(nullQRCodeData.getQrCodeId());
        assertNull(nullQRCodeData.getQrCodeName());
        assertNull(nullQRCodeData.getQrCodeUrl());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        QRCodeData emptyQRCodeData = new QRCodeData("", "", "");
        assertEquals("", emptyQRCodeData.getQrCodeId());
        assertEquals("", emptyQRCodeData.getQrCodeName());
        assertEquals("", emptyQRCodeData.getQrCodeUrl());
    }

    @Test
    void testHashCodeWithNullValues() {
        QRCodeData nullQRCodeData = new QRCodeData(null, null, null);
        assertDoesNotThrow(() -> nullQRCodeData.hashCode(),
                "hashCode should handle null values without throwing exception");
    }

    @Test
    void testPartialEquality() {
        // Same ID but different other fields
        QRCodeData sameIdDifferentFields = new QRCodeData(testId, "Different Name", "Different URL");
        assertFalse(qrCodeData.equals(sameIdDifferentFields),
                "Objects with same ID but different fields should not be equal");

        // Same everything except ID
        QRCodeData differentIdSameFields = new QRCodeData("different-id", testName, testUrl);
        assertFalse(qrCodeData.equals(differentIdSameFields),
                "Objects with different IDs should not be equal");
    }
}
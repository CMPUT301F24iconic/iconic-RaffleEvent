package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.QRCodeData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QRCodeDataTest {

    private QRCodeData qrCodeData;

    @BeforeEach
    void setUp() {
        qrCodeData = new QRCodeData("qr123", "EventQRCode", "http://example.com/qrcode123.png");
    }

    @Test
    void testGetQRCodeId() {
        assertEquals("qr123", qrCodeData.getQrCodeId());
    }

    @Test
    void testSetQRCodeId() {
        qrCodeData.setQrCodeId("qr456");
        assertEquals("qr456", qrCodeData.getQrCodeId());
    }

    @Test
    void testGetQRCodeName() {
        assertEquals("EventQRCode", qrCodeData.getQrCodeName());
    }

    @Test
    void testSetQRCodeName() {
        qrCodeData.setQrCodeName("UpdatedQRCode");
        assertEquals("UpdatedQRCode", qrCodeData.getQrCodeName());
    }

    @Test
    void testGetQRCodeUrl() {
        assertEquals("http://example.com/qrcode123.png", qrCodeData.getQrCodeUrl());
    }

    @Test
    void testSetQRCodeUrl() {
        qrCodeData.setQrCodeUrl("http://example.com/qrcode456.png");
        assertEquals("http://example.com/qrcode456.png", qrCodeData.getQrCodeUrl());
    }

    @Test
    void testEquals() {
        QRCodeData sameQRCodeData = new QRCodeData("qr123", "EventQRCode", "http://example.com/qrcode123.png");
        QRCodeData differentQRCodeData = new QRCodeData("qr789", "AnotherQRCode", "http://example.com/qrcode789.png");
        assertEquals(qrCodeData, sameQRCodeData);
        assertNotEquals(qrCodeData, differentQRCodeData);
    }
}

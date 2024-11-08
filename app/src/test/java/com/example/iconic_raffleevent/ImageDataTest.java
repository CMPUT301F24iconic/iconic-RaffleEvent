package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.ImageData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageDataTest {

    private ImageData imageData;

    @BeforeEach
    void setUp() {
        imageData = new ImageData("img123", "Sunset", "http://example.com/sunset.jpg");
    }

    @Test
    void testGetImageId() {
        assertEquals("img123", imageData.getImageId());
    }

    @Test
    void testSetImageId() {
        imageData.setImageId("img456");
        assertEquals("img456", imageData.getImageId());
    }

    @Test
    void testGetTitle() {
        assertEquals("Sunset", imageData.getTitle());
    }

    @Test
    void testSetTitle() {
        imageData.setTitle("Sunrise");
        assertEquals("Sunrise", imageData.getTitle());
    }

    @Test
    void testGetImageUrl() {
        assertEquals("http://example.com/sunset.jpg", imageData.getImageUrl());
    }

    @Test
    void testSetImageUrl() {
        imageData.setImageUrl("http://example.com/sunrise.jpg");
        assertEquals("http://example.com/sunrise.jpg", imageData.getImageUrl());
    }

    @Test
    void testEquals() {
        ImageData sameImageData = new ImageData("img123", "Sunset", "http://example.com/sunset.jpg");
        ImageData differentImageData = new ImageData("img789", "Mountain", "http://example.com/mountain.jpg");
        assertEquals(imageData, sameImageData);
        assertNotEquals(imageData, differentImageData);
    }
}

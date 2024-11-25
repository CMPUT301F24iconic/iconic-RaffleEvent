package com.example.iconic_raffleevent;

import com.example.iconic_raffleevent.model.ImageData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageDataTest {

    private ImageData imageData;
    private String testId = "img123";
    private String testTitle = "Sunset";
    private String testUrl = "http://example.com/sunset.jpg";

    @BeforeEach
    void setUp() {
        imageData = new ImageData(testId, testTitle, testUrl);
    }

    @Test
    void testGetImageId() {
        assertEquals(testId, imageData.getImageId());
    }

    @Test
    void testSetImageId() {
        String newId = "img456";
        imageData.setImageId(newId);
        assertEquals(newId, imageData.getImageId());
    }

    @Test
    void testGetTitle() {
        assertEquals(testTitle, imageData.getTitle());
    }

    @Test
    void testSetTitle() {
        String newTitle = "Sunrise";
        imageData.setTitle(newTitle);
        assertEquals(newTitle, imageData.getTitle());
    }

    @Test
    void testGetImageUrl() {
        assertEquals(testUrl, imageData.getImageUrl());
    }

    @Test
    void testSetImageUrl() {
        String newUrl = "http://example.com/sunrise.jpg";
        imageData.setImageUrl(newUrl);
        assertEquals(newUrl, imageData.getImageUrl());
    }

    @Test
    void testEqualsWithSameValues() {
        ImageData sameImageData = new ImageData(testId, testTitle, testUrl);
        assertTrue(imageData.equals(sameImageData), "Two ImageData objects with same values should be equal");
    }

    @Test
    void testEqualsWithDifferentValues() {
        ImageData differentImageData = new ImageData("img789", "Mountain", "http://example.com/mountain.jpg");
        assertFalse(imageData.equals(differentImageData), "ImageData objects with different values should not be equal");
    }

    @Test
    void testEqualsWithSelf() {
        assertTrue(imageData.equals(imageData), "ImageData should be equal to itself");
    }

    @Test
    void testEqualsWithNull() {
        assertFalse(imageData.equals(null), "ImageData should not be equal to null");
    }

    @Test
    void testEqualsWithDifferentClass() {
        assertFalse(imageData.equals("not an ImageData"), "ImageData should not be equal to other types");
    }

    @Test
    void testHashCodeConsistency() {
        ImageData sameImageData = new ImageData(testId, testTitle, testUrl);
        assertEquals(imageData.hashCode(), sameImageData.hashCode(),
                "Hash codes should be same for equal objects");
    }

    @Test
    void testNullValues() {
        ImageData nullImageData = new ImageData(null, null, null);
        assertNull(nullImageData.getImageId());
        assertNull(nullImageData.getTitle());
        assertNull(nullImageData.getImageUrl());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        ImageData emptyImageData = new ImageData("", "", "");
        assertEquals("", emptyImageData.getImageId());
        assertEquals("", emptyImageData.getTitle());
        assertEquals("", emptyImageData.getImageUrl());
    }

    @Test
    void testHashCodeWithNullValues() {
        ImageData nullImageData = new ImageData(null, null, null);
        assertDoesNotThrow(() -> nullImageData.hashCode(),
                "hashCode should handle null values without throwing exception");
    }
}
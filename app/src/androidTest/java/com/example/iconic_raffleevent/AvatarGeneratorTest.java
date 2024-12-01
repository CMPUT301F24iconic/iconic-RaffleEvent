package com.example.iconic_raffleevent;

import android.graphics.Bitmap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit test class for the AvatarGenerator utility.
 */
public class AvatarGeneratorTest {

    /**
     * Test to verify that an avatar is generated successfully with initials.
     */
    @Test
    public void testGenerateAvatarWithName() {
        String testName = "John Doe";
        int size = 100;

        Bitmap avatar = AvatarGenerator.generateAvatar(testName, size);

        // Assert that the avatar is not null
        assertNotNull("Avatar bitmap should not be null", avatar);

        // Assert that the avatar is of the correct size
        assertEquals("Avatar width should match the size", size, avatar.getWidth());
        assertEquals("Avatar height should match the size", size, avatar.getHeight());
    }

    /**
     * Test to verify avatar generation for a null or empty name.
     */
    @Test
    public void testGenerateAvatarWithNullName() {
        int size = 100;

        Bitmap avatar = AvatarGenerator.generateAvatar(null, size);

        // Assert that the avatar is not null
        assertNotNull("Avatar bitmap should not be null for null name", avatar);

        // Assert that the avatar is of the correct size
        assertEquals("Avatar width should match the size", size, avatar.getWidth());
        assertEquals("Avatar height should match the size", size, avatar.getHeight());
    }

    /**
     * Test to verify that the generated initials are correct.
     */
    @Test
    public void testGetInitials() {
        String testName = "John Doe";

        // Use reflection to test private method
        String initials = invokeGetInitials(testName);

        assertEquals("Initials should be 'JD' for 'John Doe'", "JD", initials);
    }

    /**
     * Utility method to invoke the private getInitials method using reflection.
     *
     * @param name Name to generate initials for.
     * @return The generated initials.
     */
    private String invokeGetInitials(String name) {
        try {
            java.lang.reflect.Method method = AvatarGenerator.class.getDeclaredMethod("getInitials", String.class);
            method.setAccessible(true);
            return (String) method.invoke(null, name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke getInitials method", e);
        }
    }
}

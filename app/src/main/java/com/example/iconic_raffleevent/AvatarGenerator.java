package com.example.iconic_raffleevent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Utility class for generating circular avatar images with initials.
 * This class provides methods to create avatars based on a user's name, generating a unique color and initials
 * for each avatar. It draws a colored circle with initials centered within it.
 */
public class AvatarGenerator {

    /**
     * Generates a circular avatar bitmap with the initials of the provided name.
     *
     * @param name The name from which to generate the avatar initials. If null or empty, an empty avatar will be created.
     * @param size The size (width and height) of the generated avatar bitmap in pixels.
     * @return A {@link Bitmap} representing the generated avatar image.
     */
    @NonNull
    public static Bitmap generateAvatar(@Nullable String name, int size) {
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getColorForName(name));

        canvas.drawOval(new RectF(0, 0, size, size), paint);

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(size * 0.5f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String initials = getInitials(name);
        canvas.drawText(initials, size / 2f, size / 2f - ((textPaint.descent() + textPaint.ascent()) / 2f), textPaint);

        return bitmap;
    }

    /**
     * Extracts the initials from the provided name.
     *
     * @param name The name from which to extract initials. If null or empty, an empty string is returned.
     * @return A {@link String} containing the initials of the name in uppercase. For example, "John Doe" becomes "JD".
     */
    @NonNull
    private static String getInitials(@Nullable String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        String[] names = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String n : names) {
            if (!n.isEmpty()) {
                initials.append(n.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    /**
     * Generates a unique color based on the provided name.
     *
     * @param name The name used to generate a color. If null, a default color is used.
     * @return An integer representing the RGB color generated for the name.
     */
    private static int getColorForName(@Nullable String name) {
        int hash = (name != null) ? name.hashCode() : 0;
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        return Color.rgb(r, g, b);
    }
}

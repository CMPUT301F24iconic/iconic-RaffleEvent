package com.example.iconic_raffleevent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class AvatarGenerator {

    public static Bitmap generateAvatar(String name, int size) {
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

    private static String getInitials(String name) {
        String[] names = name.split(" ");
        StringBuilder initials = new StringBuilder();
        for (String n : names) {
            if (!n.isEmpty()) {
                initials.append(n.charAt(0));
            }
        }
        return initials.toString().toUpperCase();
    }

    private static int getColorForName(String name) {
        int hash = name.hashCode();
        int r = (hash & 0xFF0000) >> 16;
        int g = (hash & 0x00FF00) >> 8;
        int b = hash & 0x0000FF;
        return Color.rgb(r, g, b);
    }
}
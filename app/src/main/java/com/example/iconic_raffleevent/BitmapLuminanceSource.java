package com.example.iconic_raffleevent;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

public class BitmapLuminanceSource extends LuminanceSource {
    private final byte[] luminances;

    public BitmapLuminanceSource(Bitmap bitmap) {
        super(bitmap.getWidth(), bitmap.getHeight());

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        luminances = new byte[width * height];
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;
            luminances[i] = (byte) ((r + g + b) / 3);
        }
    }

    @Override
    public byte[] getMatrix() {
        return luminances;
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        System.arraycopy(luminances, y * getWidth(), row, 0, getWidth());
        return row;
    }
}

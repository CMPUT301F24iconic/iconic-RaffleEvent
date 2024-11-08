package com.example.iconic_raffleevent;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.zxing.LuminanceSource;

/**
 * A custom LuminanceSource implementation that extracts luminance (brightness) data from a given Bitmap.
 * This class converts the pixel data of the bitmap to grayscale luminance values, which are used by
 * ZXing library for barcode processing.
 */
public class BitmapLuminanceSource extends LuminanceSource {

    private final byte[] luminances;

    /**
     * Constructs a new BitmapLuminanceSource from a Bitmap, processing it to extract grayscale
     * luminance data.
     *
     * @param bitmap The input {@link Bitmap} from which luminance data will be extracted.
     */
    public BitmapLuminanceSource(@NonNull Bitmap bitmap) {
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
            luminances[i] = (byte) ((r + g + b) / 3);  // Calculate grayscale value as average of RGB
        }
    }

    /**
     * Returns the complete luminance data array for the entire image.
     *
     * @return A byte array containing luminance values for each pixel in the image.
     */
    @NonNull
    @Override
    public byte[] getMatrix() {
        return luminances;
    }

    /**
     * Returns the luminance data for a specific row in the image.
     *
     * @param y   The row index, starting from 0.
     * @param row A preallocated byte array to hold the luminance values of the row.
     *            If null, a new array will be created to hold the row's data.
     * @return A byte array containing luminance values for the specified row.
     */
    @NonNull
    @Override
    public byte[] getRow(int y, @Nullable byte[] row) {
        if (row == null || row.length < getWidth()) {
            row = new byte[getWidth()];
        }
        System.arraycopy(luminances, y * getWidth(), row, 0, getWidth());
        return row;
    }
}

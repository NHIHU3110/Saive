package com.example.saive.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;
import com.example.saive.R;

public class ImageUtils {

    private static final LruCache<Integer, Bitmap> memoryCache;

    static {
        // Use 1/8th of the available memory for this memory cache.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void setSafeImage(ImageView imageView, int resId) {
        if (imageView == null) return;
        
        // Check cache first
        Bitmap cachedBitmap = memoryCache.get(resId);
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }

        Context context = imageView.getContext();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), resId, options);

            // Calculate inSampleSize with a conservative target size
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024);
            options.inJustDecodeBounds = false;
            
            // Disable scaling as we handle it with inSampleSize
            options.inScaled = false;
            
            // Use RGB_565 to reduce memory usage by 50%
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
            if (bitmap != null) {
                memoryCache.put(resId, bitmap);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setImageResource(resId);
            }
        } catch (OutOfMemoryError | Exception e) {
            try {
                imageView.setImageResource(R.drawable.ic_launcher_background);
            } catch (Exception ignored) {}
        }
    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        // Safety check: Android Hardware Canvas has limits (usually 4096 or 8192)
        // We ensure no dimension exceeds 4096 and total pixels are within safe bounds (~2M pixels)
        final int MAX_CANVAS_DIMENSION = 4096;
        while ((width / inSampleSize) > MAX_CANVAS_DIMENSION || 
               (height / inSampleSize) > MAX_CANVAS_DIMENSION ||
               ((width / inSampleSize) * (height / inSampleSize)) > 2000000) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }
}

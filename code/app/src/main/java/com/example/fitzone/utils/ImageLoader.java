package com.example.fitzone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.example.fitzone.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ImageLoader {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private ImageLoader() {
    }

    public static void charger(String url, ImageView cible) {
        cible.setImageResource(R.drawable.ic_profile_placeholder);
        cible.setTag(url);
        if (url == null || (!url.startsWith("https://") && !url.startsWith("http://"))) {
            return;
        }

        EXECUTOR.execute(() -> {
            HttpURLConnection connexion = null;
            try {
                connexion = (HttpURLConnection) new URL(url).openConnection();
                connexion.setConnectTimeout(7000);
                connexion.setReadTimeout(7000);
                connexion.setDoInput(true);
                connexion.connect();
                if (connexion.getResponseCode() >= 400) {
                    return;
                }
                try (InputStream entree = connexion.getInputStream()) {
                    Bitmap image = BitmapFactory.decodeStream(entree);
                    if (image != null) {
                        MAIN_HANDLER.post(() -> {
                            if (url.equals(cible.getTag())) {
                                cible.setImageBitmap(image);
                            }
                        });
                    }
                }
            } catch (Exception ignored) {
            } finally {
                if (connexion != null) {
                    connexion.disconnect();
                }
            }
        });
    }
}

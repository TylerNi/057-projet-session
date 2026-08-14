package com.example.fitzone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.example.fitzone.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ImageLoader {

    private ImageLoader() {
    }

    public static void charger(String url, ImageView cible) {
        cible.setImageResource(R.drawable.ic_profile_placeholder);
        cible.setTag(url);
        if (url == null || (!url.startsWith("https://") && !url.startsWith("http://"))) {
            return;
        }

        new Thread(() -> {
            HttpURLConnection connexion = null;
            try {
                connexion = (HttpURLConnection) new URL(url).openConnection();
                connexion.setConnectTimeout(5000);
                connexion.setReadTimeout(5000);
                if (connexion.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                    try (InputStream entree = connexion.getInputStream()) {
                        Bitmap image = BitmapFactory.decodeStream(entree);
                        if (image != null) {
                            cible.post(() -> {
                                if (url.equals(cible.getTag())) {
                                    cible.setImageBitmap(image);
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                Log.w("ImageLoader", "Impossible de charger l'image", e);
            } finally {
                if (connexion != null) {
                    connexion.disconnect();
                }
            }
        }).start();
    }
}

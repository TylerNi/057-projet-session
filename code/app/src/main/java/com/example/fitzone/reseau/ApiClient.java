package com.example.fitzone.reseau;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    public static final String BASE_URL = "http://10.0.2.2:3000";

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void get(String path, ApiCallback callback) {
        envoyer("GET", path, null, callback);
    }

    public static void post(String path, JSONObject body, ApiCallback callback) {
        envoyer("POST", path, body, callback);
    }

    public static void put(String path, JSONObject body, ApiCallback callback) {
        envoyer("PUT", path, body, callback);
    }

    private static void envoyer(String methode, String path, JSONObject body, ApiCallback callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection connexion = null;
            try {
                URL url = new URL(BASE_URL + path);
                connexion = (HttpURLConnection) url.openConnection();
                connexion.setRequestMethod(methode);
                connexion.setConnectTimeout(10000);
                connexion.setReadTimeout(10000);

                if (body != null) {
                    connexion.setRequestProperty("Content-Type", "application/json");
                    connexion.setDoOutput(true);
                    OutputStream sortie = connexion.getOutputStream();
                    sortie.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    sortie.close();
                }

                int code = connexion.getResponseCode();
                InputStream entree = code >= 400 ? connexion.getErrorStream() : connexion.getInputStream();
                String reponse = lire(entree);

                if (code >= 400) {
                    envoyerErreur(callback, "Erreur " + code);
                } else {
                    MAIN_HANDLER.post(() -> callback.onSuccess(reponse));
                }
            } catch (Exception e) {
                envoyerErreur(callback, "Impossible de joindre le serveur");
            } finally {
                if (connexion != null) {
                    connexion.disconnect();
                }
            }
        });
    }

    private static String lire(InputStream entree) throws Exception {
        if (entree == null) {
            return "";
        }
        BufferedReader lecteur = new BufferedReader(new InputStreamReader(entree, StandardCharsets.UTF_8));
        StringBuilder contenu = new StringBuilder();
        String ligne;
        while ((ligne = lecteur.readLine()) != null) {
            contenu.append(ligne);
        }
        lecteur.close();
        return contenu.toString();
    }

    private static void envoyerErreur(ApiCallback callback, String message) {
        MAIN_HANDLER.post(() -> callback.onError(message));
    }
}

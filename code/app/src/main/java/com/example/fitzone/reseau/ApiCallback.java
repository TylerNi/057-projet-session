package com.example.fitzone.reseau;

public interface ApiCallback {

    void onSuccess(String body);

    void onError(String message);
}

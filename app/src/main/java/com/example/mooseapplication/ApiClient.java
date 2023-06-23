package com.example.mooseapplication;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static final String BASE_URL = "";
    private final OkHttpClient httpClient;

    public ApiClient() {
        httpClient = MyOkHttpClient.getInstance();
    }

    public void makeApiCall() {
        Request request = new Request.Builder()
                .url(BASE_URL + "")
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
    }
}

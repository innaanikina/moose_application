package com.example.mooseapplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyOkHttpClient {
    private static volatile OkHttpClient instance;

    private MyOkHttpClient() {

    }

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (MyOkHttpClient.class) {
                if (instance == null) {
                    instance = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return instance;
    }
}

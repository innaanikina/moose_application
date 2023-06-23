package com.example.mooseapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PredictionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PredictionAdapter adapter;
    private List<Prediction> predictionList;
    private static final String BASE_URL = "http://10.0.2.2:5000/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //predictionList = (List<Prediction>) getIntent().getSerializableExtra("MyPredictions");
        predictionList = new ArrayList<>();
        adapter = new PredictionAdapter(predictionList, this);
        recyclerView.setAdapter(adapter);

        String predictionsJson = getIntent().getStringExtra("MyPredictions");
        if (predictionsJson == null) {
            getPredictions();
        } else {
            processPredictionsResults(predictionsJson);
        }

//        if (predictionList == null) {
//            predictionList = new ArrayList<>();
//            adapter = new PredictionAdapter(predictionList, this);
//            recyclerView.setAdapter(adapter);
//            getPredictions();
//        } else {
//            adapter = new PredictionAdapter(predictionList, this);
//            recyclerView.setAdapter(adapter);
//        }



//        getPredictions();
    }

    private void getPredictions() {
        OkHttpClient client = MyOkHttpClient.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        String password = sharedPreferences.getString("password", "Pwd");

        String credentials = Credentials.basic(username, password);

        Request request = new Request.Builder()
                .url(BASE_URL + "all")
                .header("Connection", "close")
                .header("Authorization", credentials)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        onFailure(call, new IOException("Unexpected code: " + response));
                        response.close();
                    }

                InputStream inputStream = response.body().byteStream();
                StringBuilder jsonBuilder = new StringBuilder();

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String jsonChunk = new String(buffer, 0, bytesRead);
                    jsonBuilder.append(jsonChunk);
                }

                String res = jsonBuilder.toString();
                Log.e("Res", res);

//                try {
//                    JSONObject json = new JSONObject(res);
//                    Log.e("JSON predictions", json.getString("predictions"));
//                    Prediction[] preds = new Gson().fromJson(json.getString("predictions"), Prediction[].class);
//                    Log.e("GSON", Arrays.toString(preds));
//                    Log.e("GSON length", String.valueOf(preds.length));
//                    Log.e("GSON length", preds[0].toString());
//                    predictionList.clear();
//                    predictionList.addAll(Arrays.asList(new Gson().fromJson(json.getString("predictions"), Prediction[].class)));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                    }
//                });
                processPredictionsAll(res);
                response.close();
            }
        });
    }

    private void processPredictionsAll(String jsonPredictions) {
        try {
            JSONObject json = new JSONObject(jsonPredictions);
            Log.e("JSON predictions", json.getString("predictions"));
            Prediction[] preds = new Gson().fromJson(json.getString("predictions"), Prediction[].class);
            Log.e("GSON", Arrays.toString(preds));
            Log.e("GSON length", String.valueOf(preds.length));
            Log.e("GSON length", preds[0].toString());
            predictionList.clear();
            predictionList.addAll(Arrays.asList(new Gson().fromJson(json.getString("predictions"), Prediction[].class)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void processPredictionsResults(String jsonPredictions) {
        Prediction[] preds = new Gson().fromJson(jsonPredictions, Prediction[].class);
        Log.e("GSON", Arrays.toString(preds));
        Log.e("GSON length", String.valueOf(preds.length));
        Log.e("GSON length", preds[0].toString());
        predictionList.clear();
        predictionList.addAll(Arrays.asList(preds));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}

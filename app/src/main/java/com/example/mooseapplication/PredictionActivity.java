package com.example.mooseapplication;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
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
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PredictionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PredictionAdapter adapter;
    private List<Prediction> predictionList;
    private static final String BASE_URL = "37.139.33.16:5000";
    private static final int BASE_PORT = 5000;
    @Override
    protected void onResume() {
        Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);

        super.onResume();
        setContentView(R.layout.activity_prediction);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        predictionList = new ArrayList<>();
        adapter = new PredictionAdapter(predictionList, this);

        String cameraId = getIntent().getStringExtra("cameraId");
        String fromDate = getIntent().getStringExtra("fromDate");
        String toDate = getIntent().getStringExtra("toDate");
        if (cameraId == null) {
            getPredictions();
        } else {
            getFilteredPredictions(cameraId, fromDate, toDate);
        }

        recyclerView.setAdapter(adapter);
    }

    private void getPredictions() {
        OkHttpClient client = MyOkHttpClient.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        String password = sharedPreferences.getString("password", "Pwd");

        String credentials = Credentials.basic(username, password);

        Request request = new Request.Builder()
                .url("http://" + BASE_URL + ":" + BASE_PORT + "/all")
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
                try {
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

                    processPredictionsAll(res);
                } catch (IOException e) {}
                finally {
                    response.close();
                }
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
            predictionList.addAll(Arrays.asList(new Gson().fromJson(json.getString("predictions"), Prediction[].class)));
            int newLength = predictionList.size();
            notifyAdded(newLength);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getFilteredPredictions(String cameraIdStr, String fromDateStr, String toDateStr) {
        OkHttpClient client = MyOkHttpClient.getInstance();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_URL)
                .port(BASE_PORT)
                .addPathSegment("results")
                .addQueryParameter("cameraId", cameraIdStr)
                .addQueryParameter("dateFrom", fromDateStr)
                .addQueryParameter("dateTo", toDateStr)
                .build();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        String password = sharedPreferences.getString("password", "Pwd");

        String credentials = Credentials.basic(username, password);

        Request request = new Request.Builder()
                .url(url)
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
                try {
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
                    processPredictionsResults(res);
                } catch (IOException e) {}
                finally {
                    response.close();
                }
            }
        });
    }

    private void processPredictionsResults(String jsonPredictions) {
        Prediction[] preds = new Gson().fromJson(jsonPredictions, Prediction[].class);
        Log.e("GSON", Arrays.toString(preds));
        Log.e("GSON length", String.valueOf(preds.length));
        predictionList.addAll(Arrays.asList(preds));
        int newLength = predictionList.size();
        notifyAdded(newLength);
    }

    private void notifyCleared(int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (size > 0) {
                    predictionList.clear();
                    adapter.notifyItemRangeRemoved(0, size);
                }
            }
        });
    }

    private void notifyAdded(int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        notifyCleared(predictionList.size());
    }
}

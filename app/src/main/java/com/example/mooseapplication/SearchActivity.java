package com.example.mooseapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText cameraId;
    private EditText fromDate;
    private EditText toDate;
    //private List<Prediction> predictionList = new ArrayList<>();
    private String predictions;
    private static final String BASE_URL = "10.0.2.2";
    private static final int BASE_PORT = 5000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        cameraId = findViewById(R.id.camera_id_edit_text);
        fromDate = findViewById(R.id.from_date_edit_text);
        toDate = findViewById(R.id.to_date_edit_text);

        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cameraIdStr = cameraId.getText().toString();
                String fromDateStr = fromDate.getText().toString();
                String toDateStr = toDate.getText().toString();

                getPredictions(cameraIdStr);
                Intent intent = new Intent(SearchActivity.this, PredictionActivity.class);
                intent.putExtra("MyPredictions", predictions);
                startActivity(intent);
            }
        });
    }

    private void getPredictions(String cameraIdStr) {
        OkHttpClient client = MyOkHttpClient.getInstance();

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(BASE_URL)
                .port(BASE_PORT)
                .addPathSegment("results")
                .addQueryParameter("camera_id", cameraIdStr)
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
                predictions = res;


//                    JSONObject json = new JSONObject(res);
//                    Log.e("JSON predictions", json.getString(""));
//                Prediction[] preds = new Gson().fromJson(res, Prediction[].class);
//                Log.e("GSON", Arrays.toString(preds));
//                Log.e("GSON length", String.valueOf(preds.length));
//                Log.e("GSON length", preds[0].toString());
//                predictionList.clear();
//                predictionList.addAll(Arrays.asList(preds));


                response.close();
            }
        });
    }
}

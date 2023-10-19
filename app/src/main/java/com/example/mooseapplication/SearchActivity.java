package com.example.mooseapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {
    private EditText cameraId;
    private EditText fromDate;
    private EditText toDate;


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


                Intent intent = new Intent(SearchActivity.this, PredictionActivity.class);

                intent.putExtra("cameraId", cameraIdStr);
                intent.putExtra("fromDate", fromDateStr);
                intent.putExtra("toDate", toDateStr);
                startActivity(intent);
            }
        });
    }
}

package com.example.mooseapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadActivity extends AppCompatActivity {

    private EditText filenameEditText;
    private ImageView imageView;
    private Button chooseButton;
    private Button uploadButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        filenameEditText = findViewById(R.id.filename_edit_text);
        imageView = findViewById(R.id.image_view);

        chooseButton = findViewById(R.id.choose_button);
        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        uploadButton = findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filename = filenameEditText.getText().toString().trim();
                if (TextUtils.isEmpty(filename)) {
                    filenameEditText.setError("Please enter a filename");
                    return;
                }
//
//                if (selectedFilePath == null) {
//                    Toast.makeText(getApplicationContext(), "Please select a file",
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
                //upload
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 100);
        } catch (Exception e) {
            Toast.makeText(this, "Please, install a file manager.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            Log.e("path: ", path);
            File file = new File(path);

            filenameEditText.setText(path);
            Log.e("filename: ", file.getName());


            Bitmap img = getImageFromUri(uri);
            if (img == null) {
                Log.e("IMG", "it's null");
            } else {
                imageView.setImageBitmap(img);
                //TODO do smth with the img bitmap - encode to base64?
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getImageFromUri(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();

            InputStream is = contentResolver.openInputStream(uri);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }

            byte[] bytes = os.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            is.close();
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

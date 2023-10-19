package com.example.mooseapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;


public class UploadActivity extends AppCompatActivity {

    private EditText filenameEditText;
    private ImageView imageView;
    private Button chooseButton;
    private Button uploadButton;
    private Bitmap img;
    private Uri imgUri;


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
                if (TextUtils.isEmpty(filename) || img == null) {
                    filenameEditText.setError("Please select a file");
                    return;
                }
                String encodedImg = convertImgToBase64(img);
                dumpImageMetaData(imgUri, getContentResolver().query(imgUri, null, null, null, null));
                String realPath = getRealPathFromURI_API19(getApplicationContext(), imgUri);
                Log.e("URI", realPath);
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
            imgUri = uri;
            String path = uri.getPath();
            Log.e("path: ", path);
            File file = new File(path);

            filenameEditText.setText(path);
            Log.e("filename: ", file.getName());


            img = getImageFromUri(uri);
            if (img == null) {
                Log.e("IMG", "it's null");
            } else {
                imageView.setImageBitmap(img);

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

    private String convertImgToBase64(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    private void uploadImg() {
        OkHttpClient client = MyOkHttpClient.getInstance();

        RequestBody formBody = new FormBody.Builder()
                .build();
    }

    private void dumpImageMetaData(Uri uri, Cursor cursor) {
        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                Log.i("ASD", "Display Name: " + displayName);

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null. But because an
                // int can't be null, the behavior is implementation-specific,
                // and unpredictable. So as
                // a rule, check if it's null before assigning to an int. This will
                // happen often: The storage API allows for remote files, whose
                // size might not be locally known.
                String size = null;
                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
                Log.i("ASD","Size: " + size);
            }
        } finally {
            cursor.close();
        }


    }

    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }
}

package com.example.mooseapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    Button uploadButton;
    Button predictionButton;
    Button searchButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        uploadButton = findViewById(R.id.upload_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("nav", "UploadActivity chosen");
                Intent intentUpload = new Intent(HomeActivity.this, UploadActivity.class);
                startActivity(intentUpload);
            }
        });
        predictionButton = findViewById(R.id.predictions_button);
        predictionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("nav", "PredicitonsActivity chosen");
                Intent intentPred = new Intent(HomeActivity.this, PredictionActivity.class);
                startActivity(intentPred);
            }
        });
        searchButton = findViewById(R.id.search_button_home);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("nav", "SearchActivity chosen");
                Intent intentSearch = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intentSearch);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView usernameView = headerView.findViewById(R.id.name_text_view);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        usernameView.setText("Добро пожаловать, " + username + "!");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logout:
                        //TODO logout user and redirect to LoginActivity
                        Log.i("nav", "LogoutActivity chosen");
                        Intent intentLogout = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(intentLogout);
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

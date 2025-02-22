package com.example.buddypunchclone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IPSLiveFeedMainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CameraAdapter adapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipslive_feed_main);

        // Initialize views
        recyclerView = findViewById(R.id.cameraRecyclerView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Setup Navigation Drawer
        setupNavigationDrawer();

        // Setup Bottom Navigation
        setupBottomNavigation();

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize camera list
        List<Camera> cameras = new ArrayList<>();
        cameras.add(new Camera("IPS Camera 1", "http://1234567:1234567@172.16.30.149:8080/video"));
        cameras.add(new Camera("IPS Camera 2", "http://1234567:1234567@172.16.30.150:8080/video"));

        // Setup adapter
        adapter = new CameraAdapter(this, cameras);
        recyclerView.setAdapter(adapter);
    }

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(IPSLiveFeedMainActivity.this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(IPSLiveFeedMainActivity.this, EmployeesActivity.class));
                finish();
            } else if (itemId == R.id.MapId) {
                startActivity(new Intent(IPSLiveFeedMainActivity.this, GeofencingActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(IPSLiveFeedMainActivity.this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.nav_grouping_attendance) {
                startActivity(new Intent(IPSLiveFeedMainActivity.this, AttendanceGroupingMainActivity.class));
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            // Add navigation logic for bottom menu items
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(this, EmployeesActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.MapId) {
                // Already on this activity
                return true;
            }
            return true;
        });
    }

    private void handleLogout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to login activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle drawer toggle
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
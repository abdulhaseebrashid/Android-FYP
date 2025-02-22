// SecondActivity.java
package com.example.buddypunchclone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SecondActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Start WSMainActivity in the background
        Intent wsIntent = new Intent(this, WSMainActivity.class);
        startActivity(wsIntent);

        // Initialize views and setup UI
        initializeViews();
        setupNavigationHeader();
        setupFragmentMap();
        setupNavigationListeners();
        setupDrawerToggle();

        // Load Dashboard fragment initially
        if (savedInstanceState == null) {
            loadFragment(R.id.nav_dashboard);
            // Set the default selected item in bottom navigation
            bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        }
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView emailTextView = headerView.findViewById(R.id.login_user_email);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "info@example.com");

        emailTextView.setText(userEmail);
    }

    private void setupFragmentMap() {
        fragmentMap.put(R.id.nav_dashboard, new DashboardFragment());
    }

    private void setupDrawerToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavigationListeners() {
        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            handleNavigationItem(item.getItemId());
            return true;
        });

        // Drawer Navigation Listener
        navigationView.setNavigationItemSelectedListener(item -> {
            handleNavigationItem(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void handleNavigationItem(int itemId) {
        if (itemId == R.id.MapId) {
            startActivity(new Intent(this, GeofencingActivity.class));
        } else if (itemId == R.id.nav_Employees) {
            startActivity(new Intent(this, EmployeesActivity.class));
        } else if (itemId == R.id.nav_Attendace) {
            startActivity(new Intent(this, AttendanceActivity.class));
        } else if (itemId == R.id.nav_logout) {
            handleLogout();
        }
//        else if (itemId == R.id.nav_more) {
//            if (!drawerLayout.isDrawerOpen(navigationView)) {
//                drawerLayout.openDrawer(navigationView);
//            }
//        }
        else {
            Fragment selectedFragment = fragmentMap.get(itemId);
            if (selectedFragment != null) {
                loadFragment(itemId);
            }
        }
    }

    private void handleLogout() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("auth_token");
        editor.remove("user_email");
        editor.apply();

        startActivity(intent);
        finish();
    }

    private void loadFragment(int fragmentId) {
        Fragment selectedFragment = fragmentMap.get(fragmentId);
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, selectedFragment)
                    .commit();
            navigationView.setCheckedItem(fragmentId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}
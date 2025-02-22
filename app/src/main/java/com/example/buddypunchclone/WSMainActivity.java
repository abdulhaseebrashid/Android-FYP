package com.example.buddypunchclone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.*;
import com.google.android.material.navigation.NavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WSMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();

    private static final int PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private WebSocketManager webSocketManager;
    private String jwtToken; // Store this after user login
    private String username; // Store this after user login
    private WifiLocationService wifiLocationService;
    private boolean isUsingGPS = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up the user email in navigation header
        setupNavigationHeader();

        // Initialize the fragment map with Dashboard only
        fragmentMap.put(R.id.nav_dashboard, new DashboardFragment());

//        // Initialize the fragment map with Dashboard only
//        fragmentMap.put(R.id.nav_dashboard, new DashboardFragment());

        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.MapId) {
                    startActivity(new Intent(WSMainActivity.this, GeofencingActivity.class));
                } else if (itemId == R.id.nav_Employees) {
                    startActivity(new Intent(WSMainActivity.this, EmployeesActivity.class));
                } else if (itemId == R.id.nav_Attendace) {
                    // Navigate to AttendaceActivity
                    startActivity(new Intent(WSMainActivity.this, AttendanceActivity.class));
                } else if (itemId == R.id.nav_logout) {
                    // Handle logout
                    Intent intent = new Intent(WSMainActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Clear stored token and user email
                    SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("auth_token");
                    editor.remove("user_email");
                    editor.apply();

                    startActivity(intent);
                    finish(); // Optional: finish the current activity to remove it from the stack
                } else {
                    // Handle fragment navigation (only Dashboard)
                    Fragment selectedFragment = fragmentMap.get(itemId);
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                selectedFragment).commit();
                    }
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Load Dashboard fragment initially
        if (savedInstanceState == null) {
            loadFragment(R.id.nav_dashboard);
        }


//      Jwt Token and user name Auto

        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("auth_token", "");
        username = sharedPreferences.getString("logged_username", "");

        // Only initialize WebSocket if token and username are not empty
        if (!jwtToken.isEmpty() && !username.isEmpty()) {
            // Initialize WebSocket manager first
            initializeWebSocketManager();
        } else {
            // Handle case where token or username is missing
            Log.e("WSMainActivity", "JWT Token or Username is missing");
            // Optionally redirect to login
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }


        // Initialize WebSocket manager first
        initializeWebSocketManager();

        // Then setup location services
        setupLocationServices();
    }

    private void setupNavigationHeader() {
        // Get the header view from the navigation view
        View headerView = navigationView.getHeaderView(0);

        // Find the TextView in the nav header
        TextView emailTextView = headerView.findViewById(R.id.login_user_email);

        // Retrieve the user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "info@example.com");

        // Set the email in the TextView
        emailTextView.setText(userEmail);
    }

    private void loadFragment(int fragmentId) {
        Fragment selectedFragment = fragmentMap.get(fragmentId);
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                    selectedFragment).commit();
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


    private void initializeLocationServices() {
        // Initialize both GPS and WiFi services
        setupGPSLocationService();
        setupWiFiLocationService();

        // Request permissions
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        if (!hasPermissions(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void setupGPSLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(500);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("MainActivity", "Location result is null");
                    switchToWiFiLocation();
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    webSocketManager.sendLocationUpdate(location.getLatitude(), location.getLongitude());
                }
            }
        };
    }

    private void switchToWiFiLocation() {
        if (isUsingGPS) {
            Log.d("MainActivity", "Switching to WiFi location");
            isUsingGPS = false;
            if (fusedLocationClient != null && locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        }
    }

    private void setupWiFiLocationService() {
        wifiLocationService = new WifiLocationService(this);
        wifiLocationService.startWifiLocationUpdates((latitude, longitude) -> {
            if (!isUsingGPS) {
                webSocketManager.sendLocationUpdate(latitude, longitude);
            }
        });
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initializeWebSocketManager() {
        webSocketManager = new WebSocketManager(jwtToken, username);
    }


    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(500); // Update every 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("MainActivity", "Location result is null");
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    Log.d("MainActivity", "New location received - Lat: " +
                            location.getLatitude() + ", Lng: " + location.getLongitude());
                    if (webSocketManager != null) {
                        webSocketManager.sendLocationUpdate(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                    } else {
                        Log.e("MainActivity", "WebSocketManager is null");
                    }
                }
            }
        };

        // Request permissions if needed
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(500),
                    locationCallback,
                    null
            );
        } else {
            switchToWiFiLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                startLocationUpdates();
            } else {
                switchToWiFiLocation();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        if (wifiLocationService != null) {
            wifiLocationService.stopWifiLocationUpdates();
        }
        if (webSocketManager != null) {
            webSocketManager.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
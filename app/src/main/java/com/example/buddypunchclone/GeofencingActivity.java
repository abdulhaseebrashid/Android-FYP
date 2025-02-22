package com.example.buddypunchclone;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.ResponseBody;
import java.io.IOException;
import java.util.Objects;

public class GeofencingActivity extends AppCompatActivity  implements OnMapReadyCallback{
    private static final String TAG = "GeofencingActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int MIN_ACCURACY_THRESHOLD = 100; // meters

    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private GeoFenceApiService geoFenceApiService;
    private List<GeoFence> geoFences;

    private Long currentGeoFenceId; // Track the ID of the active geofence

    private Long currentEntryId; // To track the current entry ID


    private LocationListener networkLocationListener;
    private  GoogleMap mMap;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofencing);

        // Initialize views
        setupViews();
        setupNavigationDrawer();
        setupNavigationHeader();
        setupBottomNavigation();

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize location services
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geoFenceApiService = RegistrationRetrofitClient.getSpringClient(this).create(GeoFenceApiService.class);
        fetchGeoFences();
    }

    private void setupViews() {
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

    private void setupNavigationDrawer() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(GeofencingActivity.this, SecondActivity.class));
                finish();
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(GeofencingActivity.this, EmployeesActivity.class));
                finish();
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(GeofencingActivity.this, AttendanceActivity.class));
                finish();
            } else if (itemId == R.id.nav_grouping_attendance) {
                startActivity(new Intent(GeofencingActivity.this, AttendanceGroupingMainActivity.class));
            } else if (itemId == R.id.MapId) {
                drawerLayout.closeDrawers();
            } else if (itemId == R.id.nav_logout) {
                handleLogout();
            }
            drawerLayout.closeDrawers();
            return true;
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.MapId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(GeofencingActivity.this, SecondActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Employees) {
                startActivity(new Intent(GeofencingActivity.this, EmployeesActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_Attendace) {
                startActivity(new Intent(GeofencingActivity.this, AttendanceActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.MapId) {
                return true;
            }
            return false;
        });
    }

    private void handleLogout() {
        SharedPreferences prefs = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        }
    }

    private void fetchGeoFences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty()) {
            Log.e(TAG, "No authentication token found");
            showToast("Please log in again");
            return;
        }

        geoFenceApiService.getAllGeoFences("Bearer " + token).enqueue(new Callback<List<GeoFence>>() {
            @Override
            public void onResponse(Call<List<GeoFence>> call, Response<List<GeoFence>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    geoFences = response.body();
                    if (!geoFences.isEmpty()) {
                        Log.d(TAG, "Geofences fetched: " + geoFences.size());
                        displayGeoFencePolygons(); // Display polygons first
                        requestLocationUpdates();   // Then start location updates
                    } else {
                        showToast("No geofences found");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to fetch geofences. Error: " + errorBody);
                        showToast("Failed to fetch geofences: " + response.code());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GeoFence>> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    private void displayGeoFencePolygons() {
        if (mMap == null || geoFences == null) {
            Log.e(TAG, "Map or geofences not initialized");
            return;
        }

        for (GeoFence geoFence : geoFences) {
            List<String> polygonCoordinates = geoFence.getPolygonCoordinates();
            if (polygonCoordinates == null || polygonCoordinates.size() < 3) {
                Log.w(TAG, "Invalid polygon coordinates for geofence: " + geoFence.getName());
                continue;
            }

            List<LatLng> polygonPoints = new ArrayList<>();

            // Convert string coordinates to LatLng objects
            for (String coordinate : polygonCoordinates) {
                String[] parts = coordinate.trim().split(",");
                if (parts.length == 2) {
                    try {
                        double lat = Double.parseDouble(parts[0].trim());
                        double lng = Double.parseDouble(parts[1].trim());
                        polygonPoints.add(new LatLng(lat, lng));
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error parsing coordinate: " + coordinate, e);
                    }
                }
            }

            if (polygonPoints.size() >= 3) {
                // Create polygon options
                PolygonOptions polygonOptions = new PolygonOptions()
                        .addAll(polygonPoints)
                        .strokeColor(0xFF0000FF)  // Blue stroke
                        .strokeWidth(5)
                        .fillColor(0x220000FF);   // Semi-transparent blue fill

                // Add polygon to map
                mMap.addPolygon(polygonOptions);

                // Center the map on the first polygon's first point
                if (geoFences.indexOf(geoFence) == 0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polygonPoints.get(0), 15f));
                }
            }
        }
    }

    private void requestLocationUpdates() {
        // Check permissions for both fine and coarse location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestLocationPermissions();
            return;
        }

        // Check if network location is enabled
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "Network Provider not enabled. Showing location settings dialog.");
            showLocationEnableDialog();
            return;
        }

        // Setup location listeners
        setupLocationListeners();
    }

    private void setupLocationListeners() {
        // Remove existing network location listener
        if (networkLocationListener != null && locationManager != null) {
            locationManager.removeUpdates(networkLocationListener);
        }

        // Network Provider Listener with detailed logging
        networkLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // Log detailed location information
                Log.d(TAG, "Network Location Provider Details:");
                Log.d(TAG, "Latitude: " + location.getLatitude());
                Log.d(TAG, "Longitude: " + location.getLongitude());
                Log.d(TAG, "Accuracy: " + location.getAccuracy() + " meters");
                Log.d(TAG, "Provider: " + location.getProvider());

                // Process location only if it meets accuracy requirements
                processLocation(location);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Log.w(TAG, "Network Location Provider disabled: " + provider);
                showLocationEnableDialog();
            }
        };

        // Check permissions again
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Request updates from Network Provider
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,  // 1 second
                    5,     // 5 meters
                    networkLocationListener
            );
        }
    }

    private void processLocation(Location location) {
        if (location == null) {
            Log.e(TAG, "Received null location");
            return;
        }

        // Check location accuracy
        if (location.getAccuracy() > MIN_ACCURACY_THRESHOLD) {
            Log.w(TAG, "Location accuracy is low: " + location.getAccuracy() + " meters");
            // Optionally, you can show a toast or handle low accuracy locations
            return;
        }

        if (geoFences != null && !geoFences.isEmpty()) {
            checkIfUserInsideGeoFence(location);
        }
    }

    private void showLocationEnableDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Location Services Needed")
                .setMessage("Please enable Wi-Fi/Network-based location in settings")
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    // Open location settings directly
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle cancel scenario
                    Toast.makeText(this, "Location tracking requires network location", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                requestLocationUpdates();
            } else {
                showToast("Location permission denied");
            }
        }
    }

    // Existing geofence checking methods remain the same as in your previous implementation
    private void checkIfUserInsideGeoFence(Location userLocation) {
        if (userLocation == null) {
            Log.e("GeoFence", "User location is null");
            Toast.makeText(GeofencingActivity.this, "Unable to determine location", Toast.LENGTH_SHORT).show();
            return;
        }

        if (geoFences == null || geoFences.isEmpty()) {
            Log.e("GeoFence", "No geofences available");
            Toast.makeText(GeofencingActivity.this, "No geofences found", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInsideGeoFence = false;
        GeoFence currentGeoFence = null;

        for (GeoFence geoFence : geoFences) {
            if (geoFence.getPolygonCoordinates() == null || geoFence.getPolygonCoordinates().size() < 3) {
                Log.w("GeoFence", "Geofence " + geoFence.getName() + " has insufficient coordinates");
                continue;
            }

            Log.d("GeoFence", "Checking geofence: " + geoFence.getName());
            if (isLocationInsidePolygon(userLocation, geoFence)) {
                isInsideGeoFence = true;
                currentGeoFence = geoFence;
                break;
            }
        }

        if (isInsideGeoFence) {
            if (currentGeoFenceId == null || !currentGeoFenceId.equals(currentGeoFence.getId())) {
                // User has entered a new geofence
                currentGeoFenceId = currentGeoFence.getId();
                Log.i("GeoFence", "Entered geofence: " + currentGeoFence.getName());
                Toast.makeText(GeofencingActivity.this, "You entered " + currentGeoFence.getName(), Toast.LENGTH_SHORT).show();
                sendGeoFenceEntry(currentGeoFence, userLocation);
            }
        } else {
            if (currentGeoFenceId != null) {
                // User has exited the previous geofence
                Log.i("GeoFence", "Exited geofence with ID: " + currentGeoFenceId);
                sendGeoFenceExit(userLocation);
                currentGeoFenceId = null;
            }
        }
    }


    private void sendGeoFenceEntry(GeoFence geoFence, Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty()) {
            Log.e(TAG, "No authentication token found");
            showToast("Please log in again");
            return;
        }

        GeoFenceEntry geoFenceEntry = new GeoFenceEntry();
        geoFenceEntry.setGeoFenceId((long) geoFence.getId());
        geoFenceEntry.setGeoFenceName(geoFence.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            geoFenceEntry.setEntryTime(LocalDateTime.now().toString());
        }
        geoFenceEntry.setSelectedUsers(geoFence.getSelectedUsers());
        geoFenceEntry.setSelectedGroups(geoFence.getSelectedGroups());
        geoFenceEntry.setLatitude(location.getLatitude());
        geoFenceEntry.setLongitude(location.getLongitude());
        geoFenceEntry.setActive(true);

        geoFenceApiService.createGeoFenceEntry("Bearer " + token, geoFenceEntry).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body() != null ? response.body().string() : "Empty response body";
                        Log.d(TAG, "Geofence entry created successfully: " + responseBodyString);

                        try {
                            JSONObject jsonResponse = new JSONObject(responseBodyString);
                            currentEntryId = jsonResponse.getLong("id");
                            Log.d(TAG, "Stored entry ID: " + currentEntryId);

                            // Save the entry ID to SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
                            prefs.edit().putLong("currentEntryId", currentEntryId).apply();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing entry ID from response", e);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response body", e);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Failed to create geofence entry. Error: " + errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network error creating geofence entry", t);
            }
        });
    }



    // Method 3: Send Geofence Exit
    private void sendGeoFenceExit(Location location) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty() || currentGeoFenceId == null) {
            Log.e(TAG, "No authentication token or active geofence found");
            return;
        }

        GeoFence currentGeoFence = null;
        for (GeoFence fence : geoFences) {
            if (fence.getId() == currentGeoFenceId) {
                currentGeoFence = fence;
                break;
            }
        }

        if (currentGeoFence == null) {
            Log.e(TAG, "Cannot find current geofence details");
            return;
        }

        GeoFenceEntry exitEntry = new GeoFenceEntry();
        exitEntry.setGeoFenceId(currentGeoFenceId);
        exitEntry.setGeoFenceName(currentGeoFence.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            exitEntry.setExitTime(LocalDateTime.now().toString());
        }
        exitEntry.setActive(false);
        exitEntry.setSelectedUsers(currentGeoFence.getSelectedUsers());
        exitEntry.setSelectedGroups(currentGeoFence.getSelectedGroups());
        exitEntry.setLatitude(location.getLatitude());
        exitEntry.setLongitude(location.getLongitude());

        GeoFence finalCurrentGeoFence = currentGeoFence;
        geoFenceApiService.updateGeoFenceExit("Bearer " + token, currentGeoFenceId, exitEntry)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String responseBodyString = response.body() != null ? response.body().string() : "Empty response body";
                                Log.d(TAG, "Geofence entry created successfully: " + responseBodyString);

                                // Parse the response to get the entry ID
                                try {
                                    JSONObject jsonResponse = new JSONObject(responseBodyString);
                                    currentEntryId = jsonResponse.getLong("id");
                                    Log.d(TAG, "Stored entry ID: " + currentEntryId);

                                    // Save the entry ID to SharedPreferences for persistence
                                    SharedPreferences prefs = getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
                                    prefs.edit().putLong("currentEntryId", currentEntryId).apply();
                                } catch (JSONException e) {
                                    Log.e(TAG, "Error parsing entry ID from response", e);
                                }

                            } catch (IOException e) {
                                Log.e(TAG, "Error reading success response body", e);
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Failed to create geofence entry. Error: " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error response body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Network error while recording geofence exit: " + t.getMessage(), t);
                    }
                });
    }





    private boolean isLocationInsidePolygon(Location location, GeoFence geoFence) {
        List<String> polygonCoordinates = geoFence.getPolygonCoordinates();
        List<LatLng> polygon = new ArrayList<>();

        // Convert polygonCoordinates to LatLng objects
        for (String coordinate : polygonCoordinates) {
            String[] parts = coordinate.trim().split(",");
            if (parts.length == 2) {
                try {
                    double lat = Double.parseDouble(parts[0].trim());
                    double lng = Double.parseDouble(parts[1].trim());
                    polygon.add(new LatLng(lat, lng));
                    Log.d("GeoFence", "Parsed coordinate: " + lat + "," + lng);
                } catch (NumberFormatException e) {
                    Log.e("GeoFence", "Error parsing coordinate: " + coordinate);
                }
            } else {
                Log.e("GeoFence", "Invalid coordinate format: " + coordinate);
            }
        }

        // Log the user's location
        Log.d("GeoFence", "User Location: " + location.getLatitude() + "," + location.getLongitude());

        // Perform point-in-polygon check
        return isPointInPolygon(new LatLng(location.getLatitude(), location.getLongitude()), polygon);
    }

    private boolean isPointInPolygon(LatLng point, List<LatLng> polygon) {
        Log.d("GeoFence", "Checking point: " + point.latitude + "," + point.longitude);
        Log.d("GeoFence", "Polygon size: " + polygon.size());

        int crossings = 0;
        for (int i = 0; i < polygon.size(); i++) {
            LatLng start = polygon.get(i);
            LatLng end = polygon.get((i + 1) % polygon.size());

            Log.d("GeoFence", "Edge: " + start.latitude + "," + start.longitude + " to " + end.latitude + "," + end.longitude);

            // Check for crossing
            if (point.latitude > Math.min(start.latitude, end.latitude) &&
                    point.latitude <= Math.max(start.latitude, end.latitude) &&
                    point.longitude <= Math.max(start.longitude, end.longitude)) {

                double xinters = (point.latitude - start.latitude) * (end.longitude - start.longitude) /
                        (end.latitude - start.latitude) + start.longitude;
                if (start.latitude == end.latitude || point.longitude <= xinters) {
                    crossings++;
                    Log.d("GeoFence", "Crossing added: " + crossings);
                }
            }
        }

        boolean inside = crossings % 2 != 0;
        Log.d("GeoFence", "Is point inside polygon: " + inside);
        return inside;
    }

    // Other existing methods like isLocationInsidePolygon, isPointInPolygon remain the same

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, message);
    }
    // Method 4: Call Exit API for Current Geofence
    private void callExitApiForCurrentGeofence() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        SharedPreferences geofencePrefs = getSharedPreferences("GeofencePrefs", Context.MODE_PRIVATE);
        Long storedEntryId = geofencePrefs.getLong("currentEntryId", -1);

        if (token.isEmpty()) {
            Log.e(TAG, "No authentication token found");
            return;
        }

        if (storedEntryId == -1) {
            Log.e(TAG, "No active entry ID found for exit.");
            return;
        }

        GeoFence currentGeoFence = null;
        if (currentGeoFenceId != null) {
            for (GeoFence fence : geoFences) {
                if (fence.getId() == currentGeoFenceId) {
                    currentGeoFence = fence;
                    break;
                }
            }
        }

        if (currentGeoFence == null) {
            Log.e(TAG, "Cannot find current geofence details.");
            return;
        }

        Location lastLocation = null;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting last known location", e);
        }

        GeoFenceEntry exitEntry = new GeoFenceEntry();
        exitEntry.setGeoFenceId(currentGeoFenceId);
        exitEntry.setGeoFenceName(currentGeoFence.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            exitEntry.setExitTime(LocalDateTime.now().toString());
        }
        exitEntry.setActive(false);
        exitEntry.setSelectedUsers(currentGeoFence.getSelectedUsers());
        exitEntry.setSelectedGroups(currentGeoFence.getSelectedGroups());

        if (lastLocation != null) {
            exitEntry.setLatitude(lastLocation.getLatitude());
            exitEntry.setLongitude(lastLocation.getLongitude());
        } else {
            exitEntry.setLatitude(0);
            exitEntry.setLongitude(0);
        }

        geoFenceApiService.updateGeoFenceExit("Bearer " + token, storedEntryId, exitEntry)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Exit API called successfully on app pause/destroy.");
                            geofencePrefs.edit().remove("currentEntryId").apply();
                            currentEntryId = null;
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "Failed to call exit API. Error: " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error reading error response body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(TAG, "Network error while calling exit API: " + t.getMessage());
                    }
                });
    }





    @Override
    protected void onPause() {
        super.onPause();
        // Remove location updates to save battery
        if (locationManager != null && networkLocationListener != null) {
            locationManager.removeUpdates(networkLocationListener);
        }

        // Check if user is inside a geofence and call the exit API
        if (currentGeoFenceId != null) {
            Log.d(TAG, "App is paused. Calling exit API for active geofence.");
            callExitApiForCurrentGeofence();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Call exit API when the app is destroyed
        if (currentGeoFenceId != null) {
            Log.d(TAG, "App is destroyed. Calling exit API for active geofence.");
            callExitApiForCurrentGeofence();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart location updates when activity resumes
        if (geoFences != null && !geoFences.isEmpty()) {
            requestLocationUpdates();
        }
    }


}
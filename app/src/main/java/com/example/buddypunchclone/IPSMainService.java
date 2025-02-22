package com.example.buddypunchclone;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.*;

public class IPSMainService extends Service {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_UPDATE_INTERVAL = 500; // 500ms
    private static final String CHANNEL_ID = "ForegroundServiceChannel";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private WebSocketManager webSocketManager;
    private String jwtToken;
    private String username;
    private WifiLocationService wifiLocationService;
    private boolean isUsingGPS = true;

    @Override
    public void onCreate() {
        super.onCreate();

        // Create notification channel for foreground service
        createNotificationChannel();

        // Start the service in the foreground
        startForeground(1, buildForegroundNotification());

        // Retrieve JWT token and username from SharedPreferences
        retrieveAuthTokenFromSharedPreferences();

        // Initialize WebSocket manager with token and username if available
        if (jwtToken != null && username != null) {
            initializeWebSocketManager();
        } else {
            Log.e("IPSMainService", "JWT Token or Username not found in SharedPreferences");
        }

        // Initialize location services
        initializeLocationServices();
    }

    // Create a notification channel for the foreground service (required for Android 8.0+)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Location Service Channel";
            String description = "Channel for location update service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Build notification for the foreground service
    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("IPS Location Service")
                .setContentText("Tracking location in the background")
                .setSmallIcon(R.drawable.user)
                .build();
    }

    // Retrieve JWT Token and Username from SharedPreferences
    private void retrieveAuthTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        jwtToken = sharedPreferences.getString("auth_token", null);  // Retrieve the token
        username = sharedPreferences.getString("username", null);    // Retrieve username

        if (jwtToken != null && username != null) {
            Log.d("IPSMainService", "JWT Token and Username retrieved from SharedPreferences");
        } else {
            Log.e("IPSMainService", "JWT Token or Username not found in SharedPreferences");
        }
    }

    // Initialize WebSocketManager with JWT token and username
    private void initializeWebSocketManager() {
        if (jwtToken != null && username != null) {
            webSocketManager = new WebSocketManager(jwtToken, username);
            Log.d("IPSMainService", "WebSocket Manager Initialized with Token: " + jwtToken);
        } else {
            Log.e("IPSMainService", "JWT Token or Username missing, WebSocket cannot be initialized");
        }
    }

    // Initialize location services (GPS and WiFi)
    private void initializeLocationServices() {
        setupGPSLocationService();
        setupWiFiLocationService();

        // Request necessary permissions for location services
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE
        };

        if (!hasPermissions(permissions)) {
            // Permissions should be requested before starting the service
            Log.e("IPSMainService", "Permissions missing.");
        } else {
            startLocationUpdates();
        }
    }

    // Setup GPS location service
    private void setupGPSLocationService() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(LOCATION_UPDATE_INTERVAL); // Set to 500ms for live updates

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("IPSMainService", "Location result is null");
                    switchToWiFiLocation();
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    Log.d("IPSMainService", "GPS Location Update: Lat=" + location.getLatitude() + ", Lng=" + location.getLongitude());
                    if (webSocketManager != null) {
                        webSocketManager.sendLocationUpdate(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };
    }

    // Switch to WiFi location if GPS isn't available
    private void switchToWiFiLocation() {
        if (isUsingGPS) {
            Log.d("IPSMainService", "Switching to WiFi location");
            isUsingGPS = false;
            if (fusedLocationClient != null && locationCallback != null) {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        }
    }

    // Setup WiFi location service
    private void setupWiFiLocationService() {
        wifiLocationService = new WifiLocationService(this);
        wifiLocationService.startWifiLocationUpdates((latitude, longitude) -> {
            if (!isUsingGPS) {
                Log.d("IPSMainService", "WiFi Location Update: Lat=" + latitude + ", Lng=" + longitude);
                if (webSocketManager != null) {
                    webSocketManager.sendLocationUpdate(latitude, longitude);
                }
            }
        });
    }

    // Check if necessary permissions are granted
    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Start location updates
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(LOCATION_UPDATE_INTERVAL),  // Request updates every 500ms
                    locationCallback,
                    null
            );
        } else {
            switchToWiFiLocation();
        }
    }

    // Handle service destruction to clean up resources
    @Override
    public void onDestroy() {
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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("IPSMainService", "Service is being removed from task");
        // Stop the service if the app is closed
        stopSelf();
    }
}

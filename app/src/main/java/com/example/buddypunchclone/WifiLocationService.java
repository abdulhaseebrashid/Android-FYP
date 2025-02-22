package com.example.buddypunchclone;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class WifiLocationService  {
    private static final String TAG = "WifiLocationService";
    private final Context context;
    private final WifiManager wifiManager;
    private final Handler handler = new Handler();
    private LocationCallback locationCallback;
    private static final long SCAN_INTERVAL = 5000; // 5 seconds

    public interface LocationCallback {
        void onLocationReceived(double latitude, double longitude);
    }

    public WifiLocationService(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void startWifiLocationUpdates(LocationCallback callback) {
        this.locationCallback = callback;

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        // Start periodic scanning
        handler.post(scanRunnable);
    }

    private final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanWifiNetworks();
            handler.postDelayed(this, SCAN_INTERVAL);
        }
    };

    private void scanWifiNetworks() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permission not granted");
            return;
        }

        wifiManager.startScan();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        estimateLocation(scanResults);
    }

    private void estimateLocation(List<ScanResult> scanResults) {
        // This is a simple example that uses the strongest AP's location
        // In a real implementation, you would use trilateration with multiple APs
        if (scanResults != null && !scanResults.isEmpty()) {
            // Sort by signal strength
            ScanResult strongestAP = scanResults.stream()
                    .max((ap1, ap2) -> Integer.compare(ap1.level, ap2.level))
                    .orElse(null);

            if (strongestAP != null) {
                // In a real implementation, you would have a database of AP locations
                // This is just an example using mock coordinates
                double estimatedLat = 0.0;
                double estimatedLng = 0.0;

                // Calculate estimated position based on AP BSSID
                // This should be replaced with your actual AP location database
                switch (strongestAP.BSSID) {
                    case "00:11:22:33:44:55":
                        estimatedLat = 37.7749;
                        estimatedLng = -122.4194;
                        break;
                    // Add more access points as needed
                    default:
                        // Use some default location or last known location
                        break;
                }

                if (locationCallback != null) {
                    locationCallback.onLocationReceived(estimatedLat, estimatedLng);
                }
            }
        }
    }

    public void stopWifiLocationUpdates() {
        handler.removeCallbacks(scanRunnable);
    }
}
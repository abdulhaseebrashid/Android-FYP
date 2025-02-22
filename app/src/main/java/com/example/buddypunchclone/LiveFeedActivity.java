package com.example.buddypunchclone;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveFeedActivity extends AppCompatActivity {
    private WebView webView;
    private CustomOverlayView overlayView;
    private static final String TAG = "LiveFeedActivity";
    private Handler handler;
    private static final int POLLING_INTERVAL = 5000; // 5 seconds
    private ApiService apiService;
    private boolean isPolling = true;
    private boolean currentDetectionStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_feed);

        String cameraUrl = getIntent().getStringExtra("cameraUrl");
        if (cameraUrl == null) {
            Toast.makeText(this, "Error: Camera URL not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        webView = findViewById(R.id.webview);
        overlayView = findViewById(R.id.overlayView);
        setupWebView(cameraUrl);

        // Initialize API service
        apiService = RetrofitClient.getClient().create(ApiService.class);
        handler = new Handler(Looper.getMainLooper());
        startPolling();
    }

    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPolling) {
                    fetchSystemStatus();
                    handler.postDelayed(this, POLLING_INTERVAL);
                }
            }
        }, POLLING_INTERVAL);
    }

    private void fetchSystemStatus() {
        apiService.getSystemStatus().enqueue(new Callback<SystemStatusResponse>() {
            @Override
            public void onResponse(Call<SystemStatusResponse> call, Response<SystemStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SystemStatusResponse status = response.body();
                    currentDetectionStatus = status.isUnrecognizedPersonDetected();

                    // Update the overlay color based on recognition status
                    overlayView.setRecognitionStatus(!currentDetectionStatus);

                    String message = "Status: " +
                            (currentDetectionStatus ? "Unrecognized person detected" : "Person recognized") +
                            (status.getLastUnrecognizedTime().isEmpty() ? "" :
                                    "\nLast detected: " + status.getLastUnrecognizedTime());

                    Toast.makeText(LiveFeedActivity.this, message, Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "System status response: " +
                            "Unrecognized: " + status.isUnrecognizedPersonDetected() +
                            " WhatsApp: " + status.getWhatsapp_number());

                    if (status.isUnrecognizedPersonDetected() && status.getWhatsapp_number() != null) {
                        sendWhatsAppNotification(
                                status.getWhatsapp_number(),
                                "⚠️ Alert: Unrecognized person detected at " + status.getLastUnrecognizedTime()
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<SystemStatusResponse> call, Throwable t) {
                Log.e(TAG, "API call failed: " + t.getMessage());
            }
        });
    }

    private void sendWhatsAppNotification(String to, String messageBody) {
        // Create request with exact field names matching Django
        NotificationRequest request = new NotificationRequest(to, messageBody);

        // Add logging to debug the request
        Log.d(TAG, "Sending WhatsApp notification to: " + to + " with message: " + messageBody);

        apiService.sendWhatsAppNotification(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "WhatsApp notification sent successfully");
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Failed to send WhatsApp notification: " + response.code() +
                                " Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to send WhatsApp notification: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error sending WhatsApp notification: " + t.getMessage());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView(String cameraUrl) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e(TAG, "Error loading URL: " + description);
                Toast.makeText(LiveFeedActivity.this, "Error: " + description, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d(TAG, "Page loaded successfully: " + url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        Log.d(TAG, "Loading URL: " + cameraUrl);
        webView.loadUrl(cameraUrl);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}

package com.example.buddypunchclone;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    // Update the URL to use SockJS format
    private static final String WS_URL = "http://172.16.27.92:8080/ws/websocket";

    private WebSocket webSocket;
    private final String jwtToken;
    private final Gson gson;
    private final String userId;
    private boolean isConnected = false;

    public WebSocketManager(String jwtToken, String userId) {
        Log.d(TAG, "Initializing WebSocketManager - userId: " + userId);
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.gson = new Gson();
        connectWebSocket();
    }

    private void connectWebSocket() {
        Log.d(TAG, "Attempting to connect WebSocket...");

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(WS_URL)
                .addHeader("Authorization", "Bearer " + jwtToken)
                .addHeader("Upgrade", "websocket")
                .addHeader("Connection", "Upgrade")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket Connected Successfully");
                isConnected = true;

                // Send STOMP connect frame with proper headers
                String connectFrame = "CONNECT\n" +
                        "accept-version:1.1,1.0\n" +
                        "heart-beat:10000,10000\n" +
                        "Authorization:Bearer " + jwtToken + "\n" +
                        "\n" +
                        "\u0000";

                webSocket.send(connectFrame);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Received message: " + text);

                if (text.startsWith("CONNECTED")) {
                    Log.d(TAG, "STOMP connection established");
                    // Subscribe to required topics here if needed
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket Failed", t);
                isConnected = false;

                if (response != null) {
                    Log.e(TAG, "Failure Response Code: " + response.code());
                    Log.e(TAG, "Failure Response Message: " + response.message());
                }

                reconnectWithDelay();
            }
        });
    }

    public void sendLocationUpdate(double latitude, double longitude) {
        if (!isConnected) {
            Log.w(TAG, "WebSocket not connected, attempting to reconnect...");
            connectWebSocket();
            return;
        }

        LocationUpdate locationUpdate = new LocationUpdate(
                latitude,
                longitude,
                System.currentTimeMillis(),
                userId
        );

        String message = gson.toJson(locationUpdate);
        String stompFrame = "SEND\n" +
                "destination:/app/location/update\n" +
                "content-type:application/json\n" +
                "\n" +
                message + "\u0000";

        Log.d(TAG, "Sending location update: " + message);
        webSocket.send(stompFrame);
    }

    private void reconnectWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d(TAG, "Attempting to reconnect...");
            connectWebSocket();
        }, 5000);
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure");
            isConnected = false;
        }
    }
}
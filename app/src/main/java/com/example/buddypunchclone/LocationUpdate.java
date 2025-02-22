package com.example.buddypunchclone;

public class LocationUpdate {
    private double latitude;
    private double longitude;
    private long timestamp;
    private String username; // Logged in user's ID

    public LocationUpdate() {
    }

    public LocationUpdate(double latitude, double longitude, long timestamp, String username) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.username = username;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

package com.example.buddypunchclone;

public class Camera {
    private String name;
    private String url;

    public Camera(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Add these getter methods to resolve the errors
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    // Optional: Add setter methods if you need to change values later
    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
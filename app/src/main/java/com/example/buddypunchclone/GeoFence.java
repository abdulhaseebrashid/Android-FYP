package com.example.buddypunchclone;

import java.util.List;

public class GeoFence {
    private int id;
    private String name;
    private String description;
    private List<String> selectedUsers;
    private List<String> selectedGroups;
    private List<String> polygonCoordinates; // Coordinates are provided in this format (e.g., latitude, longitude)
    private String polygonColor;

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<String> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }

    public List<String> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(List<String> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public List<String> getPolygonCoordinates() {
        return polygonCoordinates;
    }

    public void setPolygonCoordinates(List<String> polygonCoordinates) {
        this.polygonCoordinates = polygonCoordinates;
    }

    public String getPolygonColor() {
        return polygonColor;
    }

    public void setPolygonColor(String polygonColor) {
        this.polygonColor = polygonColor;
    }
}

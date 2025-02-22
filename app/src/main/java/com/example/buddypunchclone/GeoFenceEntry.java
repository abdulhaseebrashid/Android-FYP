//package com.example.buddypunchclone;
//
//import java.util.List;
//
//public class GeoFenceEntry {
//    private Long id;
//    private Long geoFenceId;
//    private String geoFenceName;
//    private String entryTime;
//    private List<String> selectedUsers;
//    private List<String> selectedGroups;
//    private double latitude;
//    private double longitude;
//
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getGeoFenceId() {
//        return geoFenceId;
//    }
//
//    public void setGeoFenceId(Long geoFenceId) {
//        this.geoFenceId = geoFenceId;
//    }
//
//    public String getGeoFenceName() {
//        return geoFenceName;
//    }
//
//    public void setGeoFenceName(String geoFenceName) {
//        this.geoFenceName = geoFenceName;
//    }
//
//
//    public String getEntryTime() {
//        return entryTime;
//    }
//
//    public void setEntryTime(String entryTime) {
//        this.entryTime = entryTime;
//    }
//
//    public List<String> getSelectedUsers() {
//        return selectedUsers;
//    }
//
//    public void setSelectedUsers(List<String> selectedUsers) {
//        this.selectedUsers = selectedUsers;
//    }
//
//    public List<String> getSelectedGroups() {
//        return selectedGroups;
//    }
//
//    public void setSelectedGroups(List<String> selectedGroups) {
//        this.selectedGroups = selectedGroups;
//    }
//
//    public double getLatitude() {
//        return latitude;
//    }
//
//    public void setLatitude(double latitude) {
//        this.latitude = latitude;
//    }
//
//    public double getLongitude() {
//        return longitude;
//    }
//
//    public void setLongitude(double longitude) {
//        this.longitude = longitude;
//    }
//}


package com.example.buddypunchclone;

import java.time.LocalDateTime;
import java.util.List;

public class GeoFenceEntry {
    private Long id;
    private Long geoFenceId;
    private String geoFenceName;
    private String entryTime; // String format for easier parsing
    private String exitTime; // New field for exit time
    private boolean isActive; // New field to track if user is still in geofence
    private Long timeSpentMinutes;
    private List<String> selectedUsers;
    private List<String> selectedGroups;
    private double latitude;
    private double longitude;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Long getTimeSpentMinutes() {
        return timeSpentMinutes;
    }

    public void setTimeSpentMinutes(Long timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGeoFenceId() {
        return geoFenceId;
    }

    public void setGeoFenceId(Long geoFenceId) {
        this.geoFenceId = geoFenceId;
    }

    public String getGeoFenceName() {
        return geoFenceName;
    }

    public void setGeoFenceName(String geoFenceName) {
        this.geoFenceName = geoFenceName;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
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
}

package com.example.buddypunchclone;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AttendanceUser implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("last_attendance_time")
    private String lastAttendanceTime;

    @SerializedName("total_attendance")
    private int totalAttendance;

    @SerializedName("major")
    private String major;

    @SerializedName("standing")
    private String standing;

    @SerializedName("starting_year")
    private int startingYear;

    @SerializedName("year")
    private int year;

    @SerializedName("image")
    private String image;

    @SerializedName("total_time_user_spent")
    private int totalTimeUserSpent;

    @SerializedName("exit_time")
    private String exitTime;

    @SerializedName("exit_user_attendance")
    private boolean exitUserAttendance;

    @SerializedName("Attendance_type")
    private String attendanceType;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    // Add getters for all fields
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getLastAttendanceTime() { return lastAttendanceTime; }
    public int getTotalAttendance() { return totalAttendance; }
    public String getMajor() { return major; }
    public String getStanding() { return standing; }
    public int getStartingYear() { return startingYear; }
    public int getYear() { return year; }
    public String getImage() { return image; }
    public int getTotalTimeUserSpent() { return totalTimeUserSpent; }
    public String getExitTime() { return exitTime; }
    public boolean isExitUserAttendance() { return exitUserAttendance; }
    public String getAttendanceType() { return attendanceType; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    // Add all setters
    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setLastAttendanceTime(String lastAttendanceTime) { this.lastAttendanceTime = lastAttendanceTime; }
    public void setTotalAttendance(int totalAttendance) { this.totalAttendance = totalAttendance; }
    public void setMajor(String major) { this.major = major; }
    public void setStanding(String standing) { this.standing = standing; }
    public void setStartingYear(int startingYear) { this.startingYear = startingYear; }
    public void setYear(int year) { this.year = year; }
    public void setImage(String image) { this.image = image; }
    public void setTotalTimeUserSpent(int totalTimeUserSpent) { this.totalTimeUserSpent = totalTimeUserSpent; }
    public void setExitTime(String exitTime) { this.exitTime = exitTime; }
    public void setExitUserAttendance(boolean exitUserAttendance) { this.exitUserAttendance = exitUserAttendance; }
    public void setAttendanceType(String attendanceType) { this.attendanceType = attendanceType; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
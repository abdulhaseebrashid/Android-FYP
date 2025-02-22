package com.example.buddypunchclone;

public class AttendanceResponse {
    private String message;
    private String studentId;
    private AttendanceData attendanceData;
    private boolean success;
    private String error;

    // Add getters and setters for new fields
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    // Existing getters and setters...
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public AttendanceData getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(AttendanceData attendanceData) {
        this.attendanceData = attendanceData;
    }
}

class AttendanceData {
    private int totalAttendance;
    private String lastAttendanceTime;
    private boolean exitUserAttendance;
    private int totalTimeUserSpent;
    private String exitTime;
    private String attendanceType;

    // Getters and setters
    public int getTotalAttendance() {
        return totalAttendance;
    }

    public void setTotalAttendance(int totalAttendance) {
        this.totalAttendance = totalAttendance;
    }

    public String getLastAttendanceTime() {
        return lastAttendanceTime;
    }

    public void setLastAttendanceTime(String lastAttendanceTime) {
        this.lastAttendanceTime = lastAttendanceTime;
    }

    public boolean isExitUserAttendance() {
        return exitUserAttendance;
    }

    public void setExitUserAttendance(boolean exitUserAttendance) {
        this.exitUserAttendance = exitUserAttendance;
    }

    public int getTotalTimeUserSpent() {
        return totalTimeUserSpent;
    }

    public void setTotalTimeUserSpent(int totalTimeUserSpent) {
        this.totalTimeUserSpent = totalTimeUserSpent;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }
}
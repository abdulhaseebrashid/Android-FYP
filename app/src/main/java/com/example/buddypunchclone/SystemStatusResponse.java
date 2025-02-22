package com.example.buddypunchclone;

public class SystemStatusResponse {
    private String last_unrecognized_time;
    private boolean unrecognized_person_detected;
    private String whatsapp_number; // Add this field


    // Getters and setters
    public String getLastUnrecognizedTime() {
        return last_unrecognized_time;
    }

    public void setLastUnrecognizedTime(String last_unrecognized_time) {
        this.last_unrecognized_time = last_unrecognized_time;
    }

    public boolean isUnrecognizedPersonDetected() {
        return unrecognized_person_detected;
    }

    public void setUnrecognizedPersonDetected(boolean unrecognized_person_detected) {
        this.unrecognized_person_detected = unrecognized_person_detected;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public void setWhatsapp_number(String whatsapp_number) {
        this.whatsapp_number = whatsapp_number;
    }


}
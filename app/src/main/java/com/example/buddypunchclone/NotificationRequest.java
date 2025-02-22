package com.example.buddypunchclone;

public class NotificationRequest {
    private String to;
    private String messageBody;

    public NotificationRequest(String to, String messageBody) {
        this.to = to;
        this.messageBody = messageBody;
    }

    // Getters and setters
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}

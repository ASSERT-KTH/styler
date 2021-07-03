package org.benetech.servicenet.service.dto;

public class SmsMessage {
    private String to;
    private String message;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "SmsMessage{" +
            "to='" + to + '\'' +
            ", message='" + message + '\'' +
            '}';
    }
}

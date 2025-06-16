package com.telemed.dto;

public class AppointmentStatusUpdateDTO {
    private String status; // APPROVED, CANCELED

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

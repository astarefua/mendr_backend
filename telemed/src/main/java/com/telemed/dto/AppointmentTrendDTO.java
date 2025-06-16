package com.telemed.dto;

public class AppointmentTrendDTO {
    private String date;
    private long count;

    public AppointmentTrendDTO(String date, long count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public long getCount() {
        return count;
    }
}

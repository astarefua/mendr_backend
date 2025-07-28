package com.telemed.dto;

public class AppointmentSummaryDTO {
    private long total;
    private long approved;
    private long pending;
    private long canceled;

    public AppointmentSummaryDTO(long total, long approved, long pending, long canceled) {
        this.total = total;
        this.approved = approved;
        this.pending = pending;
        this.canceled = canceled;
    }

    public long getTotal() {
        return total;
    }

    public long getApproved() {
        return approved;
    }

    public long getPending() {
        return pending;
    }

    public long getCanceled() {
        return canceled;
    }
}

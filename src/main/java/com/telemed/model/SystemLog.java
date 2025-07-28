package com.telemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    public SystemLog() {}

    public SystemLog(String action, String performedBy) {
        this.action = action;
        this.performedBy = performedBy;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getAction() { return action; }
    public String getPerformedBy() { return performedBy; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

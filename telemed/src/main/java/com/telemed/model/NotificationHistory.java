// NotificationHistory.java
package com.telemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class NotificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Patient patient;
    
    @ManyToOne
    private SmartMedicationGuide guide;
    
    private LocalDateTime scheduledTime;
    private LocalDateTime sentAt;
    private String notificationType; // "REMINDER" or "MISSED_DOSE"
    private boolean delivered = false;
    
    // Constructors
    public NotificationHistory() {}
    
    public NotificationHistory(Patient patient, SmartMedicationGuide guide, 
                             LocalDateTime scheduledTime, String notificationType) {
        this.patient = patient;
        this.guide = guide;
        this.scheduledTime = scheduledTime;
        this.notificationType = notificationType;
        this.sentAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public SmartMedicationGuide getGuide() { return guide; }
    public void setGuide(SmartMedicationGuide guide) { this.guide = guide; }
    
    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
    
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
}
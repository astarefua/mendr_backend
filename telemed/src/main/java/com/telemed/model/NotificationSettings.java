// NotificationSettings.java
package com.telemed.model;

import jakarta.persistence.*;

@Entity
public class NotificationSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    private boolean notificationsEnabled = true;
    private int reminderMinutesBefore = 5;
    private boolean missedDoseReminders = true;
    private int missedDoseReminderMinutes = 15;
    
    // Constructors
    public NotificationSettings() {}
    
    public NotificationSettings(Patient patient) {
        this.patient = patient;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }
    
    public int getReminderMinutesBefore() { return reminderMinutesBefore; }
    public void setReminderMinutesBefore(int reminderMinutesBefore) { this.reminderMinutesBefore = reminderMinutesBefore; }
    
    public boolean isMissedDoseReminders() { return missedDoseReminders; }
    public void setMissedDoseReminders(boolean missedDoseReminders) { this.missedDoseReminders = missedDoseReminders; }
    
    public int getMissedDoseReminderMinutes() { return missedDoseReminderMinutes; }
    public void setMissedDoseReminderMinutes(int missedDoseReminderMinutes) { this.missedDoseReminderMinutes = missedDoseReminderMinutes; }
}
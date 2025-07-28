package com.telemed.controller;

import com.telemed.model.Notification;
import com.telemed.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;





import com.telemed.model.*;
import com.telemed.repository.*;
import com.telemed.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;
    
    @Autowired
    private PatientRepository patientRepo;
    
    @Autowired
    private NotificationSettingsRepository notificationSettingsRepo;
    
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepo;


    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<Notification>> getMyNotifications() {
        return ResponseEntity.ok(service.getMyNotifications());
    }

    @PostMapping("/mark-as-read/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    
    
    
 // NotificationController.java
    
    
        
        @PostMapping("/register-token")
        @PreAuthorize("hasRole('PATIENT')")
        public ResponseEntity<String> registerFCMToken(@RequestBody Map<String, String> request) {
            String email = SecurityUtils.getCurrentUserEmail();
            String fcmToken = request.get("fcmToken");
            
            Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
            
            patient.setFcmToken(fcmToken);
            patientRepo.save(patient);
            
            return ResponseEntity.ok("FCM token registered successfully");
        }

        @GetMapping("/settings")
        @PreAuthorize("hasRole('PATIENT')")
        public ResponseEntity<NotificationSettings> getNotificationSettings() {
            String email = SecurityUtils.getCurrentUserEmail();
            Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
            
            NotificationSettings settings = notificationSettingsRepo.findByPatient(patient)
                .orElse(new NotificationSettings(patient));
            
            return ResponseEntity.ok(settings);
        }

        @PutMapping("/settings")
        @PreAuthorize("hasRole('PATIENT')")
        public ResponseEntity<String> updateNotificationSettings(@RequestBody NotificationSettings newSettings) {
            String email = SecurityUtils.getCurrentUserEmail();
            Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
            
            NotificationSettings settings = notificationSettingsRepo.findByPatient(patient)
                .orElse(new NotificationSettings(patient));
            
            settings.setNotificationsEnabled(newSettings.isNotificationsEnabled());
            settings.setReminderMinutesBefore(newSettings.getReminderMinutesBefore());
            settings.setMissedDoseReminders(newSettings.isMissedDoseReminders());
            settings.setMissedDoseReminderMinutes(newSettings.getMissedDoseReminderMinutes());
            
            notificationSettingsRepo.save(settings);
            
            return ResponseEntity.ok("Notification settings updated successfully");
        }

        @GetMapping("/history")
        @PreAuthorize("hasRole('PATIENT')")
        public ResponseEntity<List<NotificationHistory>> getNotificationHistory() {
            String email = SecurityUtils.getCurrentUserEmail();
            Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
            
            List<NotificationHistory> history = notificationHistoryRepo.findByPatientOrderBySentAtDesc(patient);
            return ResponseEntity.ok(history);
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
   


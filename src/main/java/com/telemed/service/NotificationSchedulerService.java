// NotificationSchedulerService.java
package com.telemed.service;

import com.telemed.model.*;
import com.telemed.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationSchedulerService {

    @Autowired
    private PatientRepository patientRepo;
    
    @Autowired
    private SmartMedicationGuideRepository medicationRepo;
    
    @Autowired
    private NotificationSettingsRepository notificationSettingsRepo;
    
    @Autowired
    private NotificationHistoryRepository notificationHistoryRepo;
    
    @Autowired
    private MedicationAdherenceRepository adherenceRepo;
    
    @Autowired
    private FCMService fcmService;

    // Run every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 milliseconds
    public void processNotifications() {
        LocalDateTime now = LocalDateTime.now();
        List<Patient> patients = patientRepo.findAll();
        
        for (Patient patient : patients) {
            processPatientNotifications(patient, now);
        }
    }

    private void processPatientNotifications(Patient patient, LocalDateTime now) {
        // Get patient notification settings
        NotificationSettings settings = notificationSettingsRepo.findByPatient(patient)
            .orElse(new NotificationSettings(patient));
        
        if (!settings.isNotificationsEnabled()) {
            return;
        }

        // Get active medications
        List<SmartMedicationGuide> activeGuides = medicationRepo.findByPatient(patient).stream()
            .filter(guide -> isActiveToday(guide))
            .toList();

        for (SmartMedicationGuide guide : activeGuides) {
            processGuideNotifications(patient, guide, settings, now);
        }
    }

    private void processGuideNotifications(Patient patient, SmartMedicationGuide guide, 
                                         NotificationSettings settings, LocalDateTime now) {
        
        List<LocalTime> doseTimes = calculateDoseTimes(guide.getDosesPerDay());
        
        for (LocalTime doseTime : doseTimes) {
            LocalDateTime scheduledTime = LocalDateTime.of(LocalDate.now(), doseTime);
            
            // Check for upcoming reminders (5 minutes before)
            LocalDateTime reminderTime = scheduledTime.minusMinutes(settings.getReminderMinutesBefore());
            if (shouldSendReminder(now, reminderTime)) {
                sendReminderNotification(patient, guide, scheduledTime, settings);
            }
            
            // Check for missed dose reminders
            if (settings.isMissedDoseReminders()) {
                LocalDateTime missedTime = scheduledTime.plusMinutes(settings.getMissedDoseReminderMinutes());
                if (shouldSendMissedDoseReminder(now, missedTime, patient, guide, scheduledTime)) {
                    sendMissedDoseNotification(patient, guide, scheduledTime, settings);
                }
            }
        }
    }

    private List<LocalTime> calculateDoseTimes(int dosesPerDay) {
        // Evenly space doses throughout the day (8 AM to 8 PM)
        LocalTime startTime = LocalTime.of(8, 0); // 8:00 AM
        LocalTime endTime = LocalTime.of(20, 0);  // 8:00 PM
        
        if (dosesPerDay == 1) {
            return List.of(LocalTime.of(9, 0)); // 9:00 AM
        }
        
        long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long intervalMinutes = totalMinutes / (dosesPerDay - 1);
        
        return java.util.stream.IntStream.range(0, dosesPerDay)
            .mapToObj(i -> startTime.plusMinutes(i * intervalMinutes))
            .toList();
    }

    private boolean shouldSendReminder(LocalDateTime now, LocalDateTime reminderTime) {
        // Send if we're within 1 minute of reminder time
        return Math.abs(ChronoUnit.MINUTES.between(now, reminderTime)) <= 1;
    }

    private boolean shouldSendMissedDoseReminder(LocalDateTime now, LocalDateTime missedTime, 
                                               Patient patient, SmartMedicationGuide guide, 
                                               LocalDateTime scheduledTime) {
        
        // Check if we're at the missed dose time
        if (Math.abs(ChronoUnit.MINUTES.between(now, missedTime)) > 1) {
            return false;
        }
        
        // Check if dose was already taken
        if (wasDoseTaken(patient, guide, scheduledTime)) {
            return false;
        }
        
        // Check if missed dose notification already sent
        return !wasNotificationSent(patient, guide, scheduledTime, "MISSED_DOSE");
    }

    private void sendReminderNotification(Patient patient, SmartMedicationGuide guide, 
                                        LocalDateTime scheduledTime, NotificationSettings settings) {
        
        if (wasNotificationSent(patient, guide, scheduledTime, "REMINDER")) {
            return;
        }
        
        String timeSlot = scheduledTime.toLocalTime().toString();
        boolean sent = fcmService.sendMedicationReminder(patient, guide, timeSlot);
        
        if (sent) {
            NotificationHistory history = new NotificationHistory(patient, guide, scheduledTime, "REMINDER");
            history.setDelivered(true);
            notificationHistoryRepo.save(history);
        }
    }

    private void sendMissedDoseNotification(Patient patient, SmartMedicationGuide guide, 
                                          LocalDateTime scheduledTime, NotificationSettings settings) {
        
        boolean sent = fcmService.sendMissedDoseReminder(patient, guide);
        
        if (sent) {
            NotificationHistory history = new NotificationHistory(patient, guide, scheduledTime, "MISSED_DOSE");
            history.setDelivered(true);
            notificationHistoryRepo.save(history);
        }
    }

    private boolean isActiveToday(SmartMedicationGuide guide) {
        LocalDate today = LocalDate.now();
        return !guide.getStartDate().isAfter(today) && 
               ChronoUnit.DAYS.between(guide.getStartDate(), today) < guide.getTotalDays();
    }

    private boolean wasDoseTaken(Patient patient, SmartMedicationGuide guide, LocalDateTime scheduledTime) {
        // Use your existing logic from confirmMedicationTaken
        int dosesPerDay = Math.max(1, guide.getDosesPerDay());
        int windowSizeMinutes = 1440 / dosesPerDay;
        
        LocalDateTime midnight = LocalDateTime.of(scheduledTime.toLocalDate(), LocalTime.MIDNIGHT);
        int scheduledWindow = (int) ChronoUnit.MINUTES.between(midnight, scheduledTime) / windowSizeMinutes;
        
        return adherenceRepo.findByPatientAndGuideOrderByTakenAtDesc(patient, guide).stream()
            .anyMatch(entry -> {
                int entryWindow = (int) ChronoUnit.MINUTES.between(midnight, entry.getTakenAt()) / windowSizeMinutes;
                return entry.getTakenAt().toLocalDate().equals(scheduledTime.toLocalDate()) && 
                       entryWindow == scheduledWindow;
            });
    }

    private boolean wasNotificationSent(Patient patient, SmartMedicationGuide guide, 
                                      LocalDateTime scheduledTime, String type) {
        return !notificationHistoryRepo.findByPatientAndGuideAndScheduledTimeAndType(
            patient, guide, scheduledTime, type).isEmpty();
    }
}
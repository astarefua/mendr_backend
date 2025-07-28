// ScheduledReminderService.java - PHASE 2 Implementation
// Add this to your main application class: @EnableScheduling

package com.telemed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.telemed.repository.AppointmentRepository;
import com.telemed.model.Appointment;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledReminderService {

    @Autowired
    private AppointmentRepository appointmentRepo;
    
    @Autowired
    private EmailService emailService;

    // 🕐 Run every hour to check for 24-hour reminders
    @Scheduled(fixedRate = 3600000) // 1 hour = 3,600,000 ms
    public void send24HourReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime tomorrow = now.plusHours(24);
            
            // Find appointments happening in the next 24-25 hours that haven't been reminded
            List<Appointment> upcomingAppointments = appointmentRepo.findAll().stream()
                .filter(apt -> apt.getAppointmentDate().isAfter(tomorrow.minusHours(1)) && 
                              apt.getAppointmentDate().isBefore(tomorrow.plusHours(1)))
                .filter(apt -> "PENDING".equals(apt.getStatus()))
                .toList();
            
            for (Appointment appointment : upcomingAppointments) {
                emailService.sendAppointmentReminder(appointment);
                System.out.println("📧 24-hour reminder sent for appointment ID: " + appointment.getId());
            }
            
            if (!upcomingAppointments.isEmpty()) {
                System.out.println("✅ Sent " + upcomingAppointments.size() + " 24-hour reminders");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error sending 24-hour reminders: " + e.getMessage());
        }
    }

    // 🕐 Run every 5 minutes to check for 15-minute urgent reminders
    @Scheduled(fixedRate = 300000) // 5 minutes = 300,000 ms
    public void send15MinuteUrgentReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime in15Minutes = now.plusMinutes(15);
            
            // Find appointments starting in 10-20 minutes with video room URLs
            List<Appointment> urgentAppointments = appointmentRepo.findAll().stream()
                .filter(apt -> apt.getAppointmentDate().isAfter(now.plusMinutes(10)) && 
                              apt.getAppointmentDate().isBefore(now.plusMinutes(20)))
                .filter(apt -> "PENDING".equals(apt.getStatus()))
                .filter(apt -> apt.getVideoRoomUrl() != null && !apt.getVideoRoomUrl().isEmpty())
                .toList();
            
            for (Appointment appointment : urgentAppointments) {
                emailService.sendUrgentCallReminder(appointment, appointment.getVideoRoomUrl());
                System.out.println("🚨 15-minute urgent reminder sent for appointment ID: " + appointment.getId());
            }
            
            if (!urgentAppointments.isEmpty()) {
                System.out.println("⚡ Sent " + urgentAppointments.size() + " urgent 15-minute reminders");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error sending 15-minute urgent reminders: " + e.getMessage());
        }
    }
}

// Don't forget to add @EnableScheduling to your main Spring Boot application class:
/*
@SpringBootApplication
@EnableScheduling  // Add this annotation
public class TelemedApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelemedApplication.class, args);
    }
}
*/
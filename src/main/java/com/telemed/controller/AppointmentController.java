package com.telemed.controller;

import com.telemed.dto.AppointmentRequestDTO;
import com.telemed.dto.AppointmentResponseDTO;
import com.telemed.dto.AppointmentStatusUpdateDTO;
import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.security.SecurityUtils;
import com.telemed.service.AppointmentService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.telemed.service.EmailService;


@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepository repo;
    private final DoctorRepository doctorRepo;
    
    
    private final EmailService emailService; // Add this field


    public AppointmentController(AppointmentService service, AppointmentRepository repo ,  DoctorRepository doctorRepo , EmailService emailService) {
        this.service = service;
        this.repo = repo;
        this.doctorRepo = doctorRepo;
        this.emailService = emailService;
    }

    // 1. Book a new appointment
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")

    public AppointmentResponseDTO book(@RequestBody @Valid AppointmentRequestDTO dto) {
        return service.bookAppointment(dto);
    }

    // 2. View all appointments (optional: restrict to ROLE_ADMIN later)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")

    public List<AppointmentResponseDTO> getAll() {
        return service.getAllAppointments();
    }
    
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")

    public List<AppointmentResponseDTO> getByDoctor(@PathVariable Long doctorId) {
        return service.getAppointmentsByDoctor(doctorId);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")

    public List<AppointmentResponseDTO> getByPatient(@PathVariable Long patientId) {
        return service.getAppointmentsByPatient(patientId);
    }

    @GetMapping("/status")
    public List<AppointmentResponseDTO> getByStatus(@RequestParam String status) {
        return service.getAppointmentsByStatus(status);
    }

    @GetMapping("/date")
    public List<AppointmentResponseDTO> getByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getAppointmentsByDate(date);
    }

    
    
  
    
    
    @PatchMapping("/{id}/status")
    public AppointmentResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return service.updateStatus(id, status);
    }
    
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteAppointment(id);
    }
    
    
    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponseDTO> getMyAppointments() {
        String email = SecurityUtils.getCurrentUserEmail();
        return service.getAppointmentsByDoctorEmail(email);
    }
    
    @GetMapping("/doctor/upcoming")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<AppointmentResponseDTO> getDoctorUpcomingAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.getUpcomingAppointmentsForDoctor(email);
    }
    
    @GetMapping("/patient/upcoming")
    @PreAuthorize("hasRole('PATIENT')")
    public List<AppointmentResponseDTO> getPatientUpcomingAppointments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return service.getUpcomingAppointmentsForPatient(email);
    }

    
    
    
    
    @GetMapping("/available-slots")
    @PreAuthorize("hasRole('PATIENT')")
    public List<LocalDateTime> getDoctorAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getAvailableSlotsForDoctor(doctorId, date);
    }
    
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    public void markCompleted(@PathVariable Long id) {
        Appointment appt = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        appt.setStatus("COMPLETED");
        repo.save(appt);
    }
    
    
    
    
    
   

 
     @PostMapping("/{appointmentId}/start-video-call")
     public ResponseEntity<?> startVideoCall(@PathVariable Long appointmentId) {
         try {
             // Find the appointment
             Optional<Appointment> appointmentOpt = repo.findById(appointmentId);
             
             if (appointmentOpt.isEmpty()) {
                 return ResponseEntity.notFound().build();
             }
             
             Appointment appointment = appointmentOpt.get();
             
             // Check if video room URL already exists
             if (appointment.getVideoRoomUrl() != null && !appointment.getVideoRoomUrl().isEmpty()) {
                 // Return existing room URL
                 return ResponseEntity.ok().body(Map.of(
                     "message", "Video call already started",
                     "roomUrl", appointment.getVideoRoomUrl(),
                     "appointmentId", appointmentId
                 ));
             }
             
             // Generate unique room URL
             String roomId = "telemed-appointment-" + appointmentId + "-" + 
                            UUID.randomUUID().toString().substring(0, 8);
             String videoRoomUrl = "https://meet.jit.si/" + roomId;
             
             // Update appointment with video room URL
             appointment.setVideoRoomUrl(videoRoomUrl);
             repo.save(appointment);
             
             // 🎯 PHASE 1: Send immediate notification to patient
             try {
                 emailService.sendVideoCallNotification(appointment, videoRoomUrl);
                 System.out.println("✅ Email notification sent to patient: " + appointment.getPatient().getEmail());
             } catch (Exception emailError) {
                 System.err.println("⚠️ Failed to send email notification: " + emailError.getMessage());
                 // Don't fail the whole request if email fails
             }
             
             return ResponseEntity.ok().body(Map.of(
                 "message", "Video call started successfully and patient notified",
                 "roomUrl", videoRoomUrl,
                 "appointmentId", appointmentId,
                 "patientNotified", true
             ));
             
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Failed to start video call: " + e.getMessage()));
         }
     }
     
     // 🎯 PHASE 2: Optional method for manual reminders
     @PostMapping("/{appointmentId}/send-reminder")
     @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
     public ResponseEntity<?> sendAppointmentReminder(@PathVariable Long appointmentId) {
         try {
             Optional<Appointment> appointmentOpt = repo.findById(appointmentId);
             
             if (appointmentOpt.isEmpty()) {
                 return ResponseEntity.notFound().build();
             }
             
             Appointment appointment = appointmentOpt.get();
             
             // Send reminder email
             emailService.sendAppointmentReminder(appointment);
             
             return ResponseEntity.ok().body(Map.of(
                 "message", "Reminder sent successfully",
                 "patientEmail", appointment.getPatient().getEmail()
             ));
             
         } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", "Failed to send reminder: " + e.getMessage()));
         }
     }
 
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    @PostMapping("/{appointmentId}/start-video-call")
//    public ResponseEntity<?> startVideoCall(@PathVariable Long appointmentId) {
//        try {
//            // Find the appointment
//            Optional<Appointment> appointmentOpt = repo.findById(appointmentId);
//            
//            if (appointmentOpt.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//            
//            Appointment appointment = appointmentOpt.get();
//            
//            // Check if video room URL already exists
//            if (appointment.getVideoRoomUrl() != null && !appointment.getVideoRoomUrl().isEmpty()) {
//                // Return existing room URL
//                return ResponseEntity.ok().body(Map.of(
//                    "message", "Video call already started",
//                    "roomUrl", appointment.getVideoRoomUrl(),
//                    "appointmentId", appointmentId
//                ));
//            }
//            
//            // Generate unique room URL
//            String roomId = "telemed-appointment-" + appointmentId + "-" + 
//                           UUID.randomUUID().toString().substring(0, 8);
//            String videoRoomUrl = "https://meet.jit.si/" + roomId;
//            
//            // Update appointment with video room URL
//            appointment.setVideoRoomUrl(videoRoomUrl);
//            repo.save(appointment);
//            
//            // TODO: Send notification to patient with room URL
//            // For now, just log it
//            System.out.println("Video call started for appointment " + appointmentId + 
//                              ". Room URL: " + videoRoomUrl);
//            
//            return ResponseEntity.ok().body(Map.of(
//                "message", "Video call started successfully",
//                "roomUrl", videoRoomUrl,
//                "appointmentId", appointmentId
//            ));
//            
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                               .body(Map.of("error", "Failed to start video call: " + e.getMessage()));
//        }
//    }
//    
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Add this method to your AppointmentController

    @PutMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, Object>> completeAppointment(@PathVariable Long appointmentId) {
        String doctorEmail = SecurityUtils.getCurrentUserEmail();

        Doctor doctor = doctorRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        Appointment appointment = repo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        // Ensure this doctor is the one who handled the appointment
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only complete your own appointments");
        }

        // Check if appointment is already completed
        if ("COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment is already completed");
        }

        // Mark appointment as completed
        appointment.setStatus("COMPLETED");
        repo.save(appointment);

        // Prepare response with appointment and patient details for prescription screen
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Appointment completed successfully");
        response.put("appointmentId", appointment.getId());
        response.put("patientName", appointment.getPatient().getName());
        response.put("patientId", appointment.getPatient().getId());
        response.put("doctorName", doctor.getName());
        response.put("appointmentDate", appointment.getAppointmentDate());

        return ResponseEntity.ok(response);
    }



    
    
   


}

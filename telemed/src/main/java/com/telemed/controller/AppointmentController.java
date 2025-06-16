package com.telemed.controller;

import com.telemed.dto.AppointmentRequestDTO;
import com.telemed.dto.AppointmentResponseDTO;
import com.telemed.dto.AppointmentStatusUpdateDTO;
import com.telemed.model.Appointment;
import com.telemed.repository.AppointmentRepository;
import com.telemed.security.SecurityUtils;
import com.telemed.service.AppointmentService;
import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepository repo;


    public AppointmentController(AppointmentService service, AppointmentRepository repo) {
        this.service = service;
        this.repo = repo;
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



    
   


}

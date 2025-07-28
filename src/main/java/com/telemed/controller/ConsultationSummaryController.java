package com.telemed.controller;

import com.telemed.model.Appointment;
import com.telemed.model.ConsultationSummary;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.ConsultationSummaryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/summaries")
public class ConsultationSummaryController {

    private final ConsultationSummaryRepository summaryRepo;
    private final AppointmentRepository appointmentRepo;

    public ConsultationSummaryController(ConsultationSummaryRepository summaryRepo, AppointmentRepository appointmentRepo) {
        this.summaryRepo = summaryRepo;
        this.appointmentRepo = appointmentRepo;
    }

    @GetMapping("/by-appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<ConsultationSummary>> getSummariesForAppointment(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        List<ConsultationSummary> summaries = summaryRepo.findByAppointment(appointment);
        return ResponseEntity.ok(summaries);
    }
}

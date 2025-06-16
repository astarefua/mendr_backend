package com.telemed.controller;

import com.telemed.dto.PrescriptionDTO;
import com.telemed.service.PrescriptionService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService service;

    public PrescriptionController(PrescriptionService service) {
        this.service = service;
    }

    @PostMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public PrescriptionDTO create(@PathVariable Long appointmentId, @RequestBody PrescriptionDTO dto) {
        return service.createPrescription(appointmentId, dto);
    }

    @GetMapping("/me/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<PrescriptionDTO> getMyPrescriptionsAsDoctor() {
        return service.getMyPrescriptionsAsDoctor();
    }

    @GetMapping("/me/patient")
    @PreAuthorize("hasRole('PATIENT')")
    public List<PrescriptionDTO> getMyPrescriptionsAsPatient() {
        return service.getMyPrescriptionsAsPatient();
    }
    
    @GetMapping("/{appointmentId}/prescription/pdf")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ByteArrayResource> downloadPdf(@PathVariable Long appointmentId) {
        return service.downloadPrescriptionPdf(appointmentId);
    }

}

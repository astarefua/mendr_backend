package com.telemed.controller;

import com.telemed.dto.SmartMedicationGuideDTO;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.SmartMedicationGuideRepository;
import com.telemed.service.SmartMedicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medication-guides")
public class SmartMedicationController {

    private final SmartMedicationService service;
    private final PatientRepository patientRepo;
    private final SmartMedicationGuideRepository medicationRepo;
    public SmartMedicationController(SmartMedicationService service,PatientRepository patientRepo,SmartMedicationGuideRepository medicationRepo) {
        this.service = service;
        this.patientRepo = patientRepo;
        this.medicationRepo=medicationRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<SmartMedicationGuide>> getAll() {
        return ResponseEntity.ok(service.getAllGuides());
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> addGuide(@RequestBody SmartMedicationGuide guide) {
        service.addGuide(guide);
        return ResponseEntity.ok("Guide added successfully.");
    }
    
    
 // Controller method
    @PostMapping("/guides")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> createGuide(@RequestBody SmartMedicationGuideDTO dto) {
        Patient patient = patientRepo.findById(dto.patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        SmartMedicationGuide guide = new SmartMedicationGuide();
        guide.setPatient(patient);
        guide.setMedicationName(dto.medicationName);
        guide.setVisualDescription(dto.visualDescription);
        guide.setImageUrl(dto.imageUrl);
        guide.setUsageInstruction(dto.usageInstruction);
        guide.setAnimationUrl(dto.animationUrl);
        guide.setDosesPerDay(dto.dosesPerDay);
        guide.setTotalDays(dto.totalDays);
        guide.setStartDate(dto.startDate);

        medicationRepo.save(guide);

        return ResponseEntity.ok("Medication guide assigned to patient successfully.");
    }

    
    
    
    
}

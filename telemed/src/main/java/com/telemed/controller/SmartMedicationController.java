// SmartMedicationController.java
package com.telemed.controller;

import com.telemed.dto.SmartMedicationGuideDTO;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;
import com.telemed.model.User;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.SmartMedicationGuideRepository;
import com.telemed.repository.UserRepository;
import com.telemed.service.SmartMedicationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medication-guides")
public class SmartMedicationController {
	
    @Autowired
    private DoctorRepository doctorRepo;
    
    @Autowired
    private UserRepository userRepo;

    private final SmartMedicationService service;
    private final PatientRepository patientRepo;
    private final SmartMedicationGuideRepository medicationRepo;
    
    public SmartMedicationController(SmartMedicationService service, PatientRepository patientRepo, SmartMedicationGuideRepository medicationRepo) {
        this.service = service;
        this.patientRepo = patientRepo;
        this.medicationRepo = medicationRepo;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<SmartMedicationGuide>> getAll() {
        try {
            // Get current user's email from JWT token
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            
            // Find the current user
            User currentUser = userRepo.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Current user not found"));
            
            // Get filtered guides based on user role
            List<SmartMedicationGuide> guides = service.getGuidesForCurrentUser(currentUser);
            
            return ResponseEntity.ok(guides);
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error in getAll(): " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle it
        }
    }
    
    @PostMapping("/guides")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> createGuide(@RequestBody SmartMedicationGuideDTO dto) {
        try {
            // Get current doctor
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = auth.getName();
            
            Doctor currentDoctor = doctorRepo.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            
            Patient patient = patientRepo.findById(dto.patientId)
                    .orElseThrow(() -> new RuntimeException("Patient not found"));

            SmartMedicationGuide guide = new SmartMedicationGuide();
            guide.setPatient(patient);
            guide.setDoctor(currentDoctor);
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
        } catch (Exception e) {
            System.err.println("Error in createGuide(): " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error creating medication guide: " + e.getMessage());
        }
    }
}


















































//
//package com.telemed.controller;
//
//import com.telemed.dto.SmartMedicationGuideDTO;
//import com.telemed.model.Doctor;
//import com.telemed.model.Patient;
//import com.telemed.model.SmartMedicationGuide;
//import com.telemed.model.User;
//import com.telemed.repository.DoctorRepository;
//import com.telemed.repository.PatientRepository;
//import com.telemed.repository.SmartMedicationGuideRepository;
//import com.telemed.repository.UserRepository;
//import com.telemed.service.SmartMedicationService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/medication-guides")
//public class SmartMedicationController {
//	
//    @Autowired
//    private DoctorRepository doctorRepo;
//    
//    @Autowired
//    private UserRepository userRepo; // Add this to get current user
//
//    private final SmartMedicationService service;
//    private final PatientRepository patientRepo;
//    private final SmartMedicationGuideRepository medicationRepo;
//    
//    public SmartMedicationController(SmartMedicationService service, PatientRepository patientRepo, SmartMedicationGuideRepository medicationRepo) {
//        this.service = service;
//        this.patientRepo = patientRepo;
//        this.medicationRepo = medicationRepo;
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
//    public ResponseEntity<List<SmartMedicationGuide>> getAll() {
//        // Get current user's email from JWT token
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserEmail = auth.getName();
//        
//        // Find the current user
//        User currentUser = userRepo.findByEmail(currentUserEmail)
//                .orElseThrow(() -> new RuntimeException("Current user not found"));
//        
//        // Get filtered guides based on user role
//        List<SmartMedicationGuide> guides = service.getGuidesForCurrentUser(currentUser);
//        
//        return ResponseEntity.ok(guides);
//    }
//    
//    @PostMapping("/guides")
//    @PreAuthorize("hasRole('DOCTOR')")
//    public ResponseEntity<String> createGuide(@RequestBody SmartMedicationGuideDTO dto) {
//        // Get current doctor
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String currentUserEmail = auth.getName();
//        
//        Doctor currentDoctor = doctorRepo.findByEmail(currentUserEmail)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//        
//        Patient patient = patientRepo.findById(dto.patientId)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//
//        SmartMedicationGuide guide = new SmartMedicationGuide();
//        guide.setPatient(patient);
//        guide.setDoctor(currentDoctor); // Set the doctor who created this guide
//        guide.setMedicationName(dto.medicationName);
//        guide.setVisualDescription(dto.visualDescription);
//        guide.setImageUrl(dto.imageUrl);
//        guide.setUsageInstruction(dto.usageInstruction);
//        guide.setAnimationUrl(dto.animationUrl);
//        guide.setDosesPerDay(dto.dosesPerDay);
//        guide.setTotalDays(dto.totalDays);
//        guide.setStartDate(dto.startDate);
//
//        medicationRepo.save(guide);
//
//        return ResponseEntity.ok("Medication guide assigned to patient successfully.");
//    }
//}
//
//
//
//
//
//
//



















//package com.telemed.controller;
//
//import com.telemed.dto.SmartMedicationGuideDTO;
//import com.telemed.model.Doctor;
//import com.telemed.model.Patient;
//import com.telemed.model.SmartMedicationGuide;
//import com.telemed.repository.DoctorRepository;
//import com.telemed.repository.PatientRepository;
//import com.telemed.repository.SmartMedicationGuideRepository;
//import com.telemed.service.SmartMedicationService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/medication-guides")
//public class SmartMedicationController {
//	
//	  @Autowired
//	    private DoctorRepository doctorRepo;
//
//
//    private final SmartMedicationService service;
//    private final PatientRepository patientRepo;
//    private final SmartMedicationGuideRepository medicationRepo;
//    public SmartMedicationController(SmartMedicationService service,PatientRepository patientRepo,SmartMedicationGuideRepository medicationRepo) {
//        this.service = service;
//        this.patientRepo = patientRepo;
//        this.medicationRepo=medicationRepo;
//    }
//
//    @GetMapping
//    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
//    public ResponseEntity<List<SmartMedicationGuide>> getAll() {
//        return ResponseEntity.ok(service.getAllGuides());
//    }
//
//    @PostMapping
//    @PreAuthorize("hasRole('DOCTOR')")
//    public ResponseEntity<String> addGuide(@RequestBody SmartMedicationGuide guide) {
//        service.addGuide(guide);
//        return ResponseEntity.ok("Guide added successfully.");
//    }
//    
//    
//        
//    
//  //Controller method
//  @PostMapping("/guides")
//    @PreAuthorize("hasRole('DOCTOR')")
//    public ResponseEntity<String> createGuide(@RequestBody SmartMedicationGuideDTO dto) {
//        Patient patient = patientRepo.findById(dto.patientId)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//
//        SmartMedicationGuide guide = new SmartMedicationGuide();
//        guide.setPatient(patient);
//        guide.setMedicationName(dto.medicationName);
//        guide.setVisualDescription(dto.visualDescription);
//        guide.setImageUrl(dto.imageUrl);
//        guide.setUsageInstruction(dto.usageInstruction);
//        guide.setAnimationUrl(dto.animationUrl);
//        guide.setDosesPerDay(dto.dosesPerDay);
//        guide.setTotalDays(dto.totalDays);
//        guide.setStartDate(dto.startDate);
//
//        medicationRepo.save(guide);
//
//        return ResponseEntity.ok("Medication guide assigned to patient successfully.");
//    }
//
//    
//    
//    
//    
//}

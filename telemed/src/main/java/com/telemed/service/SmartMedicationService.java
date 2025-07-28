package com.telemed.service;

import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;
import com.telemed.model.User;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.SmartMedicationGuideRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartMedicationService {

    private final SmartMedicationGuideRepository medicationRepo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;



    public SmartMedicationService(SmartMedicationGuideRepository medicationRepo , PatientRepository patientRepo , DoctorRepository doctorRepo) {
        this.medicationRepo = medicationRepo;
        this.patientRepo=patientRepo;
        this.doctorRepo=doctorRepo;
    }
    
    
    public List<SmartMedicationGuide> getGuidesForCurrentUser(User currentUser) {
        String role = currentUser.getRole();
        
        if ("ROLE_PATIENT".equals(role)) {
            // Patient can only see their own medication guides
            Patient patient = patientRepo.findByEmail(currentUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            return medicationRepo.findByPatient(patient);
            
        } else if ("ROLE_DOCTOR".equals(role)) {
            // Doctor can only see medication guides they created
            Doctor doctor = doctorRepo.findByEmail(currentUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            return medicationRepo.findByDoctor(doctor);
            
        } else {
            // For any other role (like ADMIN), return empty list or handle as needed
            return List.of();
        }
    }
    
    
    /**
     * Get medication guides based on current user's role and identity
     * - Patients: Only see their own guides
     * - Doctors: Only see guides they created
     */
//    public List<SmartMedicationGuide> getGuidesForCurrentUser(User currentUser) {
//        String role = currentUser.getRole();
//        
//        if ("ROLE_PATIENT".equals(role)) {
//            // Patient can only see their own medication guides
//            Patient patient = patientRepo.findByEmail(currentUser.getEmail())
//                    .orElseThrow(() -> new RuntimeException("Patient not found"));
//            return medicationRepo.findByPatient(patient);
//            
//        } else if ("ROLE_DOCTOR".equals(role)) {
//            // Doctor can only see medication guides they created
//            Doctor doctor = doctorRepo.findByEmail(currentUser.getEmail())
//                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
//            return medicationRepo.findByDoctor(doctor);
//            
//        } else {
//            // For any other role (like ADMIN), return empty list or handle as needed
//            return List.of();
//        }
//    }
//    
    
    
    //these 2 violate privacy, will delete soon
  public List<SmartMedicationGuide> getAllGuides() {
       return medicationRepo.findAll();
   }

    public void addGuide(SmartMedicationGuide guide) {
        medicationRepo.save(guide);
    }
}

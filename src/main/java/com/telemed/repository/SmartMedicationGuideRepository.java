package com.telemed.repository;


import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartMedicationGuideRepository extends JpaRepository<SmartMedicationGuide, Long> {

	
	/**
     * Find all medication guides for a specific patient
     * Used when a patient wants to see their own guides
     */
    List<SmartMedicationGuide> findByPatient(Patient patient);
    
    /**
     * Find all medication guides created by a specific doctor
     * Used when a doctor wants to see guides they created
     */
    List<SmartMedicationGuide> findByDoctor(Doctor doctor);
    
    /**
     * Find medication guides for a specific patient created by a specific doctor
     * This could be useful if you want doctors to see guides for their specific patients
     */
    List<SmartMedicationGuide> findByPatientAndDoctor(Patient patient, Doctor doctor);

	

}

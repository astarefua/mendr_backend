package com.telemed.repository;

import com.telemed.model.MedicationAdherence;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationAdherenceRepository extends JpaRepository<MedicationAdherence, Long> {
    
    
    // Get all adherence records for a patient
    List<MedicationAdherence> findByPatient(Patient patient);

    // Get all adherence records for a patient and specific guide
    List<MedicationAdherence> findByPatientAndGuide(Patient patient, SmartMedicationGuide guide);

    // Get adherence records in descending time for smart taken confirmation
    List<MedicationAdherence> findByPatientAndGuideOrderByTakenAtDesc(Patient patient, SmartMedicationGuide guide);
}

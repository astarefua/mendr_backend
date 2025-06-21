package com.telemed.repository;

import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartMedicationGuideRepository extends JpaRepository<SmartMedicationGuide, Long> {
	List<SmartMedicationGuide> findByPatient(Patient patient);

}

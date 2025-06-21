package com.telemed.repository;

import com.telemed.model.Patient;
import com.telemed.model.PreConsultSymptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PreConsultSymptomRepository extends JpaRepository<PreConsultSymptom, Long> {
    Optional<PreConsultSymptom> findTopByPatientOrderBySubmittedAtDesc(Patient patient);
}

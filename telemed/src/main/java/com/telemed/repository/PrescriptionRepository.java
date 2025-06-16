package com.telemed.repository;

import com.telemed.model.Prescription;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatient(Patient patient);
    List<Prescription> findByDoctor(Doctor doctor);
}

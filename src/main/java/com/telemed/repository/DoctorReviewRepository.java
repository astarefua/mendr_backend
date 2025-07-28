package com.telemed.repository;

import com.telemed.model.DoctorReview;
import com.telemed.model.Patient;
import com.telemed.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorReviewRepository extends JpaRepository<DoctorReview, Long> {
    List<DoctorReview> findByDoctor(Doctor doctor);
    Optional<DoctorReview> findByDoctorAndPatient(Doctor doctor, Patient patient);
    List<DoctorReview> findByPatient(Patient patient);


}

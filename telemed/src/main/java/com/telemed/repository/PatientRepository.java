package com.telemed.repository;

import com.telemed.model.Patient;
import com.telemed.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

	    Optional<Patient> findByEmail(String email);

    // We’ll use built-in methods like findById, save, deleteById, etc.
}

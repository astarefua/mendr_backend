package com.telemed.repository;

import com.telemed.model.Doctor;
import com.telemed.model.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

//import java.awt.print.Pageable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // We’ll use built-in methods: findAll(), save(), findById(), deleteById()
	
	

	List<Doctor> findByApproved(boolean approved);
	Optional<Doctor> findByEmail(String email);
    List<Doctor> findByApprovedTrueOrderByReviewsRatingDesc(Pageable pageable);


}

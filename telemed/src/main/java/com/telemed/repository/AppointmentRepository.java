package com.telemed.repository;

import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // You can add custom filters later
	List<Appointment> findByDoctorId(Long doctorId);
	List<Appointment> findByPatientId(Long patientId);
	List<Appointment> findByStatusIgnoreCase(String status);
	
    List<Appointment> findByDoctorAndPatient(Doctor doctor, Patient patient);
    
    // NEW: Only completed appointments
    List<Appointment> findByDoctorAndPatientAndStatus(Doctor doctor, Patient patient, String status);



	

}

package com.telemed.repository;

import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // You can add custom filters later
	List<Appointment> findByDoctorId(Long doctorId);
	List<Appointment> findByPatientId(Long patientId);
	List<Appointment> findByStatusIgnoreCase(String status);
	
    List<Appointment> findByDoctorAndPatient(Doctor doctor, Patient patient);
    
    // NEW: Only completed appointments
    List<Appointment> findByDoctorAndPatientAndStatus(Doctor doctor, Patient patient, String status);
    
    //List<Appointment> findByPatientEmailAndStatus(String email, String status); // ✅ Add this line
    
    
    @Query("SELECT a FROM Appointment a WHERE a.patient.email = :email AND a.status = :status")
    List<Appointment> findByPatientEmailAndStatus(@Param("email") String email, @Param("status") String status);





	

}

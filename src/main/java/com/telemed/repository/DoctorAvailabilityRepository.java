//package com.telemed.repository;
//
//import com.telemed.model.DoctorAvailability;
//import com.telemed.model.Doctor;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.DayOfWeek;
//import java.util.List;
//
//public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
//    List<DoctorAvailability> findByDoctor(Doctor doctor);
//    List<DoctorAvailability> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
//}


package com.telemed.repository;

import com.telemed.model.DoctorAvailability;

import jakarta.transaction.Transactional;

import com.telemed.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctor(Doctor doctor);
    List<DoctorAvailability> findByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
    List<DoctorAvailability> findByDoctorId(Long doctorId); // ✅ this is what you need

    //List<DoctorAvailability> deleteByDoctor(Doctor doctor);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM DoctorAvailability da WHERE da.id = :id AND da.doctor = :doctor")
    void deleteByIdAndDoctor(Long id, Doctor doctor);
    

}

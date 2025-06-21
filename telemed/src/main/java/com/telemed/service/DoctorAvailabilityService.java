package com.telemed.service;
import com.telemed.dto.DoctorAvailabilityRequestDTO;
import com.telemed.model.Doctor;
import com.telemed.model.DoctorAvailability;
import com.telemed.repository.DoctorAvailabilityRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.security.SecurityUtils;

import jakarta.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorAvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepo;
    private final DoctorRepository doctorRepo;

    public DoctorAvailabilityService(DoctorAvailabilityRepository availabilityRepo, DoctorRepository doctorRepo) {
        this.availabilityRepo = availabilityRepo;
        this.doctorRepo = doctorRepo;
    }

    public DoctorAvailabilityRequestDTO updateAvailability(Long id, DoctorAvailabilityRequestDTO dto, String doctorEmail) {
        DoctorAvailability availability = availabilityRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (!availability.getDoctor().getEmail().equalsIgnoreCase(doctorEmail)) {
            throw new AccessDeniedException("Not your availability");
        }

        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());

        return mapToDTO(availabilityRepo.save(availability));
    }
    
    
    @Transactional
    public void deleteAvailabilityById(Long availabilityId) {
        String email = SecurityUtils.getCurrentUserEmail();
        Doctor doctor = doctorRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorAvailability availability = availabilityRepo.findById(availabilityId)
            .orElseThrow(() -> new RuntimeException("Availability not found"));

        // Ensure doctor owns the availability
        if (!availability.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You do not own this availability");
        }

        availabilityRepo.deleteByIdAndDoctor(availabilityId, doctor);
    }


    
    public List<DoctorAvailabilityRequestDTO> getAvailabilityByDoctor(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return availabilityRepo.findByDoctor(doctor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    private DoctorAvailabilityRequestDTO mapToDTO(DoctorAvailability availability) {
        return new DoctorAvailabilityRequestDTO(
        		availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime()
        );
    }
}

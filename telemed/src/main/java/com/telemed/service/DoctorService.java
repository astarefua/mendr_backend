package com.telemed.service;

import com.telemed.dto.DoctorAvailabilityRequestDTO;
import com.telemed.dto.DoctorRequestDTO;
import com.telemed.dto.DoctorResponseDTO;
import com.telemed.model.Doctor;
import com.telemed.model.DoctorAvailability;
import com.telemed.repository.DoctorAvailabilityRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.security.SecurityUtils;

//import io.jsonwebtoken.io.IOException;
import java.io.IOException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoctorService {
	
	@Autowired
    private DoctorAvailabilityRepository availabilityRepo;

    private final DoctorRepository repository;

    public DoctorService(DoctorRepository repository) {
        this.repository = repository;
    }
    
    
    private DoctorResponseDTO mapToDTO(Doctor d) {
        return new DoctorResponseDTO(
            d.getId(),
            d.getName(),
            d.getEmail(),
            d.getSpecialty(),
            d.isApproved(),
            d.getProfilePictureUrl(),
            d.getYearsOfExperience(),
            d.getEducation(),
            d.getCertifications(),
            d.getLanguagesSpoken(),
            d.getAffiliations(),
            d.getBio(),
            d.getReviewsRating()
        );
    }
    
    
    
    public DoctorResponseDTO createDoctor(DoctorRequestDTO dto) {
        Doctor doctor = new Doctor();
        doctor.setName(dto.getName());
        doctor.setEmail(dto.getEmail());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setApproved(false);

        doctor.setProfilePictureUrl(dto.getProfilePictureUrl());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setEducation(dto.getEducation());
        doctor.setCertifications(dto.getCertifications());
        doctor.setLanguagesSpoken(dto.getLanguagesSpoken());
        doctor.setAffiliations(dto.getAffiliations());
        doctor.setBio(dto.getBio());
        doctor.setReviewsRating(dto.getReviewsRating());

        Doctor saved = repository.save(doctor);

        return new DoctorResponseDTO(
            saved.getId(),
            saved.getName(),
            saved.getEmail(),
            saved.getSpecialty(),
            saved.isApproved(),
            saved.getProfilePictureUrl(),
            saved.getYearsOfExperience(),
            saved.getEducation(),
            saved.getCertifications(),
            saved.getLanguagesSpoken(),
            saved.getAffiliations(),
            saved.getBio(),
            saved.getReviewsRating()
        );
    }
    
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO dto, String requesterEmail) {
        Doctor doctor = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        if (!isOwnerOrAdmin(id, requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Update all fields
        doctor.setName(dto.getName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setProfilePictureUrl(dto.getProfilePictureUrl());
        doctor.setYearsOfExperience(dto.getYearsOfExperience());
        doctor.setEducation(dto.getEducation());
        doctor.setCertifications(dto.getCertifications());
        doctor.setLanguagesSpoken(dto.getLanguagesSpoken());
        doctor.setAffiliations(dto.getAffiliations());
        doctor.setBio(dto.getBio());
        doctor.setReviewsRating(dto.getReviewsRating());

        Doctor updated = repository.save(doctor);

        return new DoctorResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getEmail(),
            updated.getSpecialty(),
            updated.isApproved(),
            updated.getProfilePictureUrl(),
            updated.getYearsOfExperience(),
            updated.getEducation(),
            updated.getCertifications(),
            updated.getLanguagesSpoken(),
            updated.getAffiliations(),
            updated.getBio(),
            updated.getReviewsRating()
        );
    }


    
    
    
    
    
    public List<DoctorResponseDTO> getAllDoctors() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor d = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapToDTO(d);
    }
    
    public void deleteDoctor(Long id) {
        repository.deleteById(id);
    }
    
    public List<DoctorResponseDTO> getDoctorsByApprovalStatus(boolean status) {
        return repository.findByApproved(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    
    public DoctorResponseDTO approveDoctor(Long id) {
        Doctor doctor = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setApproved(true);
        Doctor updated = repository.save(doctor);

        return new DoctorResponseDTO(
            updated.getId(),
            updated.getName(),
            updated.getEmail(),
            updated.getSpecialty(),
            updated.isApproved(),
            updated.getProfilePictureUrl(),
            updated.getYearsOfExperience(),
            updated.getEducation(),
            updated.getCertifications(),
            updated.getLanguagesSpoken(),
            updated.getAffiliations(),
            updated.getBio(),
            updated.getReviewsRating()
        );
    }

    

    
    public boolean isOwnerOrAdmin(Long id, String requesterEmail) {
    	Doctor doctor = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        return doctor.getEmail().equals(requesterEmail) || SecurityUtils.hasRole("ROLE_ADMIN");
    }

    
    public DoctorResponseDTO getDoctorByEmail(String email) {
        Doctor doctor = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return mapToDTO(doctor);
    }
    

    
    
    
    public List<DoctorResponseDTO> searchDoctors(String name, String specialty) {
    	System.out.println("Searching doctors with name: " + name + ", specialty: " + specialty);
        return repository.findAll().stream()
                .filter(Doctor::isApproved) // only approved doctors
                .filter(doctor -> name == null || doctor.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(doctor -> specialty == null || doctor.getSpecialty().toLowerCase().contains(specialty.toLowerCase()))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    

    public void setAvailability(String doctorEmail, List<DoctorAvailabilityRequestDTO> availabilityList) {
        Doctor doctor = repository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Clear previous availability first
        availabilityRepo.deleteAll(availabilityRepo.findByDoctor(doctor));

        // Add new availability
        List<DoctorAvailability> availabilities = availabilityList.stream().map(dto -> {
            DoctorAvailability availability = new DoctorAvailability();
            availability.setDoctor(doctor);
            availability.setDayOfWeek(dto.getDayOfWeek());
            availability.setStartTime(dto.getStartTime());
            availability.setEndTime(dto.getEndTime());
            return availability;
        }).toList();

        availabilityRepo.saveAll(availabilities);
    }
    

    
    public List<DoctorAvailabilityRequestDTO> getAvailabilityByDoctorId(Long doctorId) {
        Doctor doctor = repository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return doctor.getAvailabilities().stream()
                .map(av -> new DoctorAvailabilityRequestDTO(
                		av.getId(),
                        av.getDayOfWeek(),
                        av.getStartTime(),
                        av.getEndTime()
                ))
                .collect(Collectors.toList());
    }
    
    public DoctorResponseDTO saveProfilePicture(String doctorEmail, MultipartFile file) {
        Doctor doctor = repository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        try {
            String uploadDir = "uploads/profile-pictures/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);

            // ✅ THIS LINE is inside the try block!
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            doctor.setProfilePictureUrl("/" + uploadDir + filename);
            repository.save(doctor);

            return mapToDTO(doctor);
        } catch (IOException e) {
            // ✅ This handles the checked exception properly
            throw new RuntimeException("Failed to save profile picture", e);
        }
    }

    

    
    



}





    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

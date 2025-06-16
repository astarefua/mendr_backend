package com.telemed.service;

import com.telemed.dto.PatientRequestDTO;
import com.telemed.dto.PatientResponseDTO;
import com.telemed.model.Patient;
import com.telemed.repository.PatientRepository;
import com.telemed.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.telemed.exception.PatientNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public PatientResponseDTO createPatient(PatientRequestDTO dto) {
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setContactNumber(dto.getContactNumber());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactRelationship(dto.getEmergencyContactRelationship());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setProfilePictureUrl(dto.getProfilePictureUrl());

        Patient saved = repository.save(patient);
        return mapToDTO(saved);
    }

    public List<PatientResponseDTO> getAllPatients() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PatientResponseDTO getPatientById(Long id) {
        Patient p = repository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));

        return mapToDTO(p);
    }

    public void deletePatient(Long id) {
        if (!repository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public PatientResponseDTO getCurrentPatient() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return mapToDTO(patient);
    }

    public boolean isOwnerOrAdmin(Long id, String requesterEmail) {
        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        return patient.getEmail().equals(requesterEmail) || SecurityUtils.hasRole("ROLE_ADMIN");
    }
    
    
    public PatientResponseDTO updateCurrentPatient(PatientRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Patient patient = repository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        patient.setName(dto.getName());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setContactNumber(dto.getContactNumber());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactRelationship(dto.getEmergencyContactRelationship());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        patient.setProfilePictureUrl(dto.getProfilePictureUrl());

        Patient updated = repository.save(patient);
        return mapToDTO(updated);
    }
    
    private int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }
    
   
    public PatientResponseDTO saveProfilePicture(String patientEmail, MultipartFile file) {
        Patient patient = repository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        try {
            String uploadDir = "uploads/profile-pictures/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            patient.setProfilePictureUrl("/" + uploadDir + filename);
            repository.save(patient);

            return mapToDTO(patient);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile picture", e);
        }
    }




    private PatientResponseDTO mapToDTO(Patient p) {
        return new PatientResponseDTO(
                p.getId(),
                p.getName(),
                p.getEmail(),
                p.getDateOfBirth(),
                p.getGender(),
                p.getContactNumber(),
                p.getEmergencyContactName(),
                p.getEmergencyContactRelationship(),
                p.getEmergencyContactPhone(),
                p.getProfilePictureUrl(),
                calculateAge(p.getDateOfBirth()) // 👈 inject age here

        );
    }
}

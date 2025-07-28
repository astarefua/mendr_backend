package com.telemed.controller;

import com.telemed.dto.PatientRequestDTO;
import com.telemed.dto.PatientResponseDTO;
import com.telemed.security.SecurityUtils;
import com.telemed.service.PatientService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    @PostMapping
    public PatientResponseDTO create(@RequestBody @Valid PatientRequestDTO dto) {
        return service.createPatient(dto);
    }

    @GetMapping
    public List<PatientResponseDTO> getAll() {
        return service.getAllPatients();
    }
    
    @GetMapping("/{id}")
    public PatientResponseDTO getById(@PathVariable Long id) {
        String requesterEmail = SecurityUtils.getCurrentUserEmail();
        if (!service.isOwnerOrAdmin(id, requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return service.getPatientById(id);
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePatient(id);
    }
    
    
    @GetMapping("/me")
    public PatientResponseDTO getLoggedInPatient() {
        return service.getCurrentPatient();
    }
    
    @PatchMapping("/me")
    public PatientResponseDTO updateMyProfile(@RequestBody @Valid PatientRequestDTO dto) {
        return service.updateCurrentPatient(dto);
    }
    
    
    

    @PatchMapping("/me/profile-picture")
    @PreAuthorize("hasRole('PATIENT')")
    public PatientResponseDTO uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String email = SecurityUtils.getCurrentUserEmail();
        return service.saveProfilePicture(email, file);
    }



}








package com.telemed.controller;

import com.telemed.dto.DoctorAvailabilityRequestDTO;
import com.telemed.dto.DoctorRequestDTO;
import com.telemed.dto.DoctorResponseDTO;
import com.telemed.model.DoctorAvailability;
import com.telemed.security.SecurityUtils;
import com.telemed.service.DoctorAvailabilityService;
import com.telemed.service.DoctorService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService service;
    private final DoctorAvailabilityService availabilityService;

    public DoctorController(DoctorService service ,DoctorAvailabilityService availabilityService) {
        this.service = service;
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public DoctorResponseDTO create(@RequestBody @Valid DoctorRequestDTO dto) {
        return service.createDoctor(dto);
    }
    
    @GetMapping(params = "approved")
    public List<DoctorResponseDTO> getByApprovalStatus(@RequestParam boolean approved) {
        return service.getDoctorsByApprovalStatus(approved);
    }


    @GetMapping
    public List<DoctorResponseDTO> getAll() {
        return service.getAllDoctors();
    }
    
    @GetMapping("/{id}")
    public DoctorResponseDTO getById(@PathVariable Long id) {
        String requesterEmail = SecurityUtils.getCurrentUserEmail();
        if (!service.isOwnerOrAdmin(id, requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return service.getDoctorById(id);
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteDoctor(id);
    }

    @PatchMapping("/{id}/approve")
    public DoctorResponseDTO approve(@PathVariable Long id) {
        return service.approveDoctor(id);
    }
    
    @PatchMapping("/{id}")
    public DoctorResponseDTO updateDoctor(@PathVariable Long id, @RequestBody @Valid DoctorRequestDTO dto) {
        String requesterEmail = SecurityUtils.getCurrentUserEmail();
        return service.updateDoctor(id, dto, requesterEmail);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponseDTO getMyProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        return service.getDoctorByEmail(email);
    }
    
    @GetMapping("/search")
    public List<DoctorResponseDTO> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialty) {
        return service.searchDoctors(name, specialty);
    }
    

    
    @GetMapping("/{id}/availability")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public List<DoctorAvailabilityRequestDTO> getDoctorAvailability(@PathVariable Long id) {
        return service.getAvailabilityByDoctorId(id);
    }
    
    
    //this endpoint creates the availabilities . so dont think that because its patch its for update and that it should be post instead.
    @PatchMapping("/me/availability")
    @PreAuthorize("hasRole('DOCTOR')")
    public void setAvailability(@RequestBody List<DoctorAvailabilityRequestDTO> availabilityList) {
        String email = SecurityUtils.getCurrentUserEmail();
        service.setAvailability(email, availabilityList);
    }

    
    @PatchMapping("/availability/{availabilityId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorAvailabilityRequestDTO updateAvailability(@PathVariable Long availabilityId,
                                                    @RequestBody @Valid DoctorAvailabilityRequestDTO dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        return availabilityService.updateAvailability(availabilityId, dto, email);
    }

    @DeleteMapping("/availability/{availabilityId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public void deleteAvailability(@PathVariable Long availabilityId) {
         availabilityService.deleteAvailabilityById(availabilityId);

    }
    
    
    //patient uses patch and still works so in the future when connecting to the frontend we will see if to use post finally or patch finally
    @PostMapping("/me/upload-profile-picture")
    @PreAuthorize("hasRole('DOCTOR')")
    public DoctorResponseDTO uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        String email = SecurityUtils.getCurrentUserEmail();
        return service.saveProfilePicture(email, file);
    }






    
    


}

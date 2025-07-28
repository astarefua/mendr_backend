package com.telemed.controller;

import com.telemed.dto.AuthRequest;
import com.telemed.dto.AuthResponse;
import com.telemed.dto.RegisterRequestDTO;
import com.telemed.service.AuthService;
//import com.telemed.service.PostMapping;
//import com.telemed.service.RequestParam;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return service.login(request);
    }
    
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDTO dto) {
        return service.register(dto);
    }
    
    
 // Replace your current @PostMapping("/auth/register-with-file") endpoint with this updated version
// this endpoint is for patients only to support picture upload
    @PostMapping("/register-with-file")
    public String registerWithFile(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("name") String name,
            @RequestParam("dateOfBirth") String dateOfBirth,
            @RequestParam("gender") String gender,
            @RequestParam("contactNumber") String contactNumber,
            @RequestParam("emergencyContactName") String emergencyContactName,
            @RequestParam("emergencyContactRelationship") String emergencyContactRelationship,
            @RequestParam("emergencyContactPhone") String emergencyContactPhone,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        
        // Create RegisterRequestDTO and populate it
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        dto.setName(name);
        dto.setGender(gender);
        dto.setContactNumber(contactNumber);
        dto.setEmergencyContactName(emergencyContactName);
        dto.setEmergencyContactRelationship(emergencyContactRelationship);
        dto.setEmergencyContactPhone(emergencyContactPhone);
        
        // Parse the date string to LocalDate (same format that worked before)
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            try {
                // Handle ISO string format from Flutter
                String datePart = dateOfBirth.contains("T") ? dateOfBirth.split("T")[0] : dateOfBirth;
                dto.setDateOfBirth(LocalDate.parse(datePart));
            } catch (Exception e) {
                throw new RuntimeException("Invalid date format: " + dateOfBirth);
            }
        }
        
        return service.registerWithFile(dto, profilePicture);
    }
    
    
    @PostMapping("/register-doctor-with-file")
    public String registerDoctorWithFile(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("name") String name,
            @RequestParam("specialty") String specialty,
            @RequestParam(value = "yearsOfExperience", defaultValue = "0") int yearsOfExperience,
            @RequestParam(value = "education", defaultValue = "") String education,
            @RequestParam(value = "certifications", defaultValue = "") String certifications,
            @RequestParam(value = "languagesSpoken", defaultValue = "") String languagesSpoken,
            @RequestParam(value = "affiliations", defaultValue = "") String affiliations,
            @RequestParam(value = "bio", defaultValue = "") String bio,
            @RequestParam(value = "reviewsRating", defaultValue = "0.0") double reviewsRating,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        
        // Create RegisterRequestDTO and populate it
        RegisterRequestDTO dto = new RegisterRequestDTO();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(role);
        dto.setName(name);
        dto.setSpecialty(specialty);
        dto.setYearsOfExperience(yearsOfExperience);
        dto.setEducation(education);
        dto.setCertifications(certifications);
        dto.setLanguagesSpoken(languagesSpoken);
        dto.setAffiliations(affiliations);
        dto.setBio(bio);
        dto.setReviewsRating(reviewsRating);
        
        return service.registerDoctorWithFile(dto, profilePicture);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Add this method to your existing controller class

//    @PostMapping("/register-doctor-with-file")
//    public String registerDoctorWithFile(
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("role") String role,
//            @RequestParam("name") String name,
//            @RequestParam("specialty") String specialty,
//            @RequestParam(value = "yearsOfExperience", required = false) String yearsOfExperience,
//            @RequestParam(value = "education", required = false) String education,
//            @RequestParam(value = "certifications", required = false) String certifications,
//            @RequestParam(value = "languagesSpoken", required = false) String languagesSpoken,
//            @RequestParam(value = "affiliations", required = false) String affiliations,
//            @RequestParam(value = "bio", required = false) String bio,
//            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
//        
//        // Create RegisterRequestDTO and populate it
//        RegisterRequestDTO dto = new RegisterRequestDTO();
//        dto.setEmail(email);
//        dto.setPassword(password);
//        dto.setRole(role);
//        dto.setName(name);
//        dto.setSpecialty(specialty);
//        
//        // Parse optional integer fields safely
//        if (yearsOfExperience != null && !yearsOfExperience.isEmpty()) {
//            try {
//                dto.setYearsOfExperience(Integer.parseInt(yearsOfExperience));
//            } catch (NumberFormatException e) {
//                dto.setYearsOfExperience(0);
//            }
//        }
//        
//        // Set optional string fields
//        dto.setEducation(education != null ? education : "");
//        dto.setCertifications(certifications != null ? certifications : "");
//        dto.setLanguagesSpoken(languagesSpoken != null ? languagesSpoken : "");
//        dto.setAffiliations(affiliations != null ? affiliations : "");
//        dto.setBio(bio != null ? bio : "");
//        dto.setReviewsRating(0.0); // Default rating
//        
//        return service.registerDoctorWithFile(dto, profilePicture);
//    }
//    
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Add this new endpoint to your existing controller alongside your current @PostMapping("/auth/register")

//    @PostMapping("/register-with-file")
//    public String registerWithFile(
//            @RequestParam("email") String email,
//            @RequestParam("password") String password,
//            @RequestParam("role") String role,
//            @RequestParam("name") String name,
//            @RequestParam("dateOfBirth") String dateOfBirth,
//            @RequestParam("gender") String gender,
//            @RequestParam("contactNumber") String contactNumber,
//            @RequestParam("emergencyContactName") String emergencyContactName,
//            @RequestParam("emergencyContactRelationship") String emergencyContactRelationship,
//            @RequestParam("emergencyContactPhone") String emergencyContactPhone,
//            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
//        
//        return service.registerWithFile(email, password, role, name, dateOfBirth, gender, 
//                                      contactNumber, emergencyContactName, emergencyContactRelationship, 
//                                      emergencyContactPhone, profilePicture);
//    }

}

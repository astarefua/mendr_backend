package com.telemed.service;

import com.telemed.dto.AuthRequest;
import com.telemed.dto.AuthResponse;
import com.telemed.dto.RegisterRequestDTO;
import com.telemed.model.Admin;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.User;
import com.telemed.repository.UserRepository;
import com.telemed.security.JWTUtil;
import com.telemed.security.SecurityUtils;

import java.io.File;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;


@Service
public class AuthService {

    @Autowired
    private SystemLogService logService;

    private final UserRepository userRepo;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepo, JWTUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        logService.log("User logged in: " + user.getEmail(), user.getEmail());
        return new AuthResponse(token);
    }

    public String register(RegisterRequestDTO request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String role = "ROLE_" + request.getRole().toUpperCase();

        switch (request.getRole().toLowerCase()) {
            case "patient" -> {
                Patient patient = new Patient();
                patient.setEmail(request.getEmail());
                patient.setPassword(encodedPassword);
                patient.setRole(role);
                patient.setName(request.getName());
                patient.setDateOfBirth(request.getDateOfBirth());
                patient.setGender(request.getGender());
                patient.setContactNumber(request.getContactNumber());
                patient.setEmergencyContactName(request.getEmergencyContactName());
                patient.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
                patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
                patient.setProfilePictureUrl(request.getProfilePictureUrl());

                userRepo.save(patient);
            }
            case "doctor" -> {
                Doctor doctor = new Doctor();
                doctor.setEmail(request.getEmail());
                doctor.setPassword(encodedPassword);
                doctor.setRole(role);
                doctor.setName(request.getName());
                doctor.setSpecialty(request.getSpecialty());
                doctor.setApproved(false); // Initially not approved

                // ✅ Populate extended profile fields
                doctor.setProfilePictureUrl(request.getProfilePictureUrl());
                doctor.setYearsOfExperience(request.getYearsOfExperience());
                doctor.setEducation(request.getEducation());
                doctor.setCertifications(request.getCertifications());
                doctor.setLanguagesSpoken(request.getLanguagesSpoken());
                doctor.setAffiliations(request.getAffiliations());
                doctor.setBio(request.getBio());
                doctor.setReviewsRating(request.getReviewsRating());

                userRepo.save(doctor);
            }
            case "admin" -> {
                Admin admin = new Admin();
                admin.setEmail(request.getEmail());
                admin.setPassword(encodedPassword);
                admin.setRole(role);
                admin.setName(request.getName());
                userRepo.save(admin);
            }
            default -> throw new RuntimeException("Invalid role");
        }

        logService.log("Registered new user: " + request.getEmail(), request.getEmail());
        return "Registration successful for " + request.getRole();
    }
    
 // Replace your current registerWithFile service method with this updated version ( for patients only to support pic upload )

    public String registerWithFile(RegisterRequestDTO request, MultipartFile profilePicture) {
        
        System.out.println("=== REGISTRATION WITH DTO DEBUG START ===");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Name: " + request.getName());
        System.out.println("DateOfBirth: " + request.getDateOfBirth());
        System.out.println("ProfilePicture is null: " + (profilePicture == null));
        
        if (profilePicture != null) {
            System.out.println("ProfilePicture isEmpty: " + profilePicture.isEmpty());
            System.out.println("ProfilePicture original filename: " + profilePicture.getOriginalFilename());
        }
        
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String role = "ROLE_" + request.getRole().toUpperCase();
        String profilePictureUrl = null;

        // Handle profile picture upload if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            System.out.println("*** STARTING FILE UPLOAD ***");
            try {
                String uploadDir = "uploads/profile-pictures/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
                Path path = Paths.get(uploadDir + filename);
                Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                profilePictureUrl = "/" + uploadDir + filename;
                System.out.println("Profile picture URL set to: " + profilePictureUrl);
                
            } catch (IOException e) {
                System.out.println("*** FILE UPLOAD FAILED ***");
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException("Failed to save profile picture", e);
            }
        }

        switch (request.getRole().toLowerCase()) {
            case "patient" -> {
                System.out.println("Creating patient with dateOfBirth: " + request.getDateOfBirth());
                Patient patient = new Patient();
                patient.setEmail(request.getEmail());
                patient.setPassword(encodedPassword);
                patient.setRole(role);
                patient.setName(request.getName());
                patient.setDateOfBirth(request.getDateOfBirth()); // Use DTO's LocalDate directly
                patient.setGender(request.getGender());
                patient.setContactNumber(request.getContactNumber());
                patient.setEmergencyContactName(request.getEmergencyContactName());
                patient.setEmergencyContactRelationship(request.getEmergencyContactRelationship());
                patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
                patient.setProfilePictureUrl(profilePictureUrl);

                Patient savedPatient = userRepo.save(patient);
                System.out.println("Patient saved with ID: " + savedPatient.getId());
                System.out.println("Saved patient dateOfBirth: " + savedPatient.getDateOfBirth());
            }
            case "doctor" -> {
                Doctor doctor = new Doctor();
                doctor.setEmail(request.getEmail());
                doctor.setPassword(encodedPassword);
                doctor.setRole(role);
                doctor.setName(request.getName());
                doctor.setSpecialty(request.getSpecialty());
                doctor.setApproved(false);
                doctor.setProfilePictureUrl(profilePictureUrl);
                // Add other doctor fields as needed
                userRepo.save(doctor);
            }
            case "admin" -> {
                Admin admin = new Admin();
                admin.setEmail(request.getEmail());
                admin.setPassword(encodedPassword);
                admin.setRole(role);
                admin.setName(request.getName());
                userRepo.save(admin);
            }
            default -> throw new RuntimeException("Invalid role");
        }

        logService.log("Registered new user: " + request.getEmail(), request.getEmail());
        System.out.println("=== REGISTRATION WITH DTO DEBUG END ===");
        return "Registration successful for " + request.getRole();
    }
    
    
    
 // Add this method to your AuthService class
    public String registerDoctorWithFile(RegisterRequestDTO request, MultipartFile profilePicture) {
        
        System.out.println("=== DOCTOR REGISTRATION WITH FILE DEBUG START ===");
        System.out.println("Email: " + request.getEmail());
        System.out.println("Name: " + request.getName());
        System.out.println("Specialty: " + request.getSpecialty());
        System.out.println("ProfilePicture is null: " + (profilePicture == null));
        
        if (profilePicture != null) {
            System.out.println("ProfilePicture isEmpty: " + profilePicture.isEmpty());
            System.out.println("ProfilePicture original filename: " + profilePicture.getOriginalFilename());
        }
        
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already taken");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String role = "ROLE_" + request.getRole().toUpperCase();
        String profilePictureUrl = null;

        // Handle profile picture upload if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            System.out.println("*** STARTING DOCTOR FILE UPLOAD ***");
            try {
                String uploadDir = "uploads/profile-pictures/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
                Path path = Paths.get(uploadDir + filename);
                Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                
                profilePictureUrl = "/" + uploadDir + filename;
                System.out.println("Doctor profile picture URL set to: " + profilePictureUrl);
                
            } catch (IOException e) {
                System.out.println("*** DOCTOR FILE UPLOAD FAILED ***");
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException("Failed to save doctor profile picture", e);
            }
        }

        System.out.println("Creating doctor with specialty: " + request.getSpecialty());
        Doctor doctor = new Doctor();
        doctor.setEmail(request.getEmail());
        doctor.setPassword(encodedPassword);
        doctor.setRole(role);
        doctor.setName(request.getName());
        doctor.setSpecialty(request.getSpecialty());
        doctor.setApproved(false); // Initially not approved
        doctor.setProfilePictureUrl(profilePictureUrl);
        
        // Set extended profile fields
        doctor.setYearsOfExperience(request.getYearsOfExperience());
        doctor.setEducation(request.getEducation());
        doctor.setCertifications(request.getCertifications());
        doctor.setLanguagesSpoken(request.getLanguagesSpoken());
        doctor.setAffiliations(request.getAffiliations());
        doctor.setBio(request.getBio());
        doctor.setReviewsRating(request.getReviewsRating());

        Doctor savedDoctor = userRepo.save(doctor);
        System.out.println("Doctor saved with ID: " + savedDoctor.getId());
        System.out.println("Saved doctor specialty: " + savedDoctor.getSpecialty());

        logService.log("Registered new doctor: " + request.getEmail(), request.getEmail());
        System.out.println("=== DOCTOR REGISTRATION WITH FILE DEBUG END ===");
        return "Registration successful for " + request.getRole();
    }

    // Add this controller endpoint to your AuthController class
    
    
    
 // Add this method to your existing service class

//    public String registerDoctorWithFile(RegisterRequestDTO request, MultipartFile profilePicture) {
//        
//        System.out.println("=== DOCTOR REGISTRATION WITH FILE DEBUG START ===");
//        System.out.println("Email: " + request.getEmail());
//        System.out.println("Name: " + request.getName());
//        System.out.println("Specialty: " + request.getSpecialty());
//        System.out.println("ProfilePicture is null: " + (profilePicture == null));
//        
//        if (profilePicture != null) {
//            System.out.println("ProfilePicture isEmpty: " + profilePicture.isEmpty());
//            System.out.println("ProfilePicture original filename: " + profilePicture.getOriginalFilename());
//        }
//        
//        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already taken");
//        }
//
//        String encodedPassword = passwordEncoder.encode(request.getPassword());
//        String role = "ROLE_" + request.getRole().toUpperCase();
//        String profilePictureUrl = null;
//
//        // Handle profile picture upload if provided
//        if (profilePicture != null && !profilePicture.isEmpty()) {
//            System.out.println("*** STARTING DOCTOR FILE UPLOAD ***");
//            try {
//                String uploadDir = "uploads/profile-pictures/";
//                File dir = new File(uploadDir);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
//                Path path = Paths.get(uploadDir + filename);
//                Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                
//                profilePictureUrl = "/" + uploadDir + filename;
//                System.out.println("Doctor profile picture URL set to: " + profilePictureUrl);
//                
//            } catch (IOException e) {
//                System.out.println("*** DOCTOR FILE UPLOAD FAILED ***");
//                System.out.println("Error: " + e.getMessage());
//                throw new RuntimeException("Failed to save doctor profile picture", e);
//            }
//        }
//
//        System.out.println("Creating doctor with specialty: " + request.getSpecialty());
//        Doctor doctor = new Doctor();
//        doctor.setEmail(request.getEmail());
//        doctor.setPassword(encodedPassword);
//        doctor.setRole(role);
//        doctor.setName(request.getName());
//        doctor.setSpecialty(request.getSpecialty());
//        doctor.setYearsOfExperience(request.getYearsOfExperience());
//        doctor.setEducation(request.getEducation());
//        doctor.setCertifications(request.getCertifications());
//        doctor.setLanguagesSpoken(request.getLanguagesSpoken());
//        doctor.setAffiliations(request.getAffiliations());
//        doctor.setBio(request.getBio());
//        doctor.setReviewsRating(request.getReviewsRating());
//        doctor.setApproved(false);
//        doctor.setProfilePictureUrl(profilePictureUrl);
//
//        Doctor savedDoctor = userRepo.save(doctor);
//        System.out.println("Doctor saved with ID: " + savedDoctor.getId());
//
//        logService.log("Registered new doctor: " + request.getEmail(), request.getEmail());
//        System.out.println("=== DOCTOR REGISTRATION WITH FILE DEBUG END ===");
//        return "Doctor registration successful";
//    }
//    
//    
    
    
    
    
    
    
    
    
    
    
    
    
    
 // Replace your registerWithFile method with this version that includes debug logs:

//    public String registerWithFile(String email, String password, String role, String name, 
//                                  String dateOfBirth, String gender, String contactNumber,
//                                  String emergencyContactName, String emergencyContactRelationship,
//                                  String emergencyContactPhone, MultipartFile profilePicture) {
//        
//        System.out.println("=== REGISTRATION DEBUG START ===");
//        System.out.println("Email: " + email);
//        System.out.println("Name: " + name);
//        System.out.println("ProfilePicture is null: " + (profilePicture == null));
//        
//        if (profilePicture != null) {
//            System.out.println("ProfilePicture isEmpty: " + profilePicture.isEmpty());
//            System.out.println("ProfilePicture original filename: " + profilePicture.getOriginalFilename());
//            System.out.println("ProfilePicture size: " + profilePicture.getSize());
//        }
//        
//        if (userRepo.findByEmail(email).isPresent()) {
//            throw new RuntimeException("Email already taken");
//        }
//
//        String encodedPassword = passwordEncoder.encode(password);
//        String roleWithPrefix = "ROLE_" + role.toUpperCase();
//        String profilePictureUrl = null;
//
//        // Handle profile picture upload if provided
//        if (profilePicture != null && !profilePicture.isEmpty()) {
//            System.out.println("*** STARTING FILE UPLOAD ***");
//            try {
//                String uploadDir = "uploads/profile-pictures/";
//                File dir = new File(uploadDir);
//                System.out.println("Upload directory path: " + dir.getAbsolutePath());
//                
//                if (!dir.exists()) {
//                    System.out.println("Directory doesn't exist, creating...");
//                    boolean created = dir.mkdirs();
//                    System.out.println("Directory created: " + created);
//                } else {
//                    System.out.println("Directory already exists");
//                }
//
//                String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
//                Path path = Paths.get(uploadDir + filename);
//                System.out.println("Full file path: " + path.toAbsolutePath());
//                
//                Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("*** FILE COPIED SUCCESSFULLY ***");
//                
//                profilePictureUrl = "/" + uploadDir + filename;
//                System.out.println("Profile picture URL set to: " + profilePictureUrl);
//                
//            } catch (IOException e) {
//                System.out.println("*** FILE UPLOAD FAILED ***");
//                System.out.println("Error: " + e.getMessage());
//                e.printStackTrace();
//                throw new RuntimeException("Failed to save profile picture", e);
//            }
//        } else {
//            System.out.println("*** NO FILE TO UPLOAD - SKIPPING FILE UPLOAD ***");
//        }
//
//        switch (role.toLowerCase()) {
//            case "patient" -> {
//                System.out.println("Creating patient with profilePictureUrl: " + profilePictureUrl);
//                Patient patient = new Patient();
//                patient.setEmail(email);
//                patient.setPassword(encodedPassword);
//                patient.setRole(roleWithPrefix);
//                patient.setName(name);
//                
//                if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
//                    System.out.println("Raw dateOfBirth received: '" + dateOfBirth + "'");
//                    String datePart = dateOfBirth.split("T")[0];
//                    System.out.println("Date part after split: '" + datePart + "'");
//                    patient.setDateOfBirth(LocalDate.parse(datePart));
//                } else {
//                    System.out.println("dateOfBirth is null or empty!");
//                }
//                
////                // Parse dateOfBirth string to LocalDate
////                if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
////                    System.out.println("Parsing dateOfBirth: " + dateOfBirth);
////                    patient.setDateOfBirth(LocalDate.parse(dateOfBirth.split("T")[0])); // Handle ISO string
////                }
//                
//                patient.setGender(gender);
//                patient.setContactNumber(contactNumber);
//                patient.setEmergencyContactName(emergencyContactName);
//                patient.setEmergencyContactRelationship(emergencyContactRelationship);
//                patient.setEmergencyContactPhone(emergencyContactPhone);
//                patient.setProfilePictureUrl(profilePictureUrl);
//
//                Patient savedPatient = userRepo.save(patient);
//                System.out.println("Patient saved with ID: " + savedPatient.getId());
//                System.out.println("Saved patient profilePictureUrl: " + savedPatient.getProfilePictureUrl());
//            }
//            case "doctor" -> {
//                Doctor doctor = new Doctor();
//                doctor.setEmail(email);
//                doctor.setPassword(encodedPassword);
//                doctor.setRole(roleWithPrefix);
//                doctor.setName(name);
//                doctor.setApproved(false);
//                doctor.setProfilePictureUrl(profilePictureUrl);
//                userRepo.save(doctor);
//            }
//            case "admin" -> {
//                Admin admin = new Admin();
//                admin.setEmail(email);
//                admin.setPassword(encodedPassword);
//                admin.setRole(roleWithPrefix);
//                admin.setName(name);
//                userRepo.save(admin);
//            }
//            default -> throw new RuntimeException("Invalid role");
//        }
//
//        logService.log("Registered new user: " + email, email);
//        System.out.println("=== REGISTRATION DEBUG END ===");
//        return "Registration successful for " + role;
//    }
//    
//    
//    
    
    
    
    
    
    
 // Add this method to your existing service class

//    public String registerWithFile(String email, String password, String role, String name, 
//                                  String dateOfBirth, String gender, String contactNumber,
//                                  String emergencyContactName, String emergencyContactRelationship,
//                                  String emergencyContactPhone, MultipartFile profilePicture) {
//        
//        if (userRepo.findByEmail(email).isPresent()) {
//            throw new RuntimeException("Email already taken");
//        }
//
//        String encodedPassword = passwordEncoder.encode(password);
//        String roleWithPrefix = "ROLE_" + role.toUpperCase();
//        String profilePictureUrl = null;
//
//        // Handle profile picture upload if provided
//        if (profilePicture != null && !profilePicture.isEmpty()) {
//            try {
//                String uploadDir = "uploads/profile-pictures/";
//                File dir = new File(uploadDir);
//                if (!dir.exists()) {
//                    dir.mkdirs();
//                }
//
//                String filename = UUID.randomUUID() + "_" + profilePicture.getOriginalFilename();
//                Path path = Paths.get(uploadDir + filename);
//                Files.copy(profilePicture.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//                
//                profilePictureUrl = "/" + uploadDir + filename;
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to save profile picture", e);
//            }
//        }
//
//        switch (role.toLowerCase()) {
//            case "patient" -> {
//                Patient patient = new Patient();
//                patient.setEmail(email);
//                patient.setPassword(encodedPassword);
//                patient.setRole(roleWithPrefix);
//                patient.setName(name);
//                
//                // Parse dateOfBirth string to LocalDate
//                if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
//                    patient.setDateOfBirth(LocalDate.parse(dateOfBirth.split("T")[0])); // Handle ISO string
//                }
//                
//                patient.setGender(gender);
//                patient.setContactNumber(contactNumber);
//                patient.setEmergencyContactName(emergencyContactName);
//                patient.setEmergencyContactRelationship(emergencyContactRelationship);
//                patient.setEmergencyContactPhone(emergencyContactPhone);
//                patient.setProfilePictureUrl(profilePictureUrl);
//
//                userRepo.save(patient);
//            }
//            case "doctor" -> {
//                Doctor doctor = new Doctor();
//                doctor.setEmail(email);
//                doctor.setPassword(encodedPassword);
//                doctor.setRole(roleWithPrefix);
//                doctor.setName(name);
//                doctor.setApproved(false);
//                
//                // For doctors, you might want to add more parameters to handle their specific fields
//                // For now, just setting the profile picture
//                doctor.setProfilePictureUrl(profilePictureUrl);
//
//                userRepo.save(doctor);
//            }
//            case "admin" -> {
//                Admin admin = new Admin();
//                admin.setEmail(email);
//                admin.setPassword(encodedPassword);
//                admin.setRole(roleWithPrefix);
//                admin.setName(name);
//                userRepo.save(admin);
//            }
//            default -> throw new RuntimeException("Invalid role");
//        }
//
//        logService.log("Registered new user: " + email, email);
//        return "Registration successful for " + role;
//    }

    }

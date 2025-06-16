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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
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
}

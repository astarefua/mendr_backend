package com.telemed.dto;

import java.time.LocalDate;

public class RegisterRequestDTO {
    private String email;
    private String password;
    private String role;

    private String name;

    // ✅ Patient-specific fields
    private LocalDate dateOfBirth;
    private String gender;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;

    // ✅ Doctor-specific fields
    private String specialty;
    private String profilePictureUrl;
    private int yearsOfExperience;
    private String education;
    private String certifications;
    private String languagesSpoken;
    private String affiliations;
    private String bio;
    private double reviewsRating;

    // === Getters ===
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public String getContactNumber() { return contactNumber; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }

    public String getSpecialty() { return specialty; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getEducation() { return education; }
    public String getCertifications() { return certifications; }
    public String getLanguagesSpoken() { return languagesSpoken; }
    public String getAffiliations() { return affiliations; }
    public String getBio() { return bio; }
    public double getReviewsRating() { return reviewsRating; }

    // === Setters ===
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String role) { this.role = role; }
    public void setName(String name) { this.name = name; }

    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public void setEducation(String education) { this.education = education; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
    public void setLanguagesSpoken(String languagesSpoken) { this.languagesSpoken = languagesSpoken; }
    public void setAffiliations(String affiliations) { this.affiliations = affiliations; }
    public void setBio(String bio) { this.bio = bio; }
    public void setReviewsRating(double reviewsRating) { this.reviewsRating = reviewsRating; }
}

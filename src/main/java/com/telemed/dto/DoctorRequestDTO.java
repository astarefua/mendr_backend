package com.telemed.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DoctorRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Specialty is required")
    private String specialty;

    private String profilePictureUrl;
    private int yearsOfExperience;
    private String education;
    private String certifications;
    private String languagesSpoken;
    private String affiliations;
    private String bio;
    private double reviewsRating;
   

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getLanguagesSpoken() { return languagesSpoken; }
    public void setLanguagesSpoken(String languagesSpoken) { this.languagesSpoken = languagesSpoken; }

    public String getAffiliations() { return affiliations; }
    public void setAffiliations(String affiliations) { this.affiliations = affiliations; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public double getReviewsRating() { return reviewsRating; }
    public void setReviewsRating(double rating) { this.reviewsRating = reviewsRating; }
    
	
}

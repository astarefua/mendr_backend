package com.telemed.dto;

public class DoctorResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String specialty;
    private boolean approved;

    private String profilePictureUrl;
    private int yearsOfExperience;
    private String education;
    private String certifications;
    private String languagesSpoken;
    private String affiliations;
    private String bio;
    private double reviewsRating;

    public DoctorResponseDTO(Long id, String name, String email, String specialty, boolean approved,
                             String profilePictureUrl, int yearsOfExperience, String education,
                             String certifications, String languagesSpoken, String affiliations,
                             String bio, double reviewsRating) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.approved = approved;
        this.profilePictureUrl = profilePictureUrl;
        this.yearsOfExperience = yearsOfExperience;
        this.education = education;
        this.certifications = certifications;
        this.languagesSpoken = languagesSpoken;
        this.affiliations = affiliations;
        this.bio = bio;
        this.reviewsRating=reviewsRating;
    }

    // Getters only
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSpecialty() { return specialty; }
    public boolean isApproved() { return approved; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getEducation() { return education; }
    public String getCertifications() { return certifications; }
    public String getLanguagesSpoken() { return languagesSpoken; }
    public String getAffiliations() { return affiliations; }
    public String getBio() { return bio; }
    public double getReviewsRating() { return reviewsRating; }
}

package com.telemed.dto;

import java.util.List;

public class DoctorSummaryDTO {
    private Long id;
    private String name;
    private String specialty;
    private String profilePictureUrl;
    private double averageRating;
    private List<String> availableDays;

    public DoctorSummaryDTO() {}

    public DoctorSummaryDTO(Long id, String name, String specialty, String profilePictureUrl, double averageRating, List<String> availableDays) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.profilePictureUrl = profilePictureUrl;
        this.averageRating = averageRating;
        this.availableDays = availableDays;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }
}

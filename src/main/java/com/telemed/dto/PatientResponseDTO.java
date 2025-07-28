package com.telemed.dto;

import java.time.LocalDate;

public class PatientResponseDTO {
    private Long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private String gender;
    private String contactNumber;
    private String emergencyContactName;
    private String emergencyContactRelationship;
    private String emergencyContactPhone;
    private String profilePictureUrl;
    private int age; // ➕ Add this field


    public PatientResponseDTO(Long id, String name, String email, LocalDate dateOfBirth, String gender, String contactNumber, String emergencyContactName, String emergencyContactRelationship, String emergencyContactPhone, String profilePictureUrl,  int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactRelationship = emergencyContactRelationship;
        this.emergencyContactPhone = emergencyContactPhone;
        this.profilePictureUrl = profilePictureUrl;
        this.age = age;

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    
    public int getAge() {
        return age;
    }

}

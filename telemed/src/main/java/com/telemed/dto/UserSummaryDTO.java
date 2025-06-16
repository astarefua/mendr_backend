package com.telemed.dto;

public class UserSummaryDTO {
    private Long id;
    private String email;
    private String role;
    private String name; // From Patient, Doctor, Admin subclasses

    public UserSummaryDTO(Long id, String email, String role, String name) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    // Getters only
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getName() { return name; }
}

package com.telemed.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangeUserRoleDTO {

    @NotBlank(message = "New role is required")
    private String newRole;

    public String getNewRole() {
        return newRole;
    }

    public void setNewRole(String newRole) {
        this.newRole = newRole;
    }
}

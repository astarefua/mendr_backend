package com.telemed.model;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

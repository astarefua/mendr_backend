package com.telemed.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SmartMedicationGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;


    private String medicationName;
    private String visualDescription; // e.g., "red round pill"
    private String imageUrl; // URL to pill photo
    private String usageInstruction; // e.g., "Take with food"
    private String animationUrl; // URL to animation or visual instruction

    private int dosesPerDay; // e.g., 2 times a day
    private int totalDays;    // e.g., for 7 days
    private LocalDate startDate; // when to start taking the medication

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }


    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }

    public String getVisualDescription() { return visualDescription; }
    public void setVisualDescription(String visualDescription) { this.visualDescription = visualDescription; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getUsageInstruction() { return usageInstruction; }
    public void setUsageInstruction(String usageInstruction) { this.usageInstruction = usageInstruction; }

    public String getAnimationUrl() { return animationUrl; }
    public void setAnimationUrl(String animationUrl) { this.animationUrl = animationUrl; }

    public int getDosesPerDay() { return dosesPerDay; }
    public void setDosesPerDay(int dosesPerDay) { this.dosesPerDay = dosesPerDay; }

    public int getTotalDays() { return totalDays; }
    public void setTotalDays(int totalDays) { this.totalDays = totalDays; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}

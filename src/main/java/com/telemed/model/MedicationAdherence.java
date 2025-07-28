package com.telemed.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MedicationAdherence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private SmartMedicationGuide guide;

    private LocalDateTime takenAt;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public SmartMedicationGuide getGuide() { return guide; }
    public void setGuide(SmartMedicationGuide guide) { this.guide = guide; }

    public LocalDateTime getTakenAt() { return takenAt; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
}

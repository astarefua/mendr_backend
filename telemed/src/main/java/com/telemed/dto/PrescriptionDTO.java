package com.telemed.dto;

import java.time.LocalDateTime;

public class PrescriptionDTO {
    private Long id;
    private String medicationName;
    private String dosage;
    private String notes;
    private String doctorName;
    private String patientName;
    private LocalDateTime issuedAt;

    public PrescriptionDTO() {}

    public PrescriptionDTO(Long id, String medicationName, String dosage, String notes, String doctorName, String patientName, LocalDateTime issuedAt) {
        this.id = id;
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.notes = notes;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.issuedAt = issuedAt;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}

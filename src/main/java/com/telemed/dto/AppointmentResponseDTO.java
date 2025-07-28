package com.telemed.dto;

import java.time.LocalDateTime;

public class AppointmentResponseDTO {

    private Long id;
    private LocalDateTime appointmentDate;
    private String status;
    private String doctorName;
    private String patientName;

    public AppointmentResponseDTO(Long id, LocalDateTime appointmentDate, String status, String doctorName, String patientName) {
        this.id = id;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.doctorName = doctorName;
        this.patientName = patientName;
    }

    // Getters
    public Long getId() { return id; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getStatus() { return status; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
}

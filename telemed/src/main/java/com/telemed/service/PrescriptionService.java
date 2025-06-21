package com.telemed.service;

import com.telemed.dto.PrescriptionDTO;
import com.telemed.model.*;
import com.telemed.repository.*;
import com.telemed.security.SecurityUtils;

import io.jsonwebtoken.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
//import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;







@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public PrescriptionService(PrescriptionRepository prescriptionRepo,
                               AppointmentRepository appointmentRepo,
                               DoctorRepository doctorRepo,
                               PatientRepository patientRepo) {
        this.prescriptionRepo = prescriptionRepo;
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    public PrescriptionDTO createPrescription(Long appointmentId, PrescriptionDTO dto) {
        String doctorEmail = SecurityUtils.getCurrentUserEmail();

        Doctor doctor = doctorRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        
        
     // ✅ RESTRICTION: Only allow if appointment is completed
//        if (!"COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Prescription can only be issued after appointment is completed");
//        }
        
        if (!appointment.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Prescription can only be issued after appointment is completed");
        }
        
        
        // ✅ RESTRICTION: Ensure this doctor is the one who handled the appointment
        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only write prescriptions for your own appointments.");
        }

        Prescription prescription = new Prescription();
        prescription.setDoctor(doctor);
        prescription.setPatient(appointment.getPatient());
        prescription.setAppointment(appointment);
        prescription.setMedicationName(dto.getMedicationName());
        prescription.setDosage(dto.getDosage());
        prescription.setNotes(dto.getNotes());

        Prescription saved = prescriptionRepo.save(prescription);

        return mapToDTO(saved);
    }

    public List<PrescriptionDTO> getMyPrescriptionsAsDoctor() {
        String email = SecurityUtils.getCurrentUserEmail();
        Doctor doctor = doctorRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return prescriptionRepo.findByDoctor(doctor).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getMyPrescriptionsAsPatient() {
        String email = SecurityUtils.getCurrentUserEmail();
        Patient patient = patientRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        return prescriptionRepo.findByPatient(patient).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    
    

    public ResponseEntity<ByteArrayResource> downloadPrescriptionPdf(Long appointmentId) {
        String email = SecurityUtils.getCurrentUserEmail();

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        // ✅ Only the patient who booked the appointment can download the prescription
        if (!appointment.getPatient().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to prescription.");
        }

        if (!"COMPLETED".equalsIgnoreCase(appointment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Prescription available only after appointment completion.");
        }

        Prescription prescription = appointment.getPrescription();
        if (prescription == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No prescription found for this appointment.");
        }

        try {
            // Generate PDF content in memory
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Prescription"));
            document.add(new Paragraph("Doctor: " + prescription.getDoctor().getName()));
            document.add(new Paragraph("Patient: " + prescription.getPatient().getName()));
            document.add(new Paragraph("Medication: " + prescription.getMedicationName()));
            document.add(new Paragraph("Dosage: " + prescription.getDosage()));
            document.add(new Paragraph("Notes: " + prescription.getNotes()));
            document.add(new Paragraph("Issued At: " + prescription.getIssuedAt().toString()));
            document.close();

            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(resource.contentLength())
                    .body(resource);

        } catch (DocumentException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate PDF", e);
        }
    }

    
    
    
    
    
 

    private PrescriptionDTO mapToDTO(Prescription p) {
        return new PrescriptionDTO(
                p.getId(),
                p.getMedicationName(),
                p.getDosage(),
                p.getNotes(),
                p.getDoctor().getName(),
                p.getPatient().getName(),
                p.getIssuedAt()
        );
    }
}

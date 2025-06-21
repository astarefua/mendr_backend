package com.telemed.service;

import com.telemed.dto.AppointmentRequestDTO;
import com.telemed.dto.AppointmentResponseDTO;
import com.telemed.dto.DoctorAvailabilityRequestDTO;
import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.model.DoctorAvailability;
import com.telemed.model.Patient;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.DoctorAvailabilityRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.PatientRepository;

//import io.jsonwebtoken.lang.Collections;

import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
	
	@Autowired
	private SystemLogService logService;

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    
    private final DoctorAvailabilityRepository availabilityRepo;
    

    public AppointmentService(AppointmentRepository appointmentRepo,
                              DoctorRepository doctorRepo,
                              PatientRepository patientRepo,
                              SystemLogService systemLogService,
                              DoctorAvailabilityRepository availabilityRepo) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.availabilityRepo = availabilityRepo;
       
    }
    
   
    
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO dto) {
        Doctor doctor = doctorRepo.findById(dto.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Patient patient = patientRepo.findById(dto.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check for overlapping appointments
        LocalDateTime requestedStart = dto.getAppointmentDate();
        LocalDateTime requestedEnd = requestedStart.plusMinutes(30); // assuming 30-min appointment duration

        boolean hasConflict = appointmentRepo.findByDoctorId(doctor.getId()).stream()
            .anyMatch(existing -> {
                LocalDateTime existingStart = existing.getAppointmentDate();
                LocalDateTime existingEnd = existingStart.plusMinutes(30);

                return requestedStart.isBefore(existingEnd) && existingStart.isBefore(requestedEnd);
            });

        if (hasConflict) {
            throw new RuntimeException("This slot is already booked. Please choose another time.");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(requestedStart);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus("PENDING");

        Appointment saved = appointmentRepo.save(appointment);
        logService.log("Appointment booked by patient " + patient.getEmail(), patient.getEmail());

        return mapToDTO(saved);
    }

    
    

    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepo.findByDoctorId(doctorId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByDoctorEmail(String email) {
        return appointmentRepo.findAll().stream()
                .filter(a -> a.getDoctor().getEmail().equalsIgnoreCase(email))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<AppointmentResponseDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepo.findByPatientId(patientId).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<AppointmentResponseDTO> getAppointmentsByStatus(String status) {
        return appointmentRepo.findByStatusIgnoreCase(status).stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public List<AppointmentResponseDTO> getAppointmentsByDate(LocalDate date) {
        return appointmentRepo.findAll().stream()
            .filter(a -> a.getAppointmentDate().toLocalDate().equals(date))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    
    
 

    // ✅ Central method for converting Appointment to DTO
    private AppointmentResponseDTO mapToDTO(Appointment appointment) {
        return new AppointmentResponseDTO(
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getStatus(),
                appointment.getDoctor().getName(),
                appointment.getPatient().getName()
        );
    }
    
    
    
    
    public AppointmentResponseDTO updateStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // from JWT token
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // CASE: PATIENT
        if ("ROLE_PATIENT".equals(role)) {
            if (!status.equalsIgnoreCase("CANCELED")) {
                throw new AccessDeniedException("Patients can only cancel appointments.");
            }
            // Check if this is their own appointment
            if (!appointment.getPatient().getEmail().equals(email)) {
                throw new AccessDeniedException("You can only cancel your own appointment.");
            }
        }

        // CASE: DOCTOR or ADMIN → allow any status update
        else if ("ROLE_DOCTOR".equals(role) || "ROLE_ADMIN".equals(role)) {
            // proceed
        }

        // CASE: Unrecognized role
        else {
            throw new AccessDeniedException("You are not allowed to update appointments.");
        }

        // Finally, update the status
        appointment.setStatus(status.toUpperCase());
        Appointment updated = appointmentRepo.save(appointment);

        return new AppointmentResponseDTO(
            updated.getId(),
            updated.getAppointmentDate(),
            updated.getStatus(),
            updated.getDoctor().getName(),
            updated.getPatient().getName()
        );
    }
    
    
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_PATIENT".equals(role)) {
            if (!appointment.getPatient().getEmail().equals(email)) {
                throw new AccessDeniedException("You can only delete your own appointments.");
            }
        } else if ("ROLE_ADMIN".equals(role)) {
            // Admin can delete anything — allowed
        } else {
            throw new AccessDeniedException("You are not allowed to delete appointments.");
        }

        appointmentRepo.deleteById(id);
        logService.log("Deleted appointment ID " + id, email);

    }
    
    
    
    
    
    public List<AppointmentResponseDTO> getUpcomingAppointmentsForDoctor(String email) {
        LocalDate today = LocalDate.now();

        return appointmentRepo.findAll().stream()
            .filter(a -> a.getDoctor().getEmail().equalsIgnoreCase(email))
            .filter(a -> !a.getAppointmentDate().toLocalDate().isBefore(today))
            .sorted((a1, a2) -> a1.getAppointmentDate().compareTo(a2.getAppointmentDate()))
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }
    
    
    
    public List<LocalDateTime> getAvailableSlotsForDoctor(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DayOfWeek day = date.getDayOfWeek();

        List<DoctorAvailability> availabilityList = availabilityRepo.findByDoctorAndDayOfWeek(doctor, day);
        if (availabilityList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Appointment> bookedAppointments = appointmentRepo.findByDoctorId(doctorId).stream()
                .filter(appt -> appt.getAppointmentDate().toLocalDate().equals(date))
                .collect(Collectors.toList());

        List<LocalDateTime> availableSlots = new ArrayList<>();
        int slotDurationMinutes = 30;

        for (DoctorAvailability availability : availabilityList) {
            LocalTime start = availability.getStartTime();
            LocalTime end = availability.getEndTime();

            LocalDateTime slotStart = date.atTime(start);
            LocalDateTime slotEnd = date.atTime(end);

            while (!slotStart.plusMinutes(slotDurationMinutes).isAfter(slotEnd)) {
                final LocalDateTime currentSlotStart = slotStart;
                final LocalDateTime potentialEnd = currentSlotStart.plusMinutes(slotDurationMinutes);

                boolean isBooked = bookedAppointments.stream().anyMatch(appt -> {
                    LocalDateTime apptStart = appt.getAppointmentDate();
                    LocalDateTime apptEnd = apptStart.plusMinutes(slotDurationMinutes);

                    return currentSlotStart.isBefore(apptEnd) && apptStart.isBefore(potentialEnd);
                });

                if (!isBooked) {
                    availableSlots.add(currentSlotStart);
                }

                slotStart = slotStart.plusMinutes(slotDurationMinutes);
            }

        }

        return availableSlots;
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
}

    
    
    
    

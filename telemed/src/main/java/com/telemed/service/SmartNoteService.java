package com.telemed.service;

import com.telemed.dto.SmartNoteDTO;
import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.model.SmartNote;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.SmartNoteRepository;
import com.telemed.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmartNoteService {

    private final SmartNoteRepository noteRepo;
    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;

    public SmartNoteService(SmartNoteRepository noteRepo, AppointmentRepository appointmentRepo, DoctorRepository doctorRepo) {
        this.noteRepo = noteRepo;
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
    }

    public void addSmartNote(Long appointmentId, SmartNoteDTO dto) {
        String email = SecurityUtils.getCurrentUserEmail();
        Doctor doctor = doctorRepo.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only write notes for your appointments.");
        }

        SmartNote note = new SmartNote();
        note.setSymptom(dto.getSymptom());
        note.setBodyPart(dto.getBodyPart());
        note.setSeverity(dto.getSeverity());
        note.setAction(dto.getAction());
        note.setFollowUp(dto.getFollowUp());
        note.setExtraNotes(dto.getExtraNotes());
        note.setCreatedAt(LocalDateTime.now());
        note.setAppointment(appointment);

        noteRepo.save(note);
    }

    public List<SmartNoteDTO> getNotesForAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return noteRepo.findByAppointment(appointment).stream().map(note -> {
            SmartNoteDTO dto = new SmartNoteDTO();
            dto.setSymptom(note.getSymptom());
            dto.setBodyPart(note.getBodyPart());
            dto.setSeverity(note.getSeverity());
            dto.setAction(note.getAction());
            dto.setFollowUp(note.getFollowUp());
            dto.setExtraNotes(note.getExtraNotes());
            dto.setCreatedAt(note.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }
}

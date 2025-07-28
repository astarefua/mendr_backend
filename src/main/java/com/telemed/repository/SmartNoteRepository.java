package com.telemed.repository;

import com.telemed.model.Appointment;
import com.telemed.model.SmartNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmartNoteRepository extends JpaRepository<SmartNote, Long> {
    List<SmartNote> findByAppointment(Appointment appointment);
}

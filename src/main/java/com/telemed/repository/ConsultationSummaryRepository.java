package com.telemed.repository;

import com.telemed.model.Appointment;
import com.telemed.model.ConsultationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultationSummaryRepository extends JpaRepository<ConsultationSummary, Long> {
    List<ConsultationSummary> findByAppointment(Appointment appointment);
}

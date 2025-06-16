package com.telemed.repository;

import com.telemed.model.VideoSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoSessionRepository extends JpaRepository<VideoSession, Long> {
    // You can add findByDoctorId, findByPatientId, etc. if needed
}

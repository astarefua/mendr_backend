// NotificationHistoryRepository.java
package com.telemed.repository;

import com.telemed.model.NotificationHistory;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    
    @Query("SELECT n FROM NotificationHistory n WHERE n.patient = :patient AND n.guide = :guide " +
           "AND n.scheduledTime = :scheduledTime AND n.notificationType = :type")
    List<NotificationHistory> findByPatientAndGuideAndScheduledTimeAndType(
        @Param("patient") Patient patient, 
        @Param("guide") SmartMedicationGuide guide,
        @Param("scheduledTime") LocalDateTime scheduledTime, 
        @Param("type") String type
    );
    
    List<NotificationHistory> findByPatientOrderBySentAtDesc(Patient patient);
}
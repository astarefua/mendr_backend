// NotificationSettingsRepository.java
package com.telemed.repository;

import com.telemed.model.NotificationSettings;
import com.telemed.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByPatient(Patient patient);
}
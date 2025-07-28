// FCMService.java
package com.telemed.service;

import com.google.firebase.messaging.*;
import com.telemed.model.Patient;
import com.telemed.model.SmartMedicationGuide;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FCMService {

    public boolean sendMedicationReminder(Patient patient, SmartMedicationGuide guide, String timeSlot) {
        if (patient.getFcmToken() == null || patient.getFcmToken().trim().isEmpty()) {
            return false;
        }

        try {
            Message message = Message.builder()
                .setToken(patient.getFcmToken())
                .setNotification(Notification.builder()
                    .setTitle("💊 Time for your medication")
                    .setBody(guide.getMedicationName() + " - " + timeSlot)
                    .build())
                .putData("type", "MEDICATION_REMINDER")
                .putData("guideId", guide.getId().toString())
                .putData("medicationName", guide.getMedicationName())
                .putData("instructions", guide.getUsageInstruction())
                .setAndroidConfig(AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                        .setIcon("ic_medication")
                        .setColor("#2ECC71")
                        .setSound("default")
                        .setPriority(AndroidNotification.Priority.HIGH)
                        .build())
                    .build())
                .setApnsConfig(ApnsConfig.builder()
                    .setAps(Aps.builder()
                        .setSound("default")
                        .setBadge(1)
                        .build())
                    .build())
                .build();

            FirebaseMessaging.getInstance().send(message);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendMissedDoseReminder(Patient patient, SmartMedicationGuide guide) {
        if (patient.getFcmToken() == null || patient.getFcmToken().trim().isEmpty()) {
            return false;
        }

        try {
            Message message = Message.builder()
                .setToken(patient.getFcmToken())
                .setNotification(Notification.builder()
                    .setTitle("⚠️ Missed Medication")
                    .setBody("You missed your " + guide.getMedicationName() + " dose. Tap to confirm if taken.")
                    .build())
                .putData("type", "MISSED_DOSE")
                .putData("guideId", guide.getId().toString())
                .putData("medicationName", guide.getMedicationName())
                .setAndroidConfig(AndroidConfig.builder()
                    .setNotification(AndroidNotification.builder()
                        .setIcon("ic_medication")
                        .setColor("#E74C3C")
                        .setSound("default")
                        .setPriority(AndroidNotification.Priority.HIGH)
                        .build())
                    .build())
                .build();

            FirebaseMessaging.getInstance().send(message);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
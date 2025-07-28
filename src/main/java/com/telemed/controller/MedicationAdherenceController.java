package com.telemed.controller;

import com.telemed.model.MedicationAdherence;
import com.telemed.model.SmartMedicationGuide;
import com.telemed.security.SecurityUtils;
import com.telemed.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medication")
public class MedicationAdherenceController {

    private final VideoService videoService;

    public MedicationAdherenceController(VideoService videoService) {
        this.videoService = videoService;
    }
    
    // ✅ View all medication guides
    @GetMapping("/guides")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<SmartMedicationGuide>> getAllGuides() {
        String email = SecurityUtils.getCurrentUserEmail(); // Get the currently logged-in patient's email
        return ResponseEntity.ok(videoService.getMedicationGuides(email)); // Pass it to the service
    }



    // ✅ Get adherence history
    @GetMapping("/adherence")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<MedicationAdherence>> getAdherenceHistory() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(videoService.getAdherenceHistory(email));
    }
    
    @PostMapping("/confirm/{guideId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> confirmDose(@PathVariable Long guideId) {
        String email = SecurityUtils.getCurrentUserEmail();
        videoService.confirmMedicationTaken(guideId, email);
        return ResponseEntity.ok("Dose confirmed successfully");
    }
    
    
    @GetMapping("/progress/{guideId}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> getProgress(@PathVariable Long guideId) {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(videoService.getAdherenceProgress(guideId, email));
    }
    
    
 // ✅ Get today's due medications
    @GetMapping("/due-today")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<SmartMedicationGuide>> getTodaysDueMedications() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(videoService.getTodayDueDoses(email));
    }

    // ✅ Get calendar of all medication doses by date
    @GetMapping("/calendar")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<LocalDate, List<String>>> getDoseCalendar() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(videoService.getDoseCalendar(email));
    }


    
    
 // 📸 GET pill image by drug name
    @GetMapping("/pill-image")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> getPillImage(@RequestParam String drugName) {
        String imageUrl = videoService.fetchPillImageFromWikimedia(drugName);
        return ResponseEntity.ok(imageUrl);
    }
    
    
}

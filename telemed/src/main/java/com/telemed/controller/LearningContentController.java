package com.telemed.controller;

import com.telemed.model.LearningContent;
import com.telemed.repository.LearningContentRepository;
import com.telemed.service.VideoService;
import com.telemed.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning")
public class LearningContentController {

    private final VideoService videoService;
    private final LearningContentRepository learningRepo;


    public LearningContentController(VideoService videoService , LearningContentRepository learningRepo) {
        this.videoService = videoService;
        this.learningRepo = learningRepo;

    }

    // ✅ Patient submits symptom before consultation
    @PostMapping("/submit-symptom")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> submitSymptom(@RequestParam String symptom) {
        String email = SecurityUtils.getCurrentUserEmail();
        videoService.savePreConsultSymptom(email, symptom);
        return ResponseEntity.ok("Symptom saved for learning context.");
    }

    // ✅ Patient retrieves learning content
    @GetMapping("/content")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<LearningContent>> getLearningContent() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(videoService.getLearningContentForPatient(email));
    }
    
    
 // ✅ Patient (or frontend) can fetch content directly based on symptom
    @GetMapping("/content/by-symptom")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<LearningContent>> getLearningContentBySymptom(@RequestParam String symptom) {
        return ResponseEntity.ok(videoService.getLearningContentBySymptom(symptom));
    }

    
 // Add this inside LearningContentController.java
    @PostMapping("/seed")
    @PreAuthorize("hasRole('PATIENT')") // optional: secure it
    public ResponseEntity<String> seedContent() {
        List<LearningContent> contents = List.of(
            new LearningContent("Understanding Fever", "https://yourcdn.com/videos/fever-explained.mp4", "fever"),
            new LearningContent("Managing Headaches", "https://yourcdn.com/videos/headache-tips.mp4", "headache"),
            new LearningContent("Dealing with Rashes", "https://yourcdn.com/videos/rash-care.mp4", "rash"),
            new LearningContent("Common Cold & Cough", "https://yourcdn.com/videos/cold-cough.mp4", "cough"),
            new LearningContent("Tips for Body Pain Relief", "https://yourcdn.com/videos/pain-relief.mp4", "pain")
        );
        learningRepo.saveAll(contents);
        return ResponseEntity.ok("Learning content seeded.");
    }

}

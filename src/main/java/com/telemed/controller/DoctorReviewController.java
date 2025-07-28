package com.telemed.controller;

import com.telemed.dto.DoctorReviewDTO;
import com.telemed.dto.ReviewAboutDoctorDTO;
import com.telemed.service.DoctorReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class DoctorReviewController {

    private final DoctorReviewService service;

    public DoctorReviewController(DoctorReviewService service) {
        this.service = service;
    }

    @PostMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')")
    public DoctorReviewDTO submitReview(@PathVariable Long doctorId, @RequestBody DoctorReviewDTO dto) {
        return service.leaveReview(doctorId, dto);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DoctorReviewDTO> getDoctorReviews(@PathVariable Long doctorId) {
        return service.getReviewsForDoctor(doctorId);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public List<DoctorReviewDTO> getMyReviews() {
        return service.getMyReviews();
    }
    
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('PATIENT')")
    public List<ReviewAboutDoctorDTO> getReviewsByPatients() {
        return service.getReviewsByPatients();
    }


}

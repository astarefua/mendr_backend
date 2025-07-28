package com.telemed.service;

import com.telemed.dto.DoctorReviewDTO;
import com.telemed.dto.ReviewAboutDoctorDTO;
import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.model.DoctorReview;
import com.telemed.model.Patient;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.DoctorReviewRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.ReviewAboutDoctorRepository;
import com.telemed.security.SecurityUtils;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorReviewService {

    private final DoctorReviewRepository reviewRepo;
    private final ReviewAboutDoctorRepository reviewAboutDoctorRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final AppointmentRepository appointmentRepo;

    public DoctorReviewService(
        DoctorReviewRepository reviewRepo,
        DoctorRepository doctorRepo,
        PatientRepository patientRepo,
        AppointmentRepository appointmentRepo,
        ReviewAboutDoctorRepository reviewAboutDoctorRepo
    ) {
        this.reviewRepo = reviewRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.appointmentRepo = appointmentRepo;
        this.reviewAboutDoctorRepo = reviewAboutDoctorRepo;
    }
    
    public DoctorReviewDTO leaveReview(Long doctorId, DoctorReviewDTO dto) {
        String patientEmail = SecurityUtils.getCurrentUserEmail();

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        Patient patient = patientRepo.findByEmail(patientEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        // ✅ Validate rating range
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        
        
     // ✅ New check for completed appointment
//        List<Appointment> completed = appointmentRepo.findByDoctorAndPatientAndStatus(doctor, patient, "COMPLETED");
//        if (completed.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only review doctors after an appointment.");
//        }
//

        // ✅ Prevent duplicate review
        Optional<DoctorReview> existing = reviewRepo.findByDoctorAndPatient(doctor, patient);
        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this doctor");
        }

        // ✅ Save review
        DoctorReview review = new DoctorReview();
        review.setDoctor(doctor);
        review.setPatient(patient);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        reviewRepo.save(review);

        // ✅ Recalculate average
        List<DoctorReview> reviews = reviewRepo.findByDoctor(doctor);
        double average = reviews.stream().mapToInt(DoctorReview::getRating).average().orElse(0.0);
        doctor.setReviewsRating(average);
        doctorRepo.save(doctor);

        // ✅ Return saved review info
        return new DoctorReviewDTO(review.getRating(), review.getComment(), doctor.getName());
    }

    
    
    
    
    public List<DoctorReviewDTO> getReviewsForDoctor(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        return reviewRepo.findByDoctor(doctor).stream()
                .map(r -> new DoctorReviewDTO(
                        r.getRating(),
                        r.getComment(),
                        r.getPatient().getName()))
                .collect(Collectors.toList());
    }
    
    
    public List<DoctorReviewDTO> getMyReviews() {
        String email = SecurityUtils.getCurrentUserEmail();

        Doctor doctor = doctorRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));

        return reviewRepo.findByDoctor(doctor).stream()
                .map(r -> new DoctorReviewDTO(
                        r.getRating(),
                        r.getComment(),
                        r.getPatient().getName()))
                .collect(Collectors.toList());
    }
    
    public List<ReviewAboutDoctorDTO> getReviewsByPatients() {
        String patientEmail = SecurityUtils.getCurrentUserEmail();

        Patient patient = patientRepo.findByEmail(patientEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        return reviewAboutDoctorRepo.findByPatient(patient).stream()
                .map(r -> new ReviewAboutDoctorDTO(
                        r.getRating(),
                        r.getComment(),
                        r.getDoctor().getName() // Instead of patient name, show doctor name
                ))
                .collect(Collectors.toList());
    }


}




















































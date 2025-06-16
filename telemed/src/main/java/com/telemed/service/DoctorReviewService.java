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
        List<Appointment> completed = appointmentRepo.findByDoctorAndPatientAndStatus(doctor, patient, "COMPLETED");
        if (completed.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only review doctors after an appointment.");
        }

//        // ✅ Ensure the patient had at least one past appointment with this doctor
//        List<Appointment> appointments = appointmentRepo.findByDoctorAndPatient(doctor, patient);
//        boolean hasPastAppointment = appointments.stream()
//                .anyMatch(appt -> appt.getAppointmentDate().isBefore(java.time.LocalDateTime.now()));
//
//        if (!hasPastAppointment) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only review doctors after an appointment.");
//        }

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

    
    
//    public DoctorReviewDTO leaveReview(Long doctorId, DoctorReviewDTO dto) {
//        String patientEmail = SecurityUtils.getCurrentUserEmail();
//
//        Doctor doctor = doctorRepo.findById(doctorId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
//
//        Patient patient = patientRepo.findByEmail(patientEmail)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
//
//        // ✅ 1. Validate rating range
//        if (dto.getRating() < 1 || dto.getRating() > 5) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
//        }
//
//        // ✅ 2. Prevent duplicate reviews
//        Optional<DoctorReview> existing = reviewRepo.findByDoctorAndPatient(doctor, patient);
//        if (existing.isPresent()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this doctor");
//        }
//
//        // ✅ 3. Ensure patient has completed an appointment
//        boolean hasCompletedAppointment = appointmentRepo.findByDoctorAndPatient(doctor, patient)
//                .stream()
//                .anyMatch(a -> a.getStatus().equalsIgnoreCase("COMPLETED"));
//
//        if (!hasCompletedAppointment) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You can only review a doctor after completing an appointment.");
//        }
//
//        // ✅ 4. Save new review
//        DoctorReview review = new DoctorReview();
//        review.setDoctor(doctor);
//        review.setPatient(patient);
//        review.setRating(dto.getRating());
//        review.setComment(dto.getComment());
//        reviewRepo.save(review);
//
//        // ✅ 5. Recalculate and update average rating
//        List<DoctorReview> reviews = reviewRepo.findByDoctor(doctor);
//        double average = reviews.stream().mapToInt(DoctorReview::getRating).average().orElse(0.0);
//        doctor.setReviewsRating(average);
//        doctorRepo.save(doctor);
//
//        return new DoctorReviewDTO(dto.getRating(), dto.getComment(), patient.getName());
//    }
//

//    public DoctorReviewDTO leaveReview(Long doctorId, DoctorReviewDTO dto) {
//        String patientEmail = SecurityUtils.getCurrentUserEmail();
//
//        Doctor doctor = doctorRepo.findById(doctorId)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
//
//        Patient patient = patientRepo.findByEmail(patientEmail)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
//
//        if (dto.getRating() < 1 || dto.getRating() > 5) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
//        }
//
//        Optional<DoctorReview> existing = reviewRepo.findByDoctorAndPatient(doctor, patient);
//        if (existing.isPresent()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already reviewed this doctor");
//        }
//
//        DoctorReview review = new DoctorReview();
//        review.setDoctor(doctor);
//        review.setPatient(patient);
//        review.setRating(dto.getRating());
//        review.setComment(dto.getComment());
//
//        reviewRepo.save(review);
//
//        List<DoctorReview> reviews = reviewRepo.findByDoctor(doctor);
//        double average = reviews.stream().mapToInt(DoctorReview::getRating).average().orElse(0.0);
//        doctor.setReviewsRating(average);
//        doctorRepo.save(doctor);
//
//        return new DoctorReviewDTO(dto.getRating(), dto.getComment(), patient.getName());
//    }
//
    
    
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




















































//package com.telemed.service;
//
//import com.telemed.dto.DoctorReviewDTO;
//import com.telemed.model.Doctor;
//import com.telemed.model.DoctorReview;
//import com.telemed.model.Patient;
//import com.telemed.repository.DoctorRepository;
//import com.telemed.repository.DoctorReviewRepository;
//import com.telemed.repository.PatientRepository;
//import com.telemed.security.SecurityUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class DoctorReviewService {
//
//    private final DoctorReviewRepository reviewRepo;
//    private final DoctorRepository doctorRepo;
//    private final PatientRepository patientRepo;
//
//    public DoctorReviewService(DoctorReviewRepository reviewRepo, DoctorRepository doctorRepo, PatientRepository patientRepo) {
//        this.reviewRepo = reviewRepo;
//        this.doctorRepo = doctorRepo;
//        this.patientRepo = patientRepo;
//    }
//
//    @Transactional
//    public DoctorReviewDTO submitReview(Long doctorId, DoctorReviewDTO dto) {
//        String patientEmail = SecurityUtils.getCurrentUserEmail();
//
//        Doctor doctor = doctorRepo.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//        Patient patient = patientRepo.findByEmail(patientEmail)
//                .orElseThrow(() -> new RuntimeException("Patient not found"));
//
//        DoctorReview review = new DoctorReview();
//        review.setDoctor(doctor);
//        review.setPatient(patient);
//        review.setRating(dto.getRating());
//        review.setComment(dto.getComment());
//
//        reviewRepo.save(review);
//        updateDoctorAverageRating(doctor); // 🔁 update average
//
//        return new DoctorReviewDTO(review.getRating(), review.getComment(), patient.getName());
//    }
//
//    public List<DoctorReviewDTO> getReviewsForDoctor(Long doctorId) {
//        Doctor doctor = doctorRepo.findById(doctorId)
//                .orElseThrow(() -> new RuntimeException("Doctor not found"));
//
//        return reviewRepo.findByDoctor(doctor).stream()
//                .map(r -> new DoctorReviewDTO(r.getRating(), r.getComment(), r.getPatient().getName()))
//                .collect(Collectors.toList());
//    }
//
//    private void updateDoctorAverageRating(Doctor doctor) {
//        List<DoctorReview> reviews = reviewRepo.findByDoctor(doctor);
//        double avg = reviews.stream()
//                .mapToDouble(DoctorReview::getRating)
//                .average()
//                .orElse(0.0);
//
//        doctor.setReviewsRating(avg);
//        doctorRepo.save(doctor);
//    }
//}

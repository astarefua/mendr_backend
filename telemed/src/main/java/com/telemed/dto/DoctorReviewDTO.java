package com.telemed.dto;

//✅ 2. ReviewByPatientDTO — used by doctors

public class DoctorReviewDTO {
    private int rating;
    private String comment;
    private String patientName;

    // Default constructor (needed for JSON deserialization)
    public DoctorReviewDTO() {}

    // Constructor with all fields
    public DoctorReviewDTO(int rating, String comment, String patientName) {
        this.rating = rating;
        this.comment = comment;
        this.patientName = patientName;
    }

    // Getters and Setters
    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}































//package com.telemed.dto;
//
//import java.time.LocalDateTime;
//
//public class DoctorReviewDTO {
//    private Long id;
//    private int rating;
//    private String comment;
//    private String patientName;
//    private LocalDateTime createdAt;
//
//    public DoctorReviewDTO(Long id, int rating, String comment, String patientName, LocalDateTime createdAt) {
//        this.id = id;
//        this.rating = rating;
//        this.comment = comment;
//        this.patientName = patientName;
//        this.createdAt = createdAt;
//    }
//
//    // Getters and Setters
//    public Long getId() { return id; }
//
//    public int getRating() { return rating; }
//    public void setRating(int rating) { this.rating = rating; }
//
//    public String getComment() { return comment; }
//    public void setComment(String comment) { this.comment = comment; }
//
//    public String getPatientName() { return patientName; }
//    public void setPatientName(String patientName) { this.patientName = patientName; }
//
//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//}

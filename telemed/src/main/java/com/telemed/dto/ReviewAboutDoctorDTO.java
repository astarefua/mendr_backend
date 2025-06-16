package com.telemed.dto;

public class ReviewAboutDoctorDTO {
    private int rating;
    private String comment;
    private String doctorName;

    public ReviewAboutDoctorDTO() {}

    public ReviewAboutDoctorDTO(int rating, String comment, String doctorName) {
        this.rating = rating;
        this.comment = comment;
        this.doctorName = doctorName;
    }

    // Getters and setters...
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

    public String getdoctorName() {
        return doctorName;
    }

    public void setdoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

}

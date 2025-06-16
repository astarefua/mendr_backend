package com.telemed.exception;

public class PatientNotFoundException extends RuntimeException {

    public PatientNotFoundException(Long id) {
        super("Patient with ID " + id + " not found");
    }
}

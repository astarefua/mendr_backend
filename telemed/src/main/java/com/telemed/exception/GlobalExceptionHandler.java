package com.telemed.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.security.SignatureException;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
    // ✅ JWT  exceptions or errors
	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<?> handleInvalidJwt(SignatureException ex) {
	    return buildError(HttpStatus.UNAUTHORIZED, "Invalid or tampered token. Please login again.");
	}
	
	
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<?> handleExpiredJwt(ExpiredJwtException ex) {
	    return buildError(HttpStatus.UNAUTHORIZED, "Your session has expired. Please login again.");
	}

	@ExceptionHandler(MalformedJwtException.class)
	public ResponseEntity<?> handleMalformedJwt(MalformedJwtException ex) {
	    return buildError(HttpStatus.BAD_REQUEST, "Invalid token format.");
	}

	@ExceptionHandler(UnsupportedJwtException.class)
	public ResponseEntity<?> handleUnsupportedJwt(UnsupportedJwtException ex) {
	    return buildError(HttpStatus.BAD_REQUEST, "Unsupported token.");
	}


    // ✅ Resource not found — usually from .orElseThrow()
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException ex) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ✅ Bad input like invalid path variable type (/confirm/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
          return buildError(HttpStatus.BAD_REQUEST, "Invalid path variable type: " + ex.getMessage());
    }

    

    // ✅ Invalid date formats
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<?> handleDateParse(DateTimeParseException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid date format: " + ex.getParsedString());
    }

    // ✅ Generic illegal input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid input: " + ex.getMessage());
    }

    // ✅ JSON validation and DTO binding errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildError(HttpStatus.BAD_REQUEST, "Validation error: " + message);
    }

    // ✅ Access denied (wrong role)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return buildError(HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage());
    }

    // ✅ Duplicate key or constraint issues
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDuplicate(DataIntegrityViolationException ex) {
        return buildError(HttpStatus.CONFLICT, "Data conflict: Maybe this email or data already exists.");
    }

    // ✅ Runtime (custom thrown from your service layers)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ✅ Fallback for any unknown errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong: " + ex.getMessage());
    }
    
    

    private ResponseEntity<?> buildError(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("status", status.value());
        body.put("error", message);
        return new ResponseEntity<>(body, status);
    }
}

















































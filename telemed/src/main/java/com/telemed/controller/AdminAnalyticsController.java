package com.telemed.controller;

import com.telemed.dto.AppointmentResponseDTO;
import com.telemed.dto.AppointmentSummaryDTO;
import com.telemed.dto.AppointmentTrendDTO;
import com.telemed.repository.*;
import com.telemed.service.AdminAnalyticsService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {


    
    private final AdminAnalyticsService adminAnalyticsService;

    public AdminAnalyticsController(AdminAnalyticsService adminAnalyticsService) {
        this.adminAnalyticsService = adminAnalyticsService;
    }


    @GetMapping
    public Map<String, Object> getDashboardStats() {
        return adminAnalyticsService.getOverviewStats();
    }
    
    
 // ✅ New trend endpoint
    @GetMapping("/appointments/trend")
    public List<AppointmentTrendDTO> getAppointmentsByDateRange(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return adminAnalyticsService.getAppointmentsGroupedByDate(from, to);
    }
    
    
    @GetMapping("/appointments/summary")
    public AppointmentSummaryDTO getAppointmentSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return adminAnalyticsService.getSummary(from, to);
    }
    
    
    @GetMapping("/appointments/doctor/{doctorId}")
    public List<AppointmentResponseDTO> getByDoctorAndDate(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return adminAnalyticsService.getAppointmentsByDoctorWithinDate(doctorId, from, to);
    }
    
    @GetMapping("/appointments/doctor/{doctorId}/status")
    public List<AppointmentResponseDTO> getByDoctorAndStatus(
            @PathVariable Long doctorId,
            @RequestParam String status) {
        return adminAnalyticsService.getAppointmentsByDoctorAndStatus(doctorId, status);
    }
    
    @GetMapping("/charts/doctor-approval-status")
    public Map<String, Long> getDoctorApprovalChart() {
        return adminAnalyticsService.getDoctorApprovalStats();
    }
    
 // Appointments per hour chart
    @GetMapping("/appointments/hourly")
    public Map<Integer, Long> getAppointmentsByHour() {
        return adminAnalyticsService.getAppointmentsByHour();
    }
    
    
    @GetMapping("/appointments/per-week")
    public Map<String, Long> getAppointmentsPerWeek() {
        return adminAnalyticsService.getAppointmentsPerWeek();
    }
    
    @GetMapping("/doctors/by-specialty")
    public Map<String, Map<String, Long>> getDoctorApprovalStats() {
        return adminAnalyticsService.getDoctorApprovalBySpecialty();
    }
    
    
    @GetMapping("/patients/weekly-registrations")
    public Map<String, Long> getWeeklyPatientStats(@RequestParam(defaultValue = "8") int weeks) {
        return adminAnalyticsService.getWeeklyPatientRegistrations(weeks);
    }
    
    @GetMapping("/appointments/most-booked-doctors")
    public Map<String, Long> getMostBookedDoctors() {
        return adminAnalyticsService.getMostBookedDoctors();
    }










    
    
    
    
    

}


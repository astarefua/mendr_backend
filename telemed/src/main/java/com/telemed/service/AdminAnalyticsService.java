package com.telemed.service;

import com.telemed.repository.DoctorRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.dto.AppointmentResponseDTO;
import com.telemed.dto.AppointmentSummaryDTO;
import com.telemed.dto.AppointmentTrendDTO;
import com.telemed.model.Appointment;
import com.telemed.model.Doctor;
import com.telemed.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class AdminAnalyticsService {

    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentRepository appointmentRepo;

    public AdminAnalyticsService(PatientRepository patientRepo,
                                 DoctorRepository doctorRepo,
                                 AppointmentRepository appointmentRepo) {
        this.patientRepo = patientRepo;
        this.doctorRepo = doctorRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalPatients", patientRepo.count());
        stats.put("totalDoctors", doctorRepo.count());
        stats.put("pendingDoctorApprovals", doctorRepo.findByApproved(false).size());
        stats.put("totalAppointments", appointmentRepo.count());

        Map<String, Long> statusCounts = new HashMap<>();
        appointmentRepo.findAll().forEach(a -> {
            String status = a.getStatus().toUpperCase();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0L) + 1);
        });

        stats.put("appointmentsByStatus", statusCounts);
        return stats;
    }
    
    
 // ✅ New: Appointments grouped by date
       
    public List<AppointmentTrendDTO> getAppointmentsGroupedByDate(LocalDate from, LocalDate to) {
        final LocalDate fromDate = (from == null) ? LocalDate.now().minusDays(30) : from;
        final LocalDate toDate = (to == null) ? LocalDate.now() : to;

        return appointmentRepo.findAll().stream()
            .filter(a -> {
                LocalDate d = a.getAppointmentDate().toLocalDate();
                return (d.isEqual(fromDate) || d.isAfter(fromDate)) &&
                       (d.isEqual(toDate) || d.isBefore(toDate));
            })
            .collect(Collectors.groupingBy(
                a -> a.getAppointmentDate().toLocalDate().toString(), // Group by "YYYY-MM-DD"
                Collectors.counting()
            ))
            .entrySet().stream()
            .map(e -> new AppointmentTrendDTO(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(AppointmentTrendDTO::getDate)) // Optional: sort by date
            .collect(Collectors.toList());
    }
    
    
    public AppointmentSummaryDTO getSummary(LocalDate from, LocalDate to) {
        final LocalDate start = (from == null) ? LocalDate.now().minusDays(30) : from;
        final LocalDate end = (to == null) ? LocalDate.now() : to;

        List<Appointment> list = appointmentRepo.findAll().stream()
            .filter(a -> {
                LocalDate d = a.getAppointmentDate().toLocalDate();
                return (d.isEqual(start) || d.isAfter(start)) &&
                       (d.isEqual(end) || d.isBefore(end));
            }).toList();

        long total = list.size();
        long approved = list.stream().filter(a -> a.getStatus().equalsIgnoreCase("APPROVED")).count();
        long pending = list.stream().filter(a -> a.getStatus().equalsIgnoreCase("PENDING")).count();
        long canceled = list.stream().filter(a -> a.getStatus().equalsIgnoreCase("CANCELED")).count();

        return new AppointmentSummaryDTO(total, approved, pending, canceled);
    }
    
    
    public List<AppointmentResponseDTO> getAppointmentsByDoctorWithinDate(Long doctorId, LocalDate from, LocalDate to) {
        LocalDate start = (from != null) ? from : LocalDate.now().minusDays(30);
        LocalDate end = (to != null) ? to : LocalDate.now();

        return appointmentRepo.findByDoctorId(doctorId).stream()
            .filter(a -> {
                LocalDate date = a.getAppointmentDate().toLocalDate();
                return (date.isEqual(start) || date.isAfter(start)) &&
                       (date.isEqual(end) || date.isBefore(end));
            })
            .map(a -> new AppointmentResponseDTO(
                    a.getId(),
                    a.getAppointmentDate(),
                    a.getStatus(),
                    a.getDoctor().getName(),
                    a.getPatient().getName()
            ))
            .collect(Collectors.toList());
    }
    
    public List<AppointmentResponseDTO> getAppointmentsByDoctorAndStatus(Long doctorId, String status) {
        return appointmentRepo.findByDoctorId(doctorId).stream()
            .filter(a -> a.getStatus().equalsIgnoreCase(status))
            .map(a -> new AppointmentResponseDTO(
                    a.getId(),
                    a.getAppointmentDate(),
                    a.getStatus(),
                    a.getDoctor().getName(),
                    a.getPatient().getName()
            ))
            .collect(Collectors.toList());
    }
    
    public Map<String, Long> getDoctorApprovalStats() {
        return doctorRepo.findAll().stream()
            .collect(Collectors.groupingBy(
                d -> d.isApproved() ? "Approved" : "Pending",
                Collectors.counting()
            ));
    }
    
 // In AdminAnalyticsService.java
    public Map<Integer, Long> getAppointmentsByHour() {
        Map<Integer, Long> hourMap = appointmentRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        a -> a.getAppointmentDate().getHour(),
                        Collectors.counting()
                ));

        // Ensure all 24 hours are present
        Map<Integer, Long> complete = new TreeMap<>();
        for (int i = 0; i < 24; i++) {
            complete.put(i, hourMap.getOrDefault(i, 0L));
        }

        return complete;
    }
    
    public Map<String, Long> getAppointmentsPerWeek() {
        return appointmentRepo.findAll().stream()
            .collect(Collectors.groupingBy(
                a -> {
                    LocalDate date = a.getAppointmentDate().toLocalDate();
                    int week = date.get(java.time.temporal.IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                    int year = date.getYear();
                    return String.format("%d-W%d", year, week);  // e.g., 2025-W22
                },
                TreeMap::new,
                Collectors.counting()
            ));
    }
    
    
    public Map<String, Map<String, Long>> getDoctorApprovalBySpecialty() {
        return doctorRepo.findAll().stream()
            .collect(Collectors.groupingBy(
                Doctor::getSpecialty,
                Collectors.groupingBy(
                    d -> d.isApproved() ? "APPROVED" : "PENDING",
                    Collectors.counting()
                )
            ));
    }
    
    
    public Map<String, Long> getWeeklyPatientRegistrations(int weeksBack) {
        LocalDate today = LocalDate.now();
        LocalDate start = today.minusWeeks(weeksBack);

        return patientRepo.findAll().stream()
            .filter(p -> p.getCreatedAt() != null)
            .filter(p -> {
                LocalDate date = p.getCreatedAt().toLocalDate();
                return !date.isBefore(start);
            })
            .collect(Collectors.groupingBy(
                p -> {
                    LocalDate date = p.getCreatedAt().toLocalDate();
                    return date.with(java.time.DayOfWeek.MONDAY).toString(); // Group by week starting Monday
                },
                Collectors.counting()
            ));
    }
    
    
    public Map<String, Long> getMostBookedDoctors() {
        return appointmentRepo.findAll().stream()
            .collect(Collectors.groupingBy(
                a -> a.getDoctor().getName(),  // or getEmail() or getId()
                Collectors.counting()
            ))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                LinkedHashMap::new // keep sorted order
            ));
    }









    
    
    
    
    
    
    
    
    

} 
    
    
    
    
    



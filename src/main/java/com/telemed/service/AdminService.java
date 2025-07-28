package com.telemed.service;

import com.telemed.dto.AdminRequestDTO;
import com.telemed.dto.AdminResponseDTO;
import com.telemed.dto.DoctorResponseDTO;
import com.telemed.dto.UserSummaryDTO;
import com.telemed.model.Admin;
import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.User;
import com.telemed.repository.AdminRepository;
import com.telemed.repository.AppointmentRepository;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.UserRepository;
import com.telemed.security.SecurityUtils;
import com.telemed.util.CsvExporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {
	
	@Autowired
	private SystemLogService logService;

	
	@Autowired
	private DoctorRepository doctorRepo;
	
	@Autowired
	private AppointmentRepository appointmentRepo;


    private final AdminRepository repository;
    private final UserRepository userRepo; // ✅ NEW

      
    public AdminService(AdminRepository repository, UserRepository userRepo) {
        this.repository = repository;
        this.userRepo = userRepo; // ✅ Inject userRepo
    }

    public AdminResponseDTO createAdmin(AdminRequestDTO dto) {
        Admin admin = new Admin();
        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());

        Admin saved = repository.save(admin);
        return new AdminResponseDTO(saved.getId(), saved.getName(), saved.getEmail());
    }

    public List<AdminResponseDTO> getAllAdmins() {
        return repository.findAll().stream()
                .map(a -> new AdminResponseDTO(a.getId(), a.getName(), a.getEmail()))
                .collect(Collectors.toList());
    }

    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = repository.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
        return new AdminResponseDTO(admin.getId(), admin.getName(), admin.getEmail());
    }

    public void deleteAdmin(Long id) {
        repository.deleteById(id);
    }
    
    public boolean isOwnerOrAdmin(Long id, String requesterEmail) {
    	Admin admin = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        return admin.getEmail().equals(requesterEmail) || SecurityUtils.hasRole("ROLE_ADMIN");
    }
    
   

    
    

    public List<UserSummaryDTO> getAllUsers() {
        return userRepo.findAll().stream()
            .map(user -> {
                String name = null;

                if (user instanceof Patient p) {
                    name = p.getName();
                } else if (user instanceof Doctor d) {
                    name = d.getName();
                } else if (user instanceof Admin a) {
                    name = a.getName();
                }

                return new UserSummaryDTO(
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    name
                );
            })
            .collect(Collectors.toList());
    }
    
    public void changeUserRole(Long userId, String newRole) {
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the new role
        if (!newRole.startsWith("ROLE_")) {
            newRole = "ROLE_" + newRole.toUpperCase();
        }

        user.setRole(newRole);  // Change the role
        userRepo.save(user);    // Save the updated user
        logService.log("Changed role of user ID " + userId + " to " + newRole, SecurityUtils.getCurrentUserEmail());

    }
    
    
    public void deleteUserById(Long userId) {
        if (!userRepo.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        userRepo.deleteById(userId);
        logService.log("Deleted user ID " + userId, SecurityUtils.getCurrentUserEmail());

    }
    
    
    public List<DoctorResponseDTO> getPendingDoctors() {
        return doctorRepo.findByApproved(false).stream()
            .map(d -> new DoctorResponseDTO(
                d.getId(),
                d.getName(),
                d.getEmail(),
                d.getSpecialty(),
                d.isApproved(),
                d.getProfilePictureUrl(),
                d.getYearsOfExperience(),
                d.getEducation(),
                d.getCertifications(),
                d.getLanguagesSpoken(),
                d.getAffiliations(),
                d.getBio(),
                d.getReviewsRating()
            ))
            .collect(Collectors.toList());
    }


    public void approveDoctor(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setApproved(true);
        doctorRepo.save(doctor);
        logService.log("Approved doctor ID " + doctorId, SecurityUtils.getCurrentUserEmail());

    }
    
    
    public String exportAllUsersAsCsv() {
        List<String[]> rows = userRepo.findAll().stream()
            .map(user -> {
                String name = (user instanceof Doctor d) ? d.getName()
                            : (user instanceof Patient p) ? p.getName()
                            : (user instanceof Admin a) ? a.getName()
                            : "Unknown";
                return new String[]{String.valueOf(user.getId()), user.getEmail(), user.getRole(), name};
            })
            .collect(Collectors.toList());

        rows.add(0, new String[]{"ID", "Email", "Role", "Name"}); // Header

        return CsvExporter.toCsv(rows);
    }


    public String exportAppointmentsAsCsv() {
        List<String[]> rows = appointmentRepo.findAll().stream()
            .map(app -> new String[]{
                String.valueOf(app.getId()),
                app.getAppointmentDate().toString(),
                app.getStatus(),
                app.getDoctor().getName(),
                app.getPatient().getName()
            })
            .collect(Collectors.toList());

        rows.add(0, new String[]{"ID", "Date", "Status", "Doctor", "Patient"}); // Header

        return CsvExporter.toCsv(rows);
    }







}

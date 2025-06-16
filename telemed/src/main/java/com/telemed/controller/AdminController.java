package com.telemed.controller;

import com.telemed.dto.AdminRequestDTO;
import com.telemed.dto.AdminResponseDTO;
import com.telemed.dto.ChangeUserRoleDTO;
import com.telemed.dto.DoctorResponseDTO;
import com.telemed.dto.UserSummaryDTO;
import com.telemed.model.SystemLog;
import com.telemed.security.SecurityUtils;
import com.telemed.service.AdminService;
import com.telemed.service.SystemLogService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
	
	@Autowired
	private SystemLogService logService;


    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    @PostMapping
    public AdminResponseDTO create(@RequestBody @Valid AdminRequestDTO dto) {
        return service.createAdmin(dto);
    }

    @GetMapping
    public List<AdminResponseDTO> getAll() {
        return service.getAllAdmins();
    }
    
    @GetMapping("/{id}")
    public AdminResponseDTO getById(@PathVariable Long id) {
        String requesterEmail = SecurityUtils.getCurrentUserEmail();
        if (!service.isOwnerOrAdmin(id, requesterEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return service.getAdminById(id);
    }




    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteAdmin(id);
    }
    
    
    
    
    @PatchMapping("/approve-doctor/{doctorId}")
    public void approveDoctor(@PathVariable Long doctorId) {
        service.approveDoctor(doctorId);
    }
    
    @GetMapping("/pending-doctors")
    public List<DoctorResponseDTO> getPendingDoctors() {
        return service.getPendingDoctors();
    }

    
    

    @GetMapping("/all-users")
    public List<UserSummaryDTO> getAllUsers() {
        return service.getAllUsers();
    }
    
    @PatchMapping("/change-role/{userId}")
    public void changeUserRole(
            @PathVariable Long userId,
            @RequestBody @Valid ChangeUserRoleDTO dto) {
        service.changeUserRole(userId, dto.getNewRole());
    }
    
    @DeleteMapping("/delete-user/{userId}")
    public void deleteAnyUser(@PathVariable Long userId) {
        service.deleteUserById(userId);
    }
    
    @GetMapping("/logs")
    public List<SystemLog> getLogs() {
        return logService.getAllLogs();
    }
    
    
    @GetMapping(value = "/export/users", produces = "text/csv")
    public @ResponseBody String exportUsers() {
        return service.exportAllUsersAsCsv();
    }

    @GetMapping(value = "/export/appointments", produces = "text/csv")
    public @ResponseBody String exportAppointments() {
        return service.exportAppointmentsAsCsv();
    }






}







































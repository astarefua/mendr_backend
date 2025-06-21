package com.telemed.controller;

import com.telemed.dto.SmartNoteDTO;
import com.telemed.service.SmartNoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/smart-notes")
public class SmartNoteController {

    private final SmartNoteService smartNoteService;

    public SmartNoteController(SmartNoteService smartNoteService) {
        this.smartNoteService = smartNoteService;
    }

    @PostMapping("/{appointmentId}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<String> addNote(@PathVariable Long appointmentId, @RequestBody SmartNoteDTO dto) {
        smartNoteService.addSmartNote(appointmentId, dto);
        return ResponseEntity.ok("Note saved");
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<List<SmartNoteDTO>> getNotes(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(smartNoteService.getNotesForAppointment(appointmentId));
    }
}

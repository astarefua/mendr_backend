package com.telemed.controller;

import com.telemed.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }
    
    @PostMapping("/rooms")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<Map<String, String>> createRoom(@RequestParam String roomName,
                                                          @RequestParam String doctorEmail,
                                                          @RequestParam String patientEmail) {
        String url = videoService.createRoom(roomName, doctorEmail, patientEmail);
        return ResponseEntity.ok(Map.of("roomUrl", url));
    }

    @PostMapping("/token")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    public ResponseEntity<Map<String, String>> generateToken(@RequestParam String roomName,
                                                             @RequestParam String userName,
                                                             @RequestParam String userType) {
        String token = videoService.generateToken(roomName, userName, userType);
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    
    
    
    
    
    
    
    

//    @PostMapping("/rooms")
//    @PreAuthorize("hasRole('DOCTOR')")
//    public ResponseEntity<Map<String, String>> createRoom(@RequestParam String roomName) {
//        String url = videoService.createRoom(roomName);
//        return ResponseEntity.ok(Map.of("roomUrl", url));
//    }
//
//    @PostMapping("/token")
//    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
//    public ResponseEntity<Map<String, String>> generateToken(@RequestParam String roomName,
//                                                             @RequestParam String userName,
//                                                             @RequestParam String userType) {
//        String token = videoService.generateToken(roomName, userName, userType);
//        return ResponseEntity.ok(Map.of("token", token));
//    }
}

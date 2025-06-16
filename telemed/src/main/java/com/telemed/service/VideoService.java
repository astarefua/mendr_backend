package com.telemed.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.telemed.model.Doctor;
import com.telemed.model.Patient;
import com.telemed.model.VideoSession;
import com.telemed.repository.DoctorRepository;
import com.telemed.repository.PatientRepository;
import com.telemed.repository.VideoSessionRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoService {
	
	@Value("${daily.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String DAILY_BASE_URL = "https://api.daily.co/v1";

    private final VideoSessionRepository sessionRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;

    public VideoService(VideoSessionRepository sessionRepo, DoctorRepository doctorRepo, PatientRepository patientRepo) {
        this.sessionRepo = sessionRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }

    public String createRoom(String roomName, String doctorEmail, String patientEmail) {
        String url = DAILY_BASE_URL + "/rooms";

        Map<String, Object> body = new HashMap<>();
        body.put("name", roomName);
        body.put("privacy", "private");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String roomUrl = (String) response.getBody().get("url");

            // Save to DB
            Doctor doctor = doctorRepo.findByEmail(doctorEmail).orElse(null);
            Patient patient = patientRepo.findByEmail(patientEmail).orElse(null);

            VideoSession session = new VideoSession();
            session.setRoomName(roomName);
            session.setRoomUrl(roomUrl);
            session.setDoctor(doctor);
            session.setPatient(patient);
            session.setCreatedAt(LocalDateTime.now());
            sessionRepo.save(session);

            return roomUrl;
        } else {
            throw new RuntimeException("Failed to create room: " + response.getStatusCode());
        }
    }
    
    public String generateToken(String roomName, String userName, String userType) {
        String url = DAILY_BASE_URL + "/meeting-tokens";

        Map<String, Object> payload = new HashMap<>();
        payload.put("properties", Map.of(
                "room_name", roomName,
                "user_name", userName,
                "is_owner", userType.equals("doctor")
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return (String) response.getBody().get("token");
        } else {
            throw new RuntimeException("Failed to generate token: " + response.getStatusCode());
        }
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

//    @Value("${daily.api.key}")
//    private String apiKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//    private final String DAILY_BASE_URL = "https://api.daily.co/v1";
//
//    public String createRoom(String roomName) {
//        String url = DAILY_BASE_URL + "/rooms";
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("name", roomName);
//        body.put("privacy", "private");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return (String) response.getBody().get("url");
//        } else {
//            throw new RuntimeException("Failed to create room: " + response.getStatusCode());
//        }
//    }
//    
//    
//    
//    
//    public String generateToken(String roomName, String userName, String userType) {
//        String url = DAILY_BASE_URL + "/meeting-tokens";
//
//        Map<String, Object> properties = new HashMap<>();
//        properties.put("room_name", roomName);
//        properties.put("user_name", userName);
//        properties.put("is_owner", userType.equalsIgnoreCase("doctor"));
//
//        Map<String, Object> body = new HashMap<>();
//        body.put("properties", properties); // ✅ wrap inside "properties"
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
//
//        if (response.getStatusCode().is2xxSuccessful()) {
//            return (String) response.getBody().get("token");
//        } else {
//            throw new RuntimeException("Failed to generate token: " + response.getStatusCode());
//        }
//    }
//

    
    
}



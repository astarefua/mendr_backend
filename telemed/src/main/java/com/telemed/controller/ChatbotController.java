// ✅ ChatbotController.java
package com.telemed.controller;

import com.telemed.service.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final VideoService videoService;

    public ChatbotController(VideoService videoService) {
        this.videoService = videoService;
    }

    // Endpoint for chatbot messages
    @PostMapping("/ask")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<String> askChatbot(@RequestBody ChatMessageRequest request) {
        String response = videoService.getChatbotResponse(request.getMessage());
        return ResponseEntity.ok(response);
    }

    // DTO for incoming chat messages
    public static class ChatMessageRequest {
        private String message;

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}

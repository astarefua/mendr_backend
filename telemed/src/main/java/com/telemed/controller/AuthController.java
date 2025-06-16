package com.telemed.controller;

import com.telemed.dto.AuthRequest;
import com.telemed.dto.AuthResponse;
import com.telemed.dto.RegisterRequestDTO;
import com.telemed.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return service.login(request);
    }
    
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequestDTO dto) {
        return service.register(dto);
    }

}

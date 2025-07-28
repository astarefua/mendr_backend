package com.telemed.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/100ms")
public class FreeVideoController {

    private final String appAccessKey = "YOUR_APP_ACCESS_KEY";
    private final String appSecret = "YOUR_APP_SECRET";

    @GetMapping("/token")
    public Map<String, String> generateToken(@RequestParam String userId, @RequestParam String roomId) {
        long currentTimeMillis = System.currentTimeMillis();
        long expiry = currentTimeMillis + 24 * 60 * 60 * 1000; // 24 hours

        Map<String, Object> payload = new HashMap<>();
        payload.put("access_key", appAccessKey);
        payload.put("type", "app");
        payload.put("version", 2);
        payload.put("room_id", roomId);
        payload.put("user_id", userId);
        payload.put("role", "host");

        String token = Jwts.builder()
                .setClaims(payload)
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(expiry))
                .signWith(SignatureAlgorithm.HS256, appSecret.getBytes())
                .compact();

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return response;
    }
}

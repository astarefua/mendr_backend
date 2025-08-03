package com.telemed.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() throws IOException {
        // Step 1: Load env var
        String firebaseKeyJson = System.getenv("FIREBASE_ADMIN_KEY_JSON");
        if (firebaseKeyJson == null || firebaseKeyJson.isEmpty()) {
            throw new RuntimeException("FIREBASE_ADMIN_KEY_JSON environment variable is not set.");
        }

        // 🔧 Fix escaped newlines in private key
        firebaseKeyJson = firebaseKeyJson.replace("\\n", "\n");
        // 🔍 Debug preview (only log part of it to avoid leaking the full key)
        System.out.println("🔍 Firebase key JSON starts with: " +
                firebaseKeyJson.substring(0, Math.min(100, firebaseKeyJson.length())) + "...");

        // Step 2: Write to /tmp
        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "firebase-admin-key.json");
        Files.write(path, firebaseKeyJson.getBytes(StandardCharsets.UTF_8));
        System.out.println("✅ Firebase key written to: " + path);

        // Step 3: Initialize Firebase
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream(path.toFile());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase has been initialized.");
        }
    }
}

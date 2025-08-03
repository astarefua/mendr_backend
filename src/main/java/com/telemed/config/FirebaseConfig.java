package com.telemed.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.File;
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
        // Step 1: Write the key file to /tmp
        String firebaseKeyJson = System.getenv("FIREBASE_ADMIN_KEY_JSON");
        if (firebaseKeyJson == null || firebaseKeyJson.isEmpty()) {
            throw new RuntimeException("FIREBASE_ADMIN_KEY_JSON environment variable is not set.");
        }

        Path path = Paths.get(System.getProperty("java.io.tmpdir"), "firebase-admin-key.json");
        Files.write(path, firebaseKeyJson.getBytes(StandardCharsets.UTF_8));
        System.out.println("✅ Firebase key written to: " + path);

        // Step 2: Load it and initialize Firebase
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

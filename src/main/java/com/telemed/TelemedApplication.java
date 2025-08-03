package com.telemed;

import com.telemed.model.User;
import com.telemed.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@SpringBootApplication
@EnableScheduling  // Add this annotation

public class TelemedApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelemedApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepo.findByEmail("john@example.com").isEmpty()) {
				User user = new User();
				user.setEmail("john@example.com");
				user.setPassword(passwordEncoder.encode("123456"));
				user.setRole("ROLE_PATIENT");
				userRepo.save(user);
			}
		};
	}


	@PostConstruct
	public void createFirebaseKeyFile() throws IOException {
		String firebaseKeyJson = System.getenv("FIREBASE_ADMIN_KEY_JSON");

		if (firebaseKeyJson != null && !firebaseKeyJson.isEmpty()) {
			Path path = Paths.get(System.getProperty("java.io.tmpdir"), "firebase-admin-key.json");
			Files.write(path, firebaseKeyJson.getBytes(StandardCharsets.UTF_8));
			System.out.println("✅ Firebase key file written to temp directory.");
		} else {
			System.err.println("❌ FIREBASE_ADMIN_KEY_JSON env variable is not set.");
		}
	}
}

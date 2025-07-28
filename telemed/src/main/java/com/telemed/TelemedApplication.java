package com.telemed;

import com.telemed.model.User;
import com.telemed.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
